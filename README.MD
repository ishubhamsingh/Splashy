<p align="center">
<img src="/assets/splashy_banner.png" /> 
</p>

<hr>

<h1 align="center">
<img height="150" src="/assets/icon.png"/>
<br>
Splashy
</h1>

An Unsplash based wallpaper app built with Compose Multiplatform and KMP for Android and iOS.

[![Check](https://github.com/ishubhamsingh/Splashy/actions/workflows/Check.yaml/badge.svg?branch=main)](https://github.com/ishubhamsingh/Splashy/actions/workflows/Check.yaml)


## :hammer_and_wrench: Tech Stack

- [Kotlin Mutliplatform](https://kotlinlang.org/lp/multiplatform/)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Ktor Client](https://ktor.io/)
- [SQLDelight](https://cashapp.github.io/sqldelight/2.0.0/)
- [Voyager](https://github.com/adrielcafe/voyager)
- [Koin](https://insert-koin.io/)
- [Kamel](https://github.com/Kamel-Media/Kamel)
- [Napier](https://github.com/AAkira/Napier)
- [Compose Icons](https://github.com/DevSrSouza/compose-icons)
- [Moko Permissions](https://github.com/icerockdev/moko-permissions)

## :camera_flash: Screenshots

### Android

<img src="/assets/screenshots/android/Screenshot_2.png" width="260">&emsp;<img src="/assets/screenshots/android/Screenshot_3.png" width="260">&emsp;<img src="/assets/screenshots/android/Screenshot_5.png" width="260">

### iOS

<img src="/assets/screenshots/ios/Screenshot_2.png" width="260">&emsp;<img src="/assets/screenshots/ios/Screenshot_4.png" width="260">&emsp;<img src="/assets/screenshots/ios/Screenshot_6.png" width="260">

## :gear: Build

The app uses Unsplash API, hence you need to get your own API key from [here](https://unsplash.com/documentation).
In order to sign your builds generate `splashy.jks` keystore and add keystore password, alias and store password in `secrets.properties`.

secrets.properties would look like this:
```
unsplash.access.key=#Add your unsplash key
splashy.keystore.password=#Add your keystore password
splashy.key.alias=#Add your key alias
splashy.key.password=#Add your key password
```

## License
```
Copyright Shubham Singh

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```