package com.kekadoc.project.capybara.admin.common.viewmodel

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.RealSettings
import org.orbitmvi.orbit.internal.RealContainer

abstract class ViewModel<ViewState : Any>(initialState: ViewState) : ContainerHost<ViewState, Nothing> {

    protected val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override val container: Container<ViewState, Nothing> = RealContainer(
        initialState = initialState,
        parentScope = coroutineScope,
        settings = RealSettings(
            exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
                throwable.printStackTrace()
            }
        ),
    )

}