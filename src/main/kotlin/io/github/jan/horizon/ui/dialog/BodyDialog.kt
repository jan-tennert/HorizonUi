package io.github.jan.horizon.ui.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.github.jan.horizon.data.local.BodyData
import io.github.jan.horizon.data.local.Vector3D

@Composable
fun BodyDialog(oldData: BodyData? = null, onSubmit: (BodyData) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(oldData?.name ?: "") }
    var mass by remember { mutableStateOf(oldData?.mass?.toString() ?: "") }
    var diameter by remember { mutableStateOf(oldData?.diameter?.toString() ?: "") }
    var startingPosition by remember { mutableStateOf(oldData?.startingPosition ?: Vector3D()) }
    var startingVelocity by remember { mutableStateOf(oldData?.startingVelocity ?: Vector3D()) }
    var modelPath by remember { mutableStateOf(oldData?.modelPath ?: "") }
    Dialog(onDismissRequest = onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp).background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp)).padding(16.dp)
        ) {
            CreateTextField("Name", name) { name = it }
            CreateTextField("Mass in kg", mass, mass.toDoubleOrNull() == null) { mass = it }
            CreateTextField("Diameter in km", diameter, diameter.toDoubleOrNull() == null) { diameter = it }
            VectorTextField("Starting Position in km", startingPosition) { startingPosition = it }
            VectorTextField("Starting Velocity in km/s", startingVelocity) { startingVelocity = it }
            CreateTextField("Model Path", modelPath, placeholder = "earth.glb") { modelPath = it }
            Button(
                onClick = {
                    onSubmit(
                        BodyData(
                            mass.toDouble(),
                            startingPosition,
                            startingVelocity,
                            name,
                            modelPath,
                            diameter.toDouble()
                        )
                    )
                },
                enabled = name.isNotEmpty() && mass.toDoubleOrNull() != null && modelPath.isNotEmpty() && diameter.toDoubleOrNull() != null
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
fun VectorTextField(label: String, value: Vector3D, onValueChange: (Vector3D) -> Unit) {
    var x by remember { mutableStateOf(value.x.toString()) }
    var y by remember { mutableStateOf(value.y.toString()) }
    var z by remember { mutableStateOf(value.z.toString()) }
    LaunchedEffect(x, y, z) {
        val xD = x.toDoubleOrNull()
        val yD = y.toDoubleOrNull()
        val zD = z.toDoubleOrNull()
        if(xD != null && yD != null && zD != null) {
            onValueChange(Vector3D(xD, yD, zD))
        }
    }

    Text(label, fontSize = 13.sp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(4.dp)
    ) {
        OutlinedTextField(x, { x = it }, Modifier.weight(1f), singleLine = true, label = { Text("X") }, isError = x.toDoubleOrNull() == null)
        Spacer(Modifier.width(4.dp))
        OutlinedTextField(y, { y = it  }, Modifier.weight(1f), singleLine = true, label = { Text("Y") }, isError = y.toDoubleOrNull() == null)
        Spacer(Modifier.width(4.dp))
        OutlinedTextField(z, { z = it }, Modifier.weight(1f), singleLine = true, label = { Text("Z") }, isError = z.toDoubleOrNull() == null)
    }
}

@Composable
fun CreateTextField(label: String, value: String, isError: Boolean = false, placeholder: String? = null, onValueChange: (String) -> Unit) {
    OutlinedTextField(value, onValueChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth().padding(4.dp), isError = isError, placeholder = { if(placeholder != null) { Text(placeholder) } })
}

@Composable
@Preview
fun CreateDialogPreview() {
    //BodyDialog(BodyType.STAR, {}) {}
}