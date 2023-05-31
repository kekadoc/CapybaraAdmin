package com.kekadoc.project.capybara.admin.di

import org.koin.core.context.startKoin

object DI {

    fun init() {

        startKoin {
            modules(
                remoteDataSourcesModule,
                repositoriesModule,
                viewModelsModule,
            )
        }

    }

}