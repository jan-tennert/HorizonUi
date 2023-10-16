package io.github.jan.horizon.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.io.File

@Composable
fun FilePicker(
    show: Boolean,
    load: Boolean = true,
    title: String = "Choose the simulation file",
    initialDirectory: String? = null,
    fileExtensions: List<String> = emptyList(),
    onFileSelected: (File?) -> Unit
) {
    LaunchedEffect(show) {
        if (show) {
            val fileFilter = if (fileExtensions.isNotEmpty()) {
                fileExtensions.joinToString(",")
            } else {
                ""
            }

            val initialDir = initialDirectory ?: System.getProperty("user.dir")
            val filePath = if(load) {
                chooseFile(
                    initialDirectory = initialDir,
                    fileExtension = fileFilter,
                    title = title
                )
            } else {
                chooseSaveFile(
                    initialDirectory = initialDir,
                    fileExtension = fileFilter,
                    title = title
                )
            }
            if (filePath != null) {
                onFileSelected(File(filePath))
            } else {
                onFileSelected(null)
            }

        }
    }
}

internal fun chooseFile(
    initialDirectory: String,
    fileExtension: String,
    title: String
): String? = MemoryStack.stackPush().use { stack ->
    val filters = if (fileExtension.isNotEmpty()) fileExtension.split(",") else emptyList()
    val aFilterPatterns = stack.mallocPointer(filters.size)
    filters.forEach {
        aFilterPatterns.put(stack.UTF8("*.$it"))
    }
    aFilterPatterns.flip()
    TinyFileDialogs.tinyfd_openFileDialog(
        title,
        "bodies.sim",
        aFilterPatterns,
        null,
        false
    )
}

internal fun chooseSaveFile(
    initialDirectory: String,
    fileExtension: String,
    title: String
): String? = MemoryStack.stackPush().use { stack ->
    val filters = if (fileExtension.isNotEmpty()) fileExtension.split(",") else emptyList()
    val aFilterPatterns = stack.mallocPointer(filters.size)
    filters.forEach {
        aFilterPatterns.put(stack.UTF8("*.$it"))
    }
    aFilterPatterns.flip()
    TinyFileDialogs.tinyfd_saveFileDialog(
        title,
        "bodies.sim",
        aFilterPatterns,
        null
    )
}
