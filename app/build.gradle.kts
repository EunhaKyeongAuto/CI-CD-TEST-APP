plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.cicdtestapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cicdtestapp"
        minSdk = 26
        targetSdk = 34
//        versionCode = 1
//        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create("qa") {
            initWith(buildTypes["debug"])
        }
    }

    tasks.whenTaskAdded {
        doLast {
            val isDebug = name.equals("assembleDebug", true)
            val isQa = name.equals("assembleQa", true)
            val isRelease = name.equals("assembleRelease", true)
            val isBundleRelease = name.equals("bundleRelease", true)
            val projectName = "cicdtest"
            val buildTypeLabel = when {
                isDebug -> "debug"
                isQa -> "qa"
                isRelease -> "release"
                isBundleRelease -> "release"
                else -> ""
            }
            val outputFileName = "$projectName-$buildTypeLabel"
            val format = ".apk"
            val specificOutputPath = project.layout.buildDirectory.dir("outputs/apk/$buildTypeLabel").get().asFile

            specificOutputPath.walkTopDown().forEach { file ->
                if (isBundleRelease && file.extension == "aab" || !isBundleRelease && file.extension == "apk") {
                    val newFile = file.parentFile.resolve("$outputFileName$format")
                    if (file.renameTo(newFile)) {
                        println("File renamed to ${newFile.path}")
                    } else {
                        println("Failed to rename file ${file.path}")
                    }
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}