package com.kekadoc.project.capybara.admin.common.viewmodel

import androidx.compose.runtime.Composable
import org.koin.core.component.KoinComponent
import kotlin.reflect.KClass

object ViewModelFactory : KoinComponent {

    fun <T : ViewModel<*>> getViewModel(clazz: KClass<T>): T = getKoin().get(clazz, null, null)

}

inline fun <reified T : ViewModel<*>> ViewModelFactory.getViewModel(): T = ViewModelFactory.getViewModel(T::class)

@Composable
inline fun <reified T : ViewModel<*>> viewModel(): T = ViewModelFactory.getViewModel(T::class)