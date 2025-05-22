package com.example.weatherapp.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R

@Composable
fun <T> DropdownMenu(
    label: String,
    leadingIcon: @Composable () -> Unit = {},
    options: List<DropdownOption<T>>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
) {
    ExpandableColumn(
        label = label,
        leadingIcon = leadingIcon
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            options.forEach { (optionLabel, optionValue, icon) ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .clickable {
                            onOptionSelected(optionValue)
                        }
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = optionLabel,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        icon()
                    }
                    if (optionValue == selectedOption) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(R.string.selected),
                            tint = Color.Green
                        )
                    }
                }
            }
        }
    }
}

data class DropdownOption<T>(
    val label: String,
    val value: T,
    val trailingIcon: @Composable () -> Unit = {}
)
