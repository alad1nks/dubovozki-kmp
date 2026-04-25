package com.alad1nks.dubovozki.feature.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Spinner(
    expanded: Boolean,
    content: @Composable () -> Unit,
    dropdownMenuContent: @Composable () -> Unit,
    onClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = SpinnerDefaults.shape,
    backgroundColor: Color = SpinnerDefaults.backgroundColor,
) {
    Box(
        modifier = modifier,
    ) {
        Surface(
            onClick = onClick,
            shape = shape,
            color = backgroundColor,
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                    content()
                }

                Spacer(modifier = Modifier.weight(1f))

                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
        ) {
            dropdownMenuContent()
        }
    }
}

private object SpinnerDefaults {
    val shape: Shape
        @Composable get() = RoundedCornerShape(4.dp)

    val backgroundColor
        @Composable get() = MaterialTheme.colorScheme.surface
}

@Preview
@Composable
private fun SpinnerPreview() {
    val items = listOf("item1", "item2", "item3", "item4", "item5")
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(items.first()) }

    Spinner(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onClick = { expanded = true },
        content = { Text(text = selectedItem) },
        dropdownMenuContent = {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = { selectedItem = item },
                )
            }
        },
    )
}
