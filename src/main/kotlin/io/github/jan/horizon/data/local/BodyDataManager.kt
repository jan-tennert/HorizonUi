package io.github.jan.horizon.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

interface BodyDataManager {

    suspend fun readBodyData(file: File): SimulationData?

    suspend fun writeBodyData(file: File, bodyData: SimulationData)

}

internal class BodyDataManagerImpl(
    private val json: Json
) : BodyDataManager {

    override suspend fun readBodyData(file: File): SimulationData? {
        return withContext(Dispatchers.IO) {
            if(!file.exists()) return@withContext null
            try {
                json.decodeFromString(file.readText())
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun writeBodyData(file: File, bodyData: SimulationData) {
        withContext(Dispatchers.IO) {
            if(!file.exists()) file.createNewFile()
            file.writeText(json.encodeToString(bodyData))
        }
    }

}