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
import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
  alias(libs.plugins.multiplatform).apply(false)
  alias(libs.plugins.compose).apply(false)
  alias(libs.plugins.cocoapods).apply(false)
  alias(libs.plugins.android.application).apply(false)
  alias(libs.plugins.buildConfig).apply(false)
  alias(libs.plugins.kotlinx.serialization).apply(false)
  alias(libs.plugins.sqlDelight).apply(false)
  alias(libs.plugins.spotless).apply(false)
}

allprojects {
  apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)
  configure<SpotlessExtension> {
    kotlin {
      ktfmt(libs.versions.ktfmt.get()).googleStyle()
      target("**/*.kt")
      targetExclude("$projectDir/build/**/*.kt")
      licenseHeaderFile(rootProject.file("spotless/copyright.txt"))
    }
    kotlinGradle {
      ktfmt(libs.versions.ktfmt.get()).googleStyle()
      target("**/*.kts")
      targetExclude("$projectDir/build/**/*.kts")
      licenseHeaderFile(rootProject.file("spotless/copyright.txt"), "(^(?![\\/ ]\\*).*$)")
    }
    format("xml") {
      target("**/*.xml")
      targetExclude("**/build/", ".idea/")
      trimTrailingWhitespace()
      endWithNewline()
    }
  }
}
