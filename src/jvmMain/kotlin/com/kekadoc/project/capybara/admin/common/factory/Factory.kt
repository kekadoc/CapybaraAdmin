package com.kekadoc.project.capybara.server.common.factory

sealed interface Factory<R> {

    interface Nothing<R> : Factory<R> {

        fun create(): R

    }

    interface Single<T, R> : Factory<R> {

        fun create(value: T): R

    }

    interface Twin<R, T1, T2> : Factory<R>, (T1, T2) -> R

    interface Triple<R, T1, T2, T3> : Factory<R>, (T1, T2, T3) -> R

}