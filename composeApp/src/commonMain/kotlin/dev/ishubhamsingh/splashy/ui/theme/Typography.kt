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
package dev.ishubhamsingh.splashy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.ishubhamsingh.splashy.resources.Res
import dev.ishubhamsingh.splashy.resources.lato_black
import dev.ishubhamsingh.splashy.resources.lato_bold
import dev.ishubhamsingh.splashy.resources.lato_light
import dev.ishubhamsingh.splashy.resources.lato_regular
import dev.ishubhamsingh.splashy.resources.lato_thin
import dev.ishubhamsingh.splashy.resources.lobster_regular
import org.jetbrains.compose.resources.Font

@Composable
fun getLatoRegular() =
  FontFamily(
    Font(resource = Res.font.lato_regular, weight = FontWeight.Normal, style = FontStyle.Normal)
  )

@Composable
fun getLatoBold() = FontFamily(Font(Res.font.lato_bold, FontWeight.Bold, FontStyle.Normal))

@Composable
fun getLatoBlack() = FontFamily(Font(Res.font.lato_black, FontWeight.Black, FontStyle.Normal))

@Composable
fun getLatoThin() = FontFamily(Font(Res.font.lato_thin, FontWeight.Thin, FontStyle.Normal))

@Composable
fun getLatoLight() = FontFamily(Font(Res.font.lato_light, FontWeight.Light, FontStyle.Normal))

@Composable
fun getLobsterRegular() =
  FontFamily(Font(Res.font.lobster_regular, FontWeight.Normal, FontStyle.Normal))

@Composable
fun getTypography(): Typography {

  val latoBlack = getLatoBlack()
  val latoBold = getLatoBold()
  val latoRegular = getLatoRegular()
  val latoThin = getLatoThin()
  val latoLight = getLatoLight()

  return Typography(
    displayLarge =
      TextStyle(fontFamily = latoBlack, fontWeight = FontWeight.Black, fontSize = 52.sp),
    displayMedium =
      TextStyle(fontFamily = latoBlack, fontWeight = FontWeight.Black, fontSize = 24.sp),
    displaySmall =
      TextStyle(fontFamily = latoBlack, fontWeight = FontWeight.Black, fontSize = 18.sp),
    headlineLarge =
      TextStyle(fontFamily = latoBlack, fontWeight = FontWeight.Black, fontSize = 24.sp),
    headlineMedium =
      TextStyle(fontFamily = latoBlack, fontWeight = FontWeight.Black, fontSize = 18.sp),
    headlineSmall =
      TextStyle(fontFamily = latoBlack, fontWeight = FontWeight.Black, fontSize = 16.sp),
    titleLarge = TextStyle(fontFamily = latoBold, fontWeight = FontWeight.Bold, fontSize = 24.sp),
    titleMedium = TextStyle(fontFamily = latoBold, fontWeight = FontWeight.Bold, fontSize = 18.sp),
    titleSmall = TextStyle(fontFamily = latoBold, fontWeight = FontWeight.Bold, fontSize = 16.sp),
    bodyLarge =
      TextStyle(fontFamily = latoRegular, fontWeight = FontWeight.Normal, fontSize = 24.sp),
    bodyMedium =
      TextStyle(fontFamily = latoRegular, fontWeight = FontWeight.Normal, fontSize = 18.sp),
    bodySmall =
      TextStyle(fontFamily = latoRegular, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    labelLarge = TextStyle(fontFamily = latoLight, fontWeight = FontWeight.Light, fontSize = 18.sp),
    labelMedium =
      TextStyle(fontFamily = latoLight, fontWeight = FontWeight.Light, fontSize = 16.sp),
    labelSmall = TextStyle(fontFamily = latoLight, fontWeight = FontWeight.Light, fontSize = 14.sp)
  )
}
