package com.example.hakupivkirja.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hakupivkirja.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadanPituusDropdown(onMaxPistotChange: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("100m", "200m", "300m")
    var selectedOption by remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text("Rata") },
            modifier = Modifier
                .menuAnchor()// Ensures proper dropdown anchoring
                .width(80.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedOption = option
                        val newMaxPistot = when (option) {
                            "100m" -> 3
                            "200m" -> 7
                            else -> 11
                        }
                        onMaxPistotChange(newMaxPistot)
                        expanded = false

                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PistojenMaaraDropdown(maxPistot: Int, onSelectedPistotChange: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val menuItemData = List(maxPistot) { it+3 }
    var selectedPistot by remember { mutableStateOf(menuItemData[0].toString()) }


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedPistot.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Pistot") },
            modifier = Modifier
                .menuAnchor()
                .width(70.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            menuItemData.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString()) },
                    onClick = {
                        selectedPistot = option.toString()
                        onSelectedPistotChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvutDropdown() {
    var expanded by remember { mutableStateOf(false) }

    // List of SVG icons (drawables) and their corresponding text labels
    val menuItemData = listOf(
        Pair(R.drawable.ghost_solid, "Haamu"),  // Icon and text
        Pair(R.drawable.bunny, "Pupu"),
        Pair(R.drawable.run, "Näkö"),
        Pair(R.drawable.voice, "Ääni"),
        Pair(R.drawable.ready, "Valmis")
    )

    // Store the selected text option
    var selectedIcon by remember { mutableStateOf<Int?>(null) } // default to the first item

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        // OutlinedTextField to show the selected text option
        OutlinedTextField(
            value = " ",
            onValueChange = {},
            readOnly = true,
            label = { Text("Avut", fontSize = 11.sp) },
            leadingIcon = {
                // Only show the icon if something is selected
                selectedIcon?.let {
                    val iconPainter = painterResource(id = it)
                    Icon(painter = iconPainter, contentDescription = null, modifier = Modifier.size(24.dp))
                }
            },
            modifier = Modifier
                .menuAnchor()
                .width(65.dp) // Adjust width of the dropdown trigger
                .height(56.dp)
                .padding(4.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(150.dp)
        ) {
            // Loop through menuItemData to display each dropdown item
            menuItemData.forEach { (iconResId, text) ->
                DropdownMenuItem(
                    modifier = Modifier
                        .padding(8.dp),  // Add padding for better spacing,
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Load the SVG as a painter
                            val iconPainter = painterResource(id = iconResId)
                            Icon(painter = iconPainter, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = text,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    },
                    onClick = {
                        // Update selected option with the text (not the drawable resource)
                        selectedIcon = iconResId
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PalkkaDropdown() {
    var expanded by remember { mutableStateOf(false) }

    // List of SVG icons (drawables) and their corresponding text labels
    val menuItemData = listOf(
        Pair(R.drawable.bone_solid, "Ruoka"),  // Icon and text
        Pair(R.drawable.ball, "Lelu"),
    )

    // Store the selected text option
    var selectedIcon by remember { mutableStateOf<Int?>(null) } // default to the first item

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        // OutlinedTextField to show the selected text option
        OutlinedTextField(
            value = " ",
            onValueChange = {},
            readOnly = true,
            label = { Text("Palkka", fontSize = 11.sp) },
            leadingIcon = {
                // Only show the icon if something is selected
                selectedIcon?.let {
                    val iconPainter = painterResource(id = it)
                    Icon(painter = iconPainter, contentDescription = null, modifier = Modifier.size(24.dp))
                }
            },
            modifier = Modifier
                .menuAnchor()
                .width(80.dp) // Adjust width of the dropdown trigger
                .height(56.dp)
                .padding(4.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(150.dp)
        ) {
            // Loop through menuItemData to display each dropdown item
            menuItemData.forEach { (iconResId, text) ->
                DropdownMenuItem(
                    modifier = Modifier
                        .padding(8.dp),  // Add padding for better spacing,
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Load the SVG as a painter
                            val iconPainter = painterResource(id = iconResId)
                            Icon(painter = iconPainter, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = text,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    },
                    onClick = {
                        // Update selected option with the text (not the drawable resource)
                        selectedIcon = iconResId
                        expanded = false
                    }
                )
            }
        }
    }
}
