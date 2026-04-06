// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    id("org.sonarqube") version "5.1.0.4882"
}

sonar {
    properties {
        property("sonar.projectName", "SOEN345-ticket")
        property("sonar.projectKey", "vinnythepoo2_SOEN345-ticket")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "vinnythepoo2")
        property("sonar.token", System.getenv("SONAR_TOKEN") ?: "")
        
        // Root project should point to the sub-project's reports
        property("sonar.sources", "app/src/main/java")
        property("sonar.binaries", "app/build/intermediates/javac/debug/classes")
        property("sonar.java.binaries", "app/build/intermediates/javac/debug/classes")
        property("sonar.tests", "app/src/test/java")
        property("sonar.android.lint.report", "app/build/reports/lint-results-debug.xml")
        property("sonar.coverage.jacoco.xmlReportPaths", "app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
    }
}
