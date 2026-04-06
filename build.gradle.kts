// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.sonarqube) apply true
}

sonar {
    properties {
        property("sonar.projectName", "SOEN345-ticket")
        property("sonar.projectKey", "vinnythepoo2_SOEN345-ticket")
        property("sonar.organization", "vinnythepoo2")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.token", System.getenv("SONAR_TOKEN") ?: "")
        
        // Android specific global settings
        property("sonar.android.lint.report", "app/build/reports/lint-results-debug.xml")
        property("sonar.coverage.jacoco.xmlReportPaths", "app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
        property("sonar.sources", "app/src/main/java")
        property("sonar.tests", "app/src/test/java")
        property("sonar.java.binaries", "app/build/intermediates/javac/debug/classes")
    }
}
