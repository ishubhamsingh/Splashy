/*
 * Copyright 2023 Shubham Singh
 *
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
import java.util.Properties

plugins {
  alias(libs.plugins.multiplatform)
  alias(libs.plugins.compose)
  alias(libs.plugins.cocoapods)
  alias(libs.plugins.android.application)
  alias(libs.plugins.buildConfig)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.sqlDelight)
}

lateinit var secretKeyProperties: Properties

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
  targetHierarchy.default()
  val secretKeyPropertiesFile = rootProject.file("secrets.properties")
  secretKeyProperties =
    Properties().apply { secretKeyPropertiesFile.inputStream().use { secret -> load(secret) } }

  android { compilations.all { kotlinOptions { jvmTarget = "17" } } }

  iosX64()
  iosArm64()
  iosSimulatorArm64()

  cocoapods {
    version = "1.0.0"
    summary =
      "An Unsplash based wallpaper app built with Compose Multiplatform and KMM for Android and iOS"
    homepage = "https://github.com/ishubhamsingh/Splashy"
    ios.deploymentTarget = "16.0"
    podfile = project.file("../iosApp/Podfile")
    framework {
      baseName = "ComposeApp"
      isStatic = true
    }
    extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.animation)
        implementation(compose.material)
        implementation(compose.material3)
        @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
        implementation(compose.components.resources)
        implementation(libs.composeImageLoader)
        implementation(libs.napier)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.insetsx)
        implementation(libs.ktor.core)
        implementation(libs.ktor.client.content.negotiation)
        implementation(libs.ktor.serialization.kotlinx.json)
        implementation(libs.ktor.client.logging)
        implementation(libs.composeIcons.featherIcons)
        implementation(libs.composeIcons.evaIcons)
        implementation(libs.composeIcons.octicons)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.multiplatformSettings)
        implementation(libs.koin.core)
        implementation(libs.kamel.image)
        implementation(libs.materialKolor)
        implementation(libs.kotlinx.datetime)
        implementation(libs.sqlDelight.coroutines.extensions)
        api(libs.precompose)
        api(libs.moko.mvvm.compose)
        api(libs.moko.mvvm.flow.compose)
        api(libs.moko.permissions)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
        implementation(libs.koin.test)
      }
    }

    val androidMain by getting {
      dependencies {
        implementation(libs.androidx.appcompat)
        implementation(libs.androidx.activityCompose)
        implementation(libs.compose.uitooling)
        implementation(libs.kotlinx.coroutines.android)
        implementation(libs.ktor.client.okhttp)
        implementation(libs.sqlDelight.driver.android)
        implementation(libs.koin.android)
      }
    }

    val iosMain by getting {
      dependencies {
        implementation(libs.ktor.client.darwin)
        implementation(libs.sqlDelight.driver.native)
      }
    }

  }
}

android {
  namespace = "dev.ishubhamsingh.splashy"
  compileSdk = 34

  defaultConfig {
    minSdk = 26
    targetSdk = 34

    applicationId = "dev.ishubhamsingh.splashy"
    versionCode = 1
    versionName = "1.0.0"
  }
  sourceSets["main"].apply {
    manifest.srcFile("src/androidMain/AndroidManifest.xml")
    res.srcDirs("src/androidMain/resources", "src/commonMain/resources")
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  packagingOptions { resources.excludes.add("META-INF/**") }
}

buildConfig {
  buildConfigField(
    "String",
    "UNSPLASH_API_KEY",
    "\"${secretKeyProperties["unsplash.access.key"]}\""
  )
}

sqldelight {
  databases {
    create("SplashyDatabase") {
      packageName.set("dev.ishubhamsingh.splashy.db")
      dialect(libs.sqlDelight.sqlite.dialect)
    }
  }
}
