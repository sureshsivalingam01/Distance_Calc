package com.mrright.distancecalc.presentation.truck_fragment

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mrright.distancecalc.utils.constants.Remove


@Composable
fun DialogToDelete(
    openDialog: Boolean,
    mode: Remove,
    id: Int,
    onDialogDismiss: () -> Unit,
    onRemove: (mode: Remove, id: Int) -> Unit,
) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                onDialogDismiss()
            },
            title = { Text(text = "Alert!") },
            text = { Text(text = "Do you want to delete this ${mode.value} ?") },
            confirmButton = {
                Button(onClick = {
                    onDialogDismiss()
                    onRemove(mode, id)
                }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDialogDismiss()
                }) {
                    Text(text = "No")
                }
            })
    }
}


@Preview
@Composable
fun DeleteForDeletePreview() {
    DialogToDelete(
        openDialog = true,
        mode = Remove.RANGE,
        id = 1,
        onDialogDismiss = {},
        onRemove = { mode, id ->

        })
}