plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    id("jacoco")
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
        viewBinding = true
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
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = mutableListOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*",
        "**/*Test*.*", "android/**/*.*"
    )
    
    val debugTree = fileTree(layout.buildDirectory.dir("intermediates/javac/debug/classes")) {
        exclude(fileFilter)
    }
    val mainSrc = "${projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("jacoco/testDebugUnitTest.exec", "outputs/code_coverage/debugAndroidTest/connected/*coverage.ec")
    })
}
