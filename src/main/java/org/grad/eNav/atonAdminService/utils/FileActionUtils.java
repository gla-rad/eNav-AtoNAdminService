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

package org.grad.eNav.atonAdminService.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * The File Action class.
 * <p/>
 * A helper utility that assists with file operations.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class FileActionUtils {

    /**
     * A small helper function that creates a directory to be used in the
     * packaging process.
     *
     * @param path the path to create the directory in
     * @param name the name of the new directory
     * @return the generated directory
     * @throws IOException for an IO exceptions thrown
     */
    public static File createDirectory(File path, String name) throws IOException {
        final File file = new File(path,  name);
        Files.createDirectory(file.toPath());
        return file;
    }

    /**
     * A small helper function that creates a file to be used in the
     * packaging process.
     *
     * @param path the path to create the file in
     * @param name the name of the new file
     * @return the generated file
     * @throws IOException for an IO exceptions thrown
     */
    public static File createFile(File path, String name) throws IOException {
        final File file = new File(path,  name);
        Files.createFile(file.toPath());
        return file;
    }

}
