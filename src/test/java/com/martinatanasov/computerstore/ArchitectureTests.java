/*
 * Copyright 2025-2026 Martin Atanasov.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.martinatanasov.computerstore;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public class ArchitectureTests {

    private final JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.martinatanasov.computerstore");

    @Test
    public void mvcLayersTest() {
        layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Controller").definedBy("..controllers..")
                .layer("Service").definedBy("..services..")
                .layer("Repository").definedBy("..repositories..")
                //Controller → Service
                .whereLayer("Controller").mayOnlyAccessLayers("Service")
                //Service → Repository
                .whereLayer("Service").mayOnlyAccessLayers("Repository")
                //Repository → nothing above
                .whereLayer("Repository").mayNotAccessAnyLayer()
                .check(importedClasses);
    }

    /**
     * Test about controllers, services, repositories and their annotations are specific for Spring framework.
     */
    @Test
    void controllersShouldBeAnnotatedAndContainCorrectName() {
        classes()
                .that().resideInAPackage("..controllers..")
                //Ignore inner / anonymous
                .and().areTopLevelClasses()
                //Skip package-info
                .and().doNotHaveSimpleName("package-info")
                .should().beAnnotatedWith(Controller.class)
                .orShould().beAnnotatedWith(RestController.class)
                //Should contain Controller in the name
                .andShould().haveSimpleNameContaining("Controller")
                .check(importedClasses);
    }

    @Test
    void servicesShouldBeAnnotatedAndContainCorrectName() {
        classes()
                .that().resideInAPackage("..services..")
                //Ignore inner / anonymous
                .and().areTopLevelClasses()
                //Ignore interfaces
                .and().areNotInterfaces()
                .should().beAnnotatedWith(Service.class)
                //Should contain Service in the name
                .andShould().haveSimpleNameContaining("Service")
                .check(importedClasses);
    }

    @Test
    void repositoriesShouldBeAnnotatedAndContainCorrectName() {
        classes()
                .that().resideInAPackage("..repositories..")
                //Ignore inner / anonymous
                .and().areTopLevelClasses()
                //Ignore interfaces
                .and().areNotInterfaces()
                .should().beAnnotatedWith(Repository.class)
                //Should contain Repository or Dao in the name
                .andShould().haveSimpleNameContaining("Repository")
                .orShould().haveSimpleNameContaining("Dao")
                .check(importedClasses);
    }

}