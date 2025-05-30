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

/**
 * Reusable composable for displaying a dropdown menu.
 *
 * @param label The label for the dropdown menu.
 * @param leadingIcon The leading icon for the dropdown menu.
 * @param options The list of options to display in the dropdown menu.
 * @param selectedOptions The set of selected options.
 * @param onOptionSelected The callback function to be invoked when an option is selected.
 *
 */
@Composable
fun <T> DropdownMenu(
    label: String,
    leadingIcon: @Composable () -> Unit = {},
    options: List<DropdownOption<T>>,
    selectedOptions: Set<T>,
    onOptionSelected: (T) -> Unit,
) {
    ExpandableColumn(
        label = label,
        leadingIcon = leadingIcon,
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
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.selected),
                        // use transparent icon to stop height slightly shifting for the selected option
                        tint = if (optionValue in selectedOptions) Color.Green else Color.Transparent
                    )
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
