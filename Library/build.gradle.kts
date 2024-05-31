import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
}
val keystorePropertiesFile = rootProject.file("azure-configs.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "com.mylibrary.sdks"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    afterEvaluate {
        publishing {
            publications {

                create<MavenPublication>("release") {
                    from(components["release"])
                    groupId = "com.test.library"
                    artifactId = "mylibrary"
                    version = "0.0.1"
                }
            }
            repositories {
                maven {
                    name = "test"
                    url =uri(`keystoreProperties`.getProperty("repositoryUrl"))
                    credentials {
                        username= keystoreProperties.getProperty("userName")
                        password =keystoreProperties.getProperty("azureMavenAccessToken")
                    }
                }
            }
        }

    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

