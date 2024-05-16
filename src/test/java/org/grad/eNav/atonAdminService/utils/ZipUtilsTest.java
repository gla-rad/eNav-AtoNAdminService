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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZipUtilsTest {

    // Test Variables
    private ZipOutputStream zipOutputStream;
    private ByteArrayOutputStream byteArrayOutputStream;
    private File testFolder;
    private File testFile;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setup(@TempDir Path tempDir) throws IOException {
        // Initialise the output stream
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.zipOutputStream = new ZipOutputStream(this.byteArrayOutputStream);

        // Create a temp folder
        this.testFolder = tempDir.toFile();
        // Create a temp file
        this.testFile = Files.createFile(tempDir.resolve( "testFile.txt")).toFile();
    }

    /**
     * Test that we can successfully zip a file into a zip output stream.
     */
    @Test
    public void testZipSingleFile() throws IOException {
        // Use the different name for the file
        final String fileName = "qwerty.txt";

        // Perform the zipping operation
        ZipUtils.zipFile(this.testFile, fileName, zipOutputStream);
        this.zipOutputStream.close();

        // Assert that the zip contains one entry with the correct file name
        ZipEntry entry = new ZipEntry(fileName);
        assertZipContainsEntry(this.byteArrayOutputStream, entry);
    }

    /**
     * Test that we can successfully zip a folder into a zip output stream.
     */
    @Test
    public void testZipFolderWithFileInside() throws IOException {
        // Use a different name for the folder -- don't forget the final slash
        final String folderName = "asdfgh/";

        // Perform the zipping operation
        ZipUtils.zipFile(this.testFolder, folderName, this.zipOutputStream);
        this.zipOutputStream.close();

        // Assert that the zip contains two entries, one for the folder and one for the file inside
        ZipEntry folderEntry = new ZipEntry(folderName);
        ZipEntry fileEntry = new ZipEntry(folderName + this.testFile.getName());
        assertZipContainsEntry(this.byteArrayOutputStream, folderEntry);
        assertZipContainsEntry(this.byteArrayOutputStream, fileEntry);
    }

    /**
     * This helper function asserts that the specified zip entry can be found inside
     * the zipped data.
     *
     * @param entry the entry we are looking for in the zipped data
     * @throws IOException for any exceptions in the IO operations
     */
    public static void assertZipContainsEntry(ByteArrayOutputStream byteArrayOutputStream, ZipEntry entry) throws IOException {
        byte[] zipData = byteArrayOutputStream.toByteArray();
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            ZipEntry zipEntry;
            boolean entryFound = false;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().equals(entry.getName())) {
                    entryFound = true;
                    break;
                }
            }
            assertTrue(entryFound, "Expected zip entry not found: " + entry.getName());
        }
    }

}