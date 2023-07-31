package dev.ishubhamsingh.splashy.core.utils

import dev.ishubhamsingh.splashy.core.di.appModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModule())
    }
}