package io.github.jan.horizon.di

import io.github.jan.horizon.data.local.BodyDataManager
import io.github.jan.horizon.data.local.BodyDataManagerImpl
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.koin.dsl.module

@OptIn(ExperimentalSerializationApi::class)
val appModule = module {
    single {
        Json {
            namingStrategy = JsonNamingStrategy.SnakeCase
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }
    single<BodyDataManager> {
        BodyDataManagerImpl(get())
    }
}