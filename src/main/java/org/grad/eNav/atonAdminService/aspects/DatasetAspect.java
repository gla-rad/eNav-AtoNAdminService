/*
 * Copyright (c) 2024 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonAdminService.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.grad.eNav.atonAdminService.models.domain.s201.S201Dataset;
import org.grad.eNav.atonAdminService.models.enums.DatasetOperation;
import org.grad.eNav.atonAdminService.repos.DatasetRepo;
import org.grad.eNav.atonAdminService.services.DatasetContentLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * The Dataset Aspect Component Class.
 * <p/>
 * This class implements the dataset aspect operations.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Slf4j
@Aspect
@Component
public class DatasetAspect {

    /**
     * The Dataset Service.
     */
    @Autowired
    DatasetRepo datasetRepo;

    /**
     * The Dataset Content Log Service.
     */
    @Autowired
    DatasetContentLogService datasetContentLogService;

    /**
     * The logging dataset operation. This operation will use the output of the
     * logged function to update the database with the resulting dataset (the
     * one named as proceed).
     *
     * @param joinPoint The Joint Point to be used
     * @return the result object of the joint point
     * @throws Throwable for any errors that occurred during the operation
     */
    @Around("@annotation(LogDataset)")
    public Object logDataset(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get the annotation parameters
        final DatasetOperation operation = Optional.of(joinPoint)
                .map(ProceedingJoinPoint::getSignature)
                .map(MethodSignature.class::cast)
                .map(MethodSignature::getMethod)
                .map(m -> m.getAnnotation(LogDataset.class))
                .map(LogDataset::operation)
                .orElse(DatasetOperation.AUTO);

        // Process and get the result object
        final Object proceed = joinPoint.proceed();

        // // Handle if the object whether from an async execution or normal POJO
        this.handleJoinPointProceed(proceed, operation);

        // And return the result object
        return proceed;
    }

    /**
     * The main function for handling the AOP join-point return when this is an
     * S201Dataset of a collection of these
     *
     * @param proceed       The return of the joint-point
     * @param operation     The @LogDataset annotation operation if provided
     */
    protected void handleJoinPointProceed(Object proceed, DatasetOperation operation) {
        // Handle if the object is a CompletableFuture
        if(Optional.ofNullable(proceed).filter(CompletableFuture.class::isInstance).isPresent()) {
            Optional.of(proceed)
                    .map(CompletableFuture.class::cast)
                    .ifPresentOrElse(
                            completableFuture -> completableFuture.thenAcceptAsync((obj) -> this.handleJoinPointProceed(obj, operation)),
                            () -> this.handleJoinPointProceed(proceed, operation)
                    );
        }
        // Handle if the object is an S-201 Dataset
        else if(Optional.ofNullable(proceed).filter(S201Dataset.class::isInstance).isPresent()) {
            Optional.of(proceed)
                    .map(S201Dataset.class::cast)
                    .map(d -> this.datasetContentLogService.generateDatasetContentLog(d, operation))
                    .ifPresent(this.datasetContentLogService::save);
        }
        // Handle if the object is an S-201 Dataset collection
        else if(Optional.ofNullable(proceed).filter(p -> isObjectCollectionOfClass(p, S201Dataset.class)).isPresent()) {
            ((Collection<?>) proceed).stream()
                    .map(S201Dataset.class::cast)
                    .map(d -> this.datasetContentLogService.generateDatasetContentLog(d, operation))
                    .forEach(this.datasetContentLogService::save);
        }
    }

    /**
     * A helper function that can determine whether a generic object is of
     * type collection and includes a specific object class.
     *
     * @param object    The object to be checked
     * @param clazz     The class that the collection should contain
     * @return whether the object is a collection of a specific class
     */
    protected boolean isObjectCollectionOfClass(Object object, Class clazz) {
        if (object instanceof Collection<?>) {
            return ((Collection<?>) object).stream().allMatch(clazz::isInstance);
        }
        return false;
    }

}
