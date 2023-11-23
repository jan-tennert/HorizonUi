package io.github.jan.horizon.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyCard(
    name: String,
    simulate: Boolean,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onSimulateChange: () -> Unit = {},
    onExpand: (() -> Unit)? = null,
    onCreate: (() -> Unit)? = null,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val content: @Composable ColumnScope.() -> Unit = {
        val contextMenuRepresentation = if (isSystemInDarkTheme()) {
            DarkDefaultContextMenuRepresentation
        } else {
            LightDefaultContextMenuRepresentation
        }
        CompositionLocalProvider(LocalContextMenuRepresentation provides contextMenuRepresentation) {
            ContextMenuArea(
                items = {
                    buildList {
                        if(onCreate != null) {
                            add(ContextMenuItem("Create", onCreate))
                        }
                        add(ContextMenuItem("Edit", onEdit))
                        add(ContextMenuItem(if(simulate) "Disable Simulation" else "Enable Simulation", onSimulateChange))
                        add(ContextMenuItem("Delete", onDelete))
                    }
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(name, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    if (onExpand != null) {
                        Icon(if (expanded) Icons.Filled.ExpandMore else Icons.Filled.ExpandLess, null)
                        Spacer(Modifier.width(4.dp))
                    }
                }
            }
        }
    }
    if(onExpand != null) {
        Card(onExpand, modifier, content = content)
    } else {
        Card(modifier, content = content)
    }
}