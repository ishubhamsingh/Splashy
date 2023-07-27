package dev.ishubhamsingh.splashy.core.di

import com.arkivanov.decompose.ComponentContext
import dev.ishubhamsingh.splashy.core.api.UnsplashApi
import dev.ishubhamsingh.splashy.core.di.components.CommonComponents
import io.ktor.client.HttpClient
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@Singleton
abstract class ApplicationComponent (
   @get:Provides val componentContext: ComponentContext
): CommonComponents {
   abstract val unsplashApiCreator: () -> UnsplashApi
}