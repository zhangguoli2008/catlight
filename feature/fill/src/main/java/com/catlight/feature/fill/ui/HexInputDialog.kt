package com.catlight.feature.fill.ui

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction

@Composable
fun HexInputDialog(
    currentHex: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var value by remember(currentHex) { mutableStateOf(currentHex.removePrefix("#")) }
    val isValid = value.length in setOf(3, 6, 8)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "十六进制颜色") },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { input ->
                    value = input.filter { it.isLetterOrDigit() }.take(8)
                },
                singleLine = true,
                prefix = { Text(text = "#") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (isValid) {
                        onConfirm("#$value")
                    }
                }),
                label = { Text("输入颜色代码") }
            )
        },
        confirmButton = {
            TextButton(onClick = { if (isValid) onConfirm("#$value") }, enabled = isValid) {
                Text("确定", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
