package dev.ishubhamsingh.splashy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.defaultComponentContext
import dev.ishubhamsingh.splashy.core.di.ApplicationComponent
import dev.ishubhamsingh.splashy.core.di.create

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val applicationComponent = ApplicationComponent::class.create(componentContext = defaultComponentContext())
        val unsplashApi = applicationComponent.unsplashApiCreator()
        setContent { AppView(unsplashApi) }
    }
}