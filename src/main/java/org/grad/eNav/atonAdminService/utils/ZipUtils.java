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

import jakarta.validation.constraints.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The ZipUtils class.
 * <p/>
 * A helper utility that allows easier zip generation operations.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class ZipUtils {

    /**
     * This utility function can be used to easily zip a file or a folder
     * recursively by adding all its sub-folders and contents. It required
     * the file (or folder) to be zipped, the name of the zipped file and
     * the java ZipOutputStream to provide the output.
     *
     * @param fileToZip the file to be zipped
     * @param fileName the name of the output zipped file
     * @param zipOut the zip output stream
     * @throws IOException for exception in the IO operations
     */
    public static void zipFile(@NotNull File fileToZip,
                               @NotNull String fileName,
                               @NotNull ZipOutputStream zipOut) throws IOException {

        // Do not consider hidden files
        if (fileToZip.isHidden()) {
            return;
        }

        // For directories do a recursive operation
        if (fileToZip.isDirectory()) {
            // Directory file name should always finish with a slash
            final String dirFileName = fileName + (fileName.endsWith("/") ? "" : "/");

            // Add the directory in the zip
            zipOut.putNextEntry(new ZipEntry(dirFileName));
            zipOut.closeEntry();

            // Get all the children in the directory
            final File[] children = Optional.of(fileToZip)
                    .map(File::listFiles)
                    .orElseGet(() -> new File[]{});

            // And act recursively
            for (File childFile : children) {
                zipFile(childFile, dirFileName + childFile.getName(), zipOut);
            }
            return;
        }

        // And for files, read the contents in chunks
        final FileInputStream fis = new FileInputStream(fileToZip);
        final ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

}
