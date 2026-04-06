// Top-level build file
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.sonarqube) apply true
}

sonar {
    properties {
        property("sonar.projectName", "SOEN345-ticket")
        property("sonar.projectKey", "vinnythepoo2_SOEN345-ticket")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "vinnythepoo2")
        property("sonar.token", System.getenv("SONAR_TOKEN") ?: "")
    }
}

subprojects {
    apply(plugin = "org.sonarqube")
}
