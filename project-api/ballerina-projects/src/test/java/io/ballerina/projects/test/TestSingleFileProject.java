/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.projects.test;

import io.ballerina.projects.DocumentId;
import io.ballerina.projects.Module;
import io.ballerina.projects.ModuleId;
import io.ballerina.projects.Package;
import io.ballerina.projects.Project;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.directory.ProjectLoader;
import io.ballerina.projects.directory.SingleFileProject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Contains cases to test the basic package structure.
 *
 * @since 2.0.0
 */
public class TestSingleFileProject {
    private static final Path RESOURCE_DIRECTORY = Paths.get("src/test/resources/");

    @Test
    public void testLoadSingleFile() {
        Path projectPath = RESOURCE_DIRECTORY.resolve("single-file").resolve("main.bal");
        SingleFileProject project = null;
        try {
            project = SingleFileProject.loadProject(projectPath);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        // 2) Load the package
        Package currentPackage = project.getPackage();
        // 3) Load the default module
        Module defaultModule = currentPackage.getDefaultModule();
        Assert.assertEquals(defaultModule.documentIds().size(), 1);

        Collection<ModuleId> moduleIds = currentPackage.moduleIds();
        Assert.assertEquals(moduleIds.size(), 1);
        Assert.assertEquals(moduleIds.iterator().next(), currentPackage.getDefaultModule().moduleId());
        Assert.assertTrue(project.target().toFile().exists());
        Assert.assertEquals(project.target().getParent().getParent().toString(), System.getProperty("java.io.tmpdir"));
    }

    // LS project test
    @Test
    public void testLoadProjectByDefaultModuleFile() {
        Path projectPath = RESOURCE_DIRECTORY.resolve("myproject").resolve("main.bal");
        Project project = null;
        try {
            project = ProjectLoader.loadProject(projectPath);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertTrue(project instanceof BuildProject);
        // 2) Load the package
        Package currentPackage = ((BuildProject) project).getPackage();
        // 3) Load the default module
        Module defaultModule = currentPackage.getDefaultModule();
        Assert.assertEquals(defaultModule.documentIds().size(), 2);

        // TODO find an easy way to test the project structure. e.g. serialize the structure in a json file.
        int noOfSrcDocuments = 0;
        int noOfTestDocuments = 0;
        final Collection<ModuleId> moduleIds = currentPackage.moduleIds();
        Assert.assertEquals(moduleIds.size(), 3);
        for (ModuleId moduleId : moduleIds) {
            Module module = currentPackage.module(moduleId);
            for (DocumentId documentId : module.documentIds()) {
                noOfSrcDocuments++;
            }
            for (DocumentId testDocumentId : module.testDocumentIds()) {
                noOfTestDocuments++;
            }
        }

        Assert.assertEquals(noOfSrcDocuments, 4);
        Assert.assertEquals(noOfTestDocuments, 3);

        Assert.assertTrue(project.target().toFile().exists());
        Assert.assertEquals(project.target().getParent(), RESOURCE_DIRECTORY.resolve("myproject"));
    }

    @Test
    public void testLoadProjectByOtherModulesFile() {
        Path projectPath = RESOURCE_DIRECTORY.resolve("myproject").resolve("modules").resolve("services")
                .resolve("svc.bal");
        Project project = null;
        try {
            project = ProjectLoader.loadProject(projectPath);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertTrue(project instanceof BuildProject);
        // 2) Load the package
        Package currentPackage = ((BuildProject) project).getPackage();
        // 3) Load the default module
        Module defaultModule = currentPackage.getDefaultModule();
        Assert.assertEquals(defaultModule.documentIds().size(), 2);

        // TODO find an easy way to test the project structure. e.g. serialize the structure in a json file.
        int noOfSrcDocuments = 0;
        int noOfTestDocuments = 0;
        final Collection<ModuleId> moduleIds = currentPackage.moduleIds();
        Assert.assertEquals(moduleIds.size(), 3);
        for (ModuleId moduleId : moduleIds) {
            Module module = currentPackage.module(moduleId);
            for (DocumentId documentId : module.documentIds()) {
                noOfSrcDocuments++;
            }
            for (DocumentId testDocumentId : module.testDocumentIds()) {
                noOfTestDocuments++;
            }
        }

        Assert.assertEquals(noOfSrcDocuments, 4);
        Assert.assertEquals(noOfTestDocuments, 3);

        Assert.assertTrue(project.target().toFile().exists());
        Assert.assertEquals(project.target().getParent(), RESOURCE_DIRECTORY.resolve("myproject"));
    }
}