package io.github.jan.horizon.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun TopMenu(content: @Composable () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun TopMenuItem(
    title: String,
    expand: Boolean,
    onExpandChange: (Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        TextButton({ onExpandChange(true) }) {
            Text(title)
        }
        DropdownMenu(expand, onDismissRequest = { onExpandChange(false) }) {
            content()
        }
    }
}