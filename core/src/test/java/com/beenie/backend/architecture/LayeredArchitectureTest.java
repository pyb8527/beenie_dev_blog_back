package com.beenie.backend.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.beenie.backend")
class LayeredArchitectureTest {

    @ArchTest
    static final ArchRule layer_dependencies_are_respected = layeredArchitecture()
            .consideringAllDependencies()
            .withOptionalLayers(true)
            .layer("Web").definedBy("com.beenie.backend.web..")
            .layer("Application").definedBy("com.beenie.backend.application..")
            .layer("Domain").definedBy("com.beenie.backend.domain..")
            .layer("Infrastructure").definedBy("com.beenie.backend.infrastructure..")
            .layer("Storage").definedBy("com.beenie.backend.storage..")
            .layer("Support").definedBy("com.beenie.backend.support..")

            .whereLayer("Web").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Web")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Web", "Application", "Infrastructure", "Storage")
            .whereLayer("Infrastructure").mayOnlyBeAccessedByLayers("Web", "Application")
            .whereLayer("Storage").mayOnlyBeAccessedByLayers("Web", "Application", "Infrastructure")
            .whereLayer("Support").mayOnlyBeAccessedByLayers(
                    "Web", "Application", "Domain", "Infrastructure", "Storage");
}
