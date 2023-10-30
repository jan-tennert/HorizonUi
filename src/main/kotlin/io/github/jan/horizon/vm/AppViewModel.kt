package io.github.jan.horizon.vm

import androidx.compose.runtime.mutableStateMapOf
import io.github.jan.horizon.data.local.*
import io.github.jan.horizon.data.local.BodyType.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class AppViewModel: KoinComponent {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val bodyDataManager by inject<BodyDataManager>()
    val stars = MutableStateFlow(emptyList<Body>())
    val planets = MutableStateFlow(emptyList<Body>())
    val moons = MutableStateFlow(emptyList<Body>())
    val saveFile = MutableStateFlow<File>(File("assets/bodies.sim"))
    val startingTimeMillis = MutableStateFlow(Clock.System.now().toEpochMilliseconds())
    val expandState = mutableStateMapOf<String, Boolean>()

    fun dispose() {
        scope.cancel()
    }

    fun addBody(body: BodyData, parent: String?, type: BodyType) {
        when(type) {
            STAR -> stars.value += Body(body, parent)
            PLANET -> planets.value += Body(body, parent)
            MOON -> moons.value += Body(body, parent)
        }
    }

    fun editBody(type: BodyType, oldBody: BodyData, newBody: BodyData) {
        if(oldBody.name != newBody.name) {
            expandState[newBody.name] = expandState[oldBody.name] ?: false
            expandState.remove(oldBody.name)
        }
        when(type) {
            STAR -> {
                stars.value = stars.value.map { if(it.data == oldBody) Body(newBody, it.parent) else it }
                //update children
                planets.value = planets.value.map { if(it.parent == oldBody.name) Body(it.data, newBody.name) else it }
            }
            PLANET ->{
                planets.value = planets.value.map { if(it.data == oldBody) Body(newBody, it.parent) else it }
                //update children
                moons.value = moons.value.map { if(it.parent == oldBody.name) Body(it.data, newBody.name) else it }
            }
            MOON -> moons.value = moons.value.map { if(it.data == oldBody) Body(newBody, it.parent) else it }
        }
    }

    fun deleteBody(body: BodyData) {
        stars.value = stars.value.filter { it.data != body }
        planets.value = planets.value.filter { it.data != body }
        moons.value = moons.value.filter { it.data != body }
        expandState.remove(body.name)
    }

    fun load() {
        scope.launch {
            runCatching {
                bodyDataManager.readBodyData(saveFile.value)
            }.onSuccess {
                val bodies = it?.bodies ?: emptyList()
                val stars = bodies.map { star -> Body(star.data) }
                val planets = bodies.flatMap { star -> star.children.map { planet -> Body(planet.data, parent = star.data.name) } }
                val moons = bodies.flatMap { star -> star.children.flatMap { planet -> planet.children.map { moon -> Body(moon.data, parent = planet.data.name) } } }
                this@AppViewModel.stars.value = stars
                this@AppViewModel.planets.value = planets
                this@AppViewModel.moons.value = moons
                this@AppViewModel.startingTimeMillis.value = it?.startingTimeMillis ?: Clock.System.now().toEpochMilliseconds()
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun save() {
        scope.launch {
            runCatching {
                val bodies = stars.value.map { star ->
                    SerializedBody(
                        data = star.data,
                        children = planets.value.filter { it.parent == star.data.name }.map { planet ->
                            SerializedBody(
                                data = planet.data,
                                children = moons.value.filter { it.parent == planet.data.name }.map { moon ->
                                    SerializedBody(
                                        data = moon.data,
                                        children = emptyList()
                                    )
                                }
                            )
                        }
                    )
                }
                bodyDataManager.writeBodyData(saveFile.value, SimulationData(bodies, startingTimeMillis.value))
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

}