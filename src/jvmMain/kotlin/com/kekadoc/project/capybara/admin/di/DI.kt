package com.kekadoc.project.capybara.admin.di

import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

object DI : KoinComponent {

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