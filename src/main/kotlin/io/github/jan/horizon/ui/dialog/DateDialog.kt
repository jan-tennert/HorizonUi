package io.github.jan.horizon.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(
    timeMillis: Long?,
    onDismissRequest: () -> Unit,
    onTimeSelected: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(timeMillis)
    var showDateChange by remember { mutableStateOf(false) }
    val formattedDate = remember(datePickerState.selectedDateMillis) { datePickerState.selectedDateMillis?.let { DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(it).atZone(
        ZoneId.systemDefault())) } }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Current Starting Date: $formattedDate")
            TextButton(
                onClick = { showDateChange = true },
                content = {
                    Text("Change Date")
                }
            )
        }
    }

    if(showDateChange) {
        DatePickerDialog(
            onDismissRequest = { showDateChange = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { onTimeSelected(it) }
                        showDateChange = false
                    },
                    content = {
                        Text("Ok")
                    }
                )
            }
        ) {
            DatePicker(
                datePickerState
            )
        }
    }
}