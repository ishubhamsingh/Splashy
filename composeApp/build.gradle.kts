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
  alias(libs.plugins.android.application)
  alias(libs.plugins.buildConfig)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.sqlDelight)
}

lateinit var secretKeyProperties: Properties

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
  applyDefaultHierarchyTemplate()
  val secretKeyPropertiesFile = rootProject.file("secrets.properties")
  secretKeyProperties =
    Properties().apply { secretKeyPropertiesFile.inputStream().use { secret -> load(secret) } }

  androidTarget()

  listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "ComposeApp"
      isStatic = true
    }
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
        implementation(libs.koin.compose)
        implementation(libs.kamel.image)
        implementation(libs.kotlinx.datetime)
        implementation(libs.sqlDelight.coroutines.extensions)
        api(libs.moko.permissions)
        implementation(libs.voyager.navigator)
        implementation(libs.voyager.tabNavigator)
        implementation(libs.voyager.transitions)
        implementation(libs.voyager.koin)
        implementation(libs.uri.kmp)
        implementation(libs.stately.common)
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
        implementation(libs.material)
        implementation(libs.androidx.appcompat)
        implementation(libs.androidx.activityCompose)
        implementation(libs.compose.uitooling)
        implementation(libs.kotlinx.coroutines.android)
        implementation(libs.ktor.client.okhttp)
        implementation(libs.sqlDelight.driver.android)
        implementation(libs.koin.android)
      }
    }

    val iosX64Main by getting
    val iosArm64Main by getting
    val iosSimulatorArm64Main by getting
    val iosMain by getting {
      dependsOn(commonMain)
      iosX64Main.dependsOn(this)
      iosArm64Main.dependsOn(this)
      iosSimulatorArm64Main.dependsOn(this)
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

  buildFeatures { buildConfig = true }

  signingConfigs {
    create("release") {
      storeFile = file("$rootDir/keystore/splashy.jks")
      storePassword = "${secretKeyProperties["splashy.keystore.password"]}"
      keyAlias = "${secretKeyProperties["splashy.key.alias"]}"
      keyPassword = "${secretKeyProperties["splashy.key.password"]}"
    }
  }

  buildTypes {
    debug {
      isMinifyEnabled = false
      isDebuggable = true
      applicationIdSuffix = ".debug"
      signingConfig = signingConfigs.getByName("release")
    }

    release {
      isMinifyEnabled = true
      isShrinkResources = true
      isDebuggable = false
      signingConfig = signingConfigs.getByName("release")
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/resources")
  sourceSets["main"].resources.srcDirs("src/commonMain/resources")

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  packaging { resources.excludes.add("META-INF/*") }

  kotlin { jvmToolchain(17) }
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

compose.resources {
  publicResClass = true
  packageOfResClass = "dev.ishubhamsingh.splashy.resources"
  generateResClass = always
}
