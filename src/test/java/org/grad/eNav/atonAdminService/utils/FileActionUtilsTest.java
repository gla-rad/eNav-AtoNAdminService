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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class FileActionUtilsTest {

    // Test Variables
    Path tempDir;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        this.tempDir = tempDir;
    }

    /**
     * Test that we can successfully create a directory with empty contents.
     */
    @Test
    void testCreateDirectory() throws IOException {
        // Create the test directory
        final File testDir = FileActionUtils.createDirectory(this.tempDir.toFile(), "testDir");

        // Assess the result
        assertNotNull(testDir);
        assertTrue(testDir.exists());
        assertTrue(testDir.isDirectory());
        assertEquals(0, Objects.requireNonNull(testDir.listFiles()).length);
    }

    /**
     * Test that we can successfully create a file with empty contents.
     */
    @Test
    void testCreateFile() throws IOException {
        // Create the test file
        final File testFile = FileActionUtils.createFile(this.tempDir.toFile(), "testFile");

        // Assess the result
        assertNotNull(testFile);
        assertTrue(testFile.exists());
        assertTrue(testFile.isFile());
        assertEquals(0, Files.size(testFile.toPath()));
    }

}