package io.github.jan.horizon.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.horizon.data.local.Body
import io.github.jan.horizon.data.local.BodyData
import io.github.jan.horizon.data.local.BodyType
import io.github.jan.horizon.ui.components.BodyCard
import io.github.jan.horizon.ui.components.TopMenu
import io.github.jan.horizon.ui.components.TopMenuItem
import io.github.jan.horizon.ui.dialog.BodyDialog
import io.github.jan.horizon.ui.dialog.DateDialog
import io.github.jan.horizon.ui.dialog.FilePicker
import io.github.jan.horizon.vm.AppViewModel

@Composable
fun BodyScreen(viewModel: AppViewModel) {
    val stars by viewModel.stars.collectAsState()
    val moons by viewModel.moons.collectAsState()
    val planets by viewModel.planets.collectAsState()
    val startingTime by viewModel.startingTimeMillis.collectAsState()
    val expandState = remember { mutableStateMapOf<String, Boolean>() }
    var showOpenDialog by rememberSaveable { mutableStateOf(false) }
    var showSaveDialog by rememberSaveable { mutableStateOf(false) }
    var showTimeDialog by rememberSaveable { mutableStateOf(false) }
    var showOptionsMenu by rememberSaveable { mutableStateOf(false) }
    var showFileMenu by rememberSaveable { mutableStateOf(false) }
    var showCreateStarDialog by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(8.dp)) {
        TopMenu {
            TopMenuItem("File", showFileMenu, { showFileMenu = it }) {
                DropdownMenuItem(
                    text = { Text("Open") },
                    onClick = { showOpenDialog = true; showFileMenu = false },
                )
                DropdownMenuItem(
                    text = { Text("Save") },
                    onClick = { viewModel.save(); showFileMenu = false }
                )
                DropdownMenuItem(
                    text = { Text("Save As") },
                    onClick = { showSaveDialog = true; showFileMenu = false }
                )
            }
            TopMenuItem("Options", showOptionsMenu, { showOptionsMenu = it }) {
                DropdownMenuItem(
                    text = { Text("Change Time Settings") },
                    onClick = { showTimeDialog = true; showOptionsMenu = false }
                )
            }
        }
        LazyColumn {
            stars.forEach { star ->
                val expandedStar = expandState[star.data.name] == true
                val planetChildren = planets.filter { it.parent == star.data.name }
                BodyList(
                    body = star,
                    expanded = expandedStar,
                    onCreate = { viewModel.addBody(it, star.data.name, BodyType.PLANET) },
                    onDelete = { viewModel.deleteBody(star.data) },
                    onExpand = { expandState[star.data.name] = !expandedStar },
                    onEdit = { old, new -> viewModel.editBody(BodyType.STAR, old, new) },
                    type = BodyType.STAR
                )
                if (!expandedStar) return@forEach
                planetChildren.forEach inner@{ planet ->
                    val expandedPlanet = expandState[planet.data.name] == true
                    val moonChildren = moons.filter { it.parent == planet.data.name }
                    BodyList(
                        body = planet,
                        expanded = expandedPlanet,
                        onCreate = { viewModel.addBody(it, planet.data.name, BodyType.MOON) },
                        onDelete = { viewModel.deleteBody(planet.data) },
                        onExpand = { expandState[planet.data.name] = !expandedPlanet },
                        onEdit = { old, new -> viewModel.editBody(BodyType.PLANET, old, new) },
                        type = BodyType.PLANET
                    )
                    if (!expandedPlanet) return@inner
                    moonChildren.forEach { moon ->
                        BodyList(
                            body = moon,
                            expanded = false,
                            onCreate = {},
                            onDelete = { viewModel.deleteBody(moon.data) },
                            onExpand = {},
                            onEdit = { old, new -> viewModel.editBody(BodyType.MOON, old, new) },
                            type = BodyType.MOON
                        )
                    }
                }
            }
        }
    }

    FilePicker(showOpenDialog, fileExtensions = listOf("sim")) {
        showOpenDialog = false
        it?.let { file ->
            viewModel.saveFile.value = file
            viewModel.load()
        }
    }

    FilePicker(showSaveDialog, load = false, fileExtensions = listOf("sim")) {
        showSaveDialog = false
        it?.let { file ->
            viewModel.saveFile.value = file
            viewModel.save()
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        ExtendedFloatingActionButton(
            onClick = { showCreateStarDialog = true },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, null)
            Spacer(Modifier.width(8.dp))
            Text("Add Star")
        }
    }

    if(showTimeDialog) {
        DateDialog(
            timeMillis = startingTime,
            onDismissRequest = { showTimeDialog = false },
            onTimeSelected = { viewModel.startingTimeMillis.value = it }
        )
    }

    if(showCreateStarDialog) {
        BodyDialog(
            onSubmit = {
                viewModel.addBody(it, null, BodyType.STAR)
                showCreateStarDialog = false
            },
            onDismiss = { showCreateStarDialog = false }
        )
    }
}

fun LazyListScope.BodyList(
    body: Body,
    type: BodyType,
    expanded: Boolean,
    onCreate: (BodyData) -> Unit = {},
    onEdit: (old: BodyData, new: BodyData) -> Unit,
    onDelete: () -> Unit = {},
    onExpand: () -> Unit
) {
    item {
        val padding = remember(type) {
            when (type) {
                BodyType.STAR -> 0.dp
                BodyType.PLANET -> 16.dp
                BodyType.MOON -> 32.dp
            }
        }
        var showCreateDialog by rememberSaveable { mutableStateOf(false) }
        var showEditDialog by rememberSaveable { mutableStateOf(false) }
        Row {
            Spacer(Modifier.width(padding))
            BodyCard(
                body.data.name,
                Modifier.padding(6.dp),
                expanded = expanded,
                onExpand = if (type != BodyType.MOON) {
                    onExpand
                } else null,
                onCreate = if (type != BodyType.MOON) {
                    { showCreateDialog = !showCreateDialog }
                } else null,
                onDelete = onDelete,
                onEdit = { showEditDialog = !showEditDialog }
            )
        }

        if (showCreateDialog) {
            BodyDialog(
                onSubmit = {
                    onCreate(it)
                    showCreateDialog = false
                },
                onDismiss = { showCreateDialog = false }
            )
        }

        if (showEditDialog) {
            BodyDialog(
                oldData = body.data,
                onSubmit = {
                    onEdit(body.data, it)
                    showEditDialog = false
                },
                onDismiss = { showEditDialog = false }
            )
        }
    }
}
/*
object TestData {

    val sun = Body(
        data = BodyData(
            mass = 1.0,
            startingPosition = Vector3D(0.0, 0.0, 0.0),
            startingVelocity = Vector3D(0.0, 0.0, 0.0),
            name = "Sun",
         //   modelPath = "sun.glb"
        )
    )

    val earth = Body(
        data = BodyData(
            mass = 1.0,
            startingPosition = Vector3D(0.0, 0.0, 0.0),
            startingVelocity = Vector3D(0.0, 0.0, 0.0),
            name = "Earth",
          //  modelPath = "earth.glb",
        ),
        parent = "Sun"
    )

    val moon = Body(
        data = BodyData(
            mass = 1.0,
            startingPosition = Vector3D(0.0, 0.0, 0.0),
            startingVelocity = Vector3D(0.0, 0.0, 0.0),
            name = "Moon",
            modelPath = "moon.glb",
        ),
        parent = "Earth"
    )

}*/