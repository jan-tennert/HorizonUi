package io.github.jan.horizon.data.local

import kotlinx.serialization.Serializable

@Serializable
data class Vector3D(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0
)

/**
 * @param mass in kg
 * @param startingPosition in km
 * @param startingVelocity in km/s
 * @param name of the body
 * @param modelPath path to the model
 * @param diameter in km
 */
@Serializable
data class BodyData(
    val mass: Double,
    val startingPosition: Vector3D,
    val startingVelocity: Vector3D,
    val name: String,
    val modelPath: String,
    val diameter: Double = 0.0,
    val rotationSpeed: Double = 0.0,
    val axialTilt: Double = 0.0,
    val simulate: Boolean = true,
)

@Serializable
data class SerializedBody(
    val data: BodyData,
    val children: List<SerializedBody>
)

@Serializable
data class SimulationData(
    val bodies: List<SerializedBody>,
    val startingTimeMillis: Long
)

data class Body(
    val data: BodyData,
    val parent: String? = null,
)

enum class BodyType {
    STAR,
    PLANET,
    MOON
}