package com.kekadoc.project.capybara.admin.ui.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class Resource<out T> {

    object Undefined : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    data class Data<T>(val value: T) : Resource<T>()
    data class Error(val throwable: Throwable) : Resource<Nothing>()

}

fun <T> Flow<T>.asResource(skipLoading: Boolean = false): Flow<Resource<T>> =
    map<T, Resource<T>> { Resource.Data(it) }
        .let {
            if (skipLoading) it
            else it.onStart { emit(Resource.Loading) }
        }
        .catch { emit(Resource.Error(it)) }