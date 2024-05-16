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

package org.grad.eNav.atonAdminService.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.grad.eNav.atonAdminService.models.domain.s100.ServiceInformationConfig;
import org.grad.eNav.atonAdminService.models.enums.DatasetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The Home Viewer Controller.
 *
 * This is the home controller that allows user to view the main options.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Controller
public class HTMLViewerController {

    /**
     * The Service Information Config.
     */
    @Autowired
    ServiceInformationConfig serviceInformationConfig;

    /**
     * The home page of the AtoN Service Application.
     *
     * @param model The application UI model
     * @return The index page
     */
    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("endpoints", Arrays.stream(DatasetType.values())
                .map(type -> String.format("%s", type.name()))
                .collect(Collectors.toList()));

        // Add the properties to the UI model
        model.addAttribute("appName", this.serviceInformationConfig.name());
        model.addAttribute("appOperatorUrl", this.serviceInformationConfig.url());
        model.addAttribute("appCopyright", this.serviceInformationConfig.copyright());
        // Return the rendered index
        return "index";
    }

    /**
     * The Datasets page of the AtoN Service Application.
     *
     * @param model The application UI model
     * @return The datasets page
     */
    @GetMapping("/datasets")
    public String datasets(Model model) {
        model.addAttribute("appName", this.serviceInformationConfig.name());
        model.addAttribute("appOperatorUrl", this.serviceInformationConfig.url());
        model.addAttribute("appCopyright", this.serviceInformationConfig.copyright());
        return "datasets";
    }

    /**
     * The Aids to Navigation page of the AtoN Service Application.
     *
     * @param model The application UI model
     * @return The atons page
     */
    @GetMapping("/atons")
    public String atons(Model model) {
        model.addAttribute("appName", this.serviceInformationConfig.name());
        model.addAttribute("appOperatorUrl", this.serviceInformationConfig.url());
        model.addAttribute("appCopyright", this.serviceInformationConfig.copyright());
        return "atons";
    }

    /**
     * The SECOM Subscriptions page of the AtoN Service Application.
     *
     * @param model The application UI model
     * @return The subscriptions page
     */
    @GetMapping("/subscriptions")
    public String subscriptions(Model model) {
        model.addAttribute("appName", this.serviceInformationConfig.name());
        model.addAttribute("appOperatorUrl", this.serviceInformationConfig.url());
        model.addAttribute("appCopyright", this.serviceInformationConfig.copyright());
        return "subscriptions";
    }

    /**
     * The Aids to Navigation page of the AtoN Service Application.
     *
     * @param model The application UI model
     * @return The logs page
     */
    @GetMapping("/logs")
    public String logs(Model model) {
        model.addAttribute("appName", this.serviceInformationConfig.name());
        model.addAttribute("appOperatorUrl", this.serviceInformationConfig.url());
        model.addAttribute("appCopyright", this.serviceInformationConfig.copyright());
        return "datasetContentLogs";
    }

    /**
     * The about page of the AtoN Service Application.
     *
     * @param model The application UI model
     * @return The about page
     */
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("appName", this.serviceInformationConfig.name());
        model.addAttribute("appVersion", this.serviceInformationConfig.version());
        model.addAttribute("appOperatorName", this.serviceInformationConfig.organization());
        model.addAttribute("appOperatorContact", Arrays.toString(this.serviceInformationConfig.electronicMailAddresses().toArray()));
        model.addAttribute("appOperatorUrl", this.serviceInformationConfig.url());
        model.addAttribute("appCopyright", this.serviceInformationConfig.copyright());
        return "about";
    }

    /**
     * Logs the user in an authenticated session and redirect to the home page.
     *
     * @param request The logout request
     * @return The home page
     */
    @GetMapping(path = "/login")
    public ModelAndView login(HttpServletRequest request) {
        return new ModelAndView("redirect:" + "/");
    }

    /**
     * Logs the user out of the authenticated session.
     *
     * @param request The logout request
     * @return The home page
     * @throws ServletException Servlet Exception during the logout
     */
    @GetMapping(path = "/logout")
    public ModelAndView logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return new ModelAndView("redirect:" + "/");
    }

}
