import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    id("jacoco")
}

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use { load(it) }
    }
}

fun localProperty(key: String): String {
    val prop = localProperties.getProperty(key, "")
    if (prop.isNotEmpty()) return prop
    // Fallback to environment variables for CI (GitHub Secrets)
    return System.getenv(key) ?: ""
}

android {
    namespace = "com.example.soen345_ticket"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.soen345_ticket"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "EMAILJS_SERVICE_ID", "\"${localProperty("EMAILJS_SERVICE_ID")}\"")
        buildConfigField("String", "EMAILJS_TEMPLATE_ID", "\"${localProperty("EMAILJS_TEMPLATE_ID")}\"")
        buildConfigField("String", "EMAILJS_PUBLIC_KEY", "\"${localProperty("EMAILJS_PUBLIC_KEY")}\"")
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

sonar {
    properties {
        property("sonar.android.lint.report", "build/reports/lint-results-debug.xml")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.ui.database)

    testImplementation(libs.junit)
    testImplementation(libs.json)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockito.core)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.contrib)
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = mutableListOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*",
        "**/*Test*.*", "android/**/*.*",
        "**/databinding/*Binding.class",
        "**/DataBinderMapperImpl.class",
        "**/DataBindingInfo.class",
        "**/BR.class",
        "com/example/soen345_ticket/databinding/*",
        "**/*$*.class",             // Exclude anonymous inner classes/lambdas
        "**/androidx/*.*",          // Exclude library code
        "**/com/google/firebase/*"  // Exclude Firebase internals
    )
    
    // Modern path for Java classes (AGP 9 adds compileDebugJavaWithJavac subdirectory)
    val debugTree = fileTree(layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes")) {
        exclude(fileFilter)
    }
    val mainSrc = "${projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    
    // Modern paths for .exec and .ec files in AGP 8.x/9.x
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include(
            "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
            "outputs/code_coverage/debugAndroidTest/connected/*coverage.ec"
        )
    })
}

tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
    extensions.configure(org.gradle.testing.jacoco.plugins.JacocoTaskExtension::class.java) {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}
