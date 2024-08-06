package com.kr3st1k.pumptracker.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AlertDialogWithTwoButtons(
    showDialog: Boolean,
    title: String,
    content: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (showDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.background,
            onDismissRequest = onDismiss,
            title = { Text(text = title) },
            text = {
                Text(text = content)
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}