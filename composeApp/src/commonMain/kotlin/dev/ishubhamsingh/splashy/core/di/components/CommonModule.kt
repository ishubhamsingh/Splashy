package dev.ishubhamsingh.splashy.core.di.components

import dev.ishubhamsingh.splashy.core.domain.UnsplashRepository
import dev.ishubhamsingh.splashy.core.network.UnsplashRepositoryImpl
import dev.ishubhamsingh.splashy.core.network.api.UnsplashApi
import dev.ishubhamsingh.splashy.core.utils.getHttpClient
import dev.ishubhamsingh.splashy.features.home.HomeViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule = module {
    singleOf(::getHttpClient)
    single { UnsplashApi(get()) }
    factory<UnsplashRepository> { UnsplashRepositoryImpl(get()) }
    factory { HomeViewModel() }
}