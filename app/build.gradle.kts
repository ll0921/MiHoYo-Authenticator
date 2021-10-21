plugins {
    id("com.android.application")
    id("kotlin-android")
}

val vCompose = "1.1.0-alpha05"

android {

    compileSdk = 31

    defaultConfig {
        applicationId = "hat.auth"
        minSdk = 26
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        resourceConfigurations.apply {
            clear()
            add("zh")
        }
    }

    buildTypes {

        debug {
            versionNameSuffix = "-debug"
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            versionNameSuffix = "-release"
        }

        create("beta") {
            initWith(getByName("debug"))
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            versionNameSuffix = "-beta"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = vCompose
    }

    packagingOptions {
        resources.excludes.apply {
            add("META-INF/AL2.0")
            add("META-INF/LGPL2.1")
        }
    }
}

dependencies {

    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.google.android.material:material:1.4.0")
    implementation("com.google.accompanist:accompanist-permissions:0.19.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.activity:activity-compose:1.4.0-rc01")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha03")

    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.2")
    implementation("com.microsoft.appcenter:appcenter-crashes:4.3.1")
    implementation("com.microsoft.appcenter:appcenter-analytics:4.3.1")
    implementation("com.geetest.sensebot:sensebot:4.3.1") {
        exclude("com.squareup.okhttp3","okhttp")
    }
    implementation("com.journeyapps:zxing-android-embedded:4.2.0") {
        exclude("androidx.legacy","legacy-support-v4")
    }

    implementation("androidx.compose.ui:ui:$vCompose")
    implementation("androidx.compose.material:material:$vCompose")
    implementation("androidx.compose.ui:ui-tooling-preview:$vCompose")
    debugImplementation("androidx.compose.ui:ui-tooling:$vCompose")

}
