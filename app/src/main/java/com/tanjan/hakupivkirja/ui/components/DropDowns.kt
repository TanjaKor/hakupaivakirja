package com.tanjan.hakupivkirja.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanjan.hakupivkirja.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadanPituusDropdown(
    currentTrackLength: String,
    onSelectionChange: (selectedOption: String, correspondingMaxPistot: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("100m", "200m", "300m")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = currentTrackLength,
            onValueChange = {},
            readOnly = true,
            label = { Text("Rata") },
            modifier = Modifier
                .menuAnchor()
                .width(90.dp)
                .padding(start = 8.dp, end = 4.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        val newMaxPistot = when (option) {
                            "100m" -> 3
                            "200m" -> 7
                            else -> 11
                        }
                        onSelectionChange(option, newMaxPistot)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PistojenMaaraDropdown(
    selectedPistot: Int,
    maxPistot: Int, 
    onSelectedPistotChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val menuItemData = List(maxPistot) { it + 3 }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            // Luetaan arvo suoraan parametrista, ei paikallisesta muistista
            value = selectedPistot.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Pistot") },
            modifier = Modifier
                .menuAnchor()
                .width(80.dp)
                .padding(start = 4.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            menuItemData.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString()) },
                    onClick = {
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
fun AvutDropdown(selectedText: String, onSelectedValueChange: (String) -> Unit,  modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    val menuItemData = listOf(
            Pair(R.drawable.ghost_solid, "Haamu"),
            Pair(R.drawable.bunny, "Pupu"),
            Pair(R.drawable.run, "Näkö"),
            Pair(R.drawable.voice, "Ääni"),
            Pair(R.drawable.ready, "Valmis")
        )


    val currentIconResId = remember(selectedText, menuItemData) {
        menuItemData.find { it.second == selectedText }?.first
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = " ",
            onValueChange = {},
            readOnly = true,
            label = { Text("Avut", fontSize = 11.sp) },
            leadingIcon = {
                currentIconResId?.let { iconRes ->
                    val iconPainter = painterResource(id = iconRes)
                    Icon(
                        painter = iconPainter,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer),
            modifier = modifier
                .menuAnchor()
                .padding(1.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(150.dp)
        ) {
            menuItemData.forEach { (iconResId, textValue) ->
                DropdownMenuItem(
                    modifier = Modifier.padding(8.dp),
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val iconPainter = painterResource(id = iconResId)
                            Icon(painter = iconPainter, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = textValue,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    },
                    onClick = {
                        onSelectedValueChange(textValue)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PalkkaDropdown(selectedText: String, onSelectedValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    val menuItemData =
        listOf(
        Pair(R.drawable.bone_solid, "Ruoka"),
        Pair(R.drawable.ball, "Lelu"),
        Pair(null, "Molemmat" )
        )

    val selectedIconResId = remember(selectedText, menuItemData) {
        menuItemData.find { it.second == selectedText }?.first
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
            OutlinedTextField(
                value = " ",
                onValueChange = {},
                readOnly = true,
                label = { Text("Palkka", fontSize = 11.sp) },
                leadingIcon = {
                    if (selectedIconResId != null) {
                        val iconPainter = painterResource(id = selectedIconResId)
                        Icon(
                            painter = iconPainter,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    } else if (selectedText == "Molemmat") {
                        Text(
                            text = "&",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer),
                modifier = modifier
                    .menuAnchor()
                    .padding(1.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(150.dp)
            ) {
                menuItemData.forEach { (iconResId, text) ->
                    DropdownMenuItem(
                        modifier = Modifier.padding(8.dp),
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (iconResId != null) {
                                    val iconPainter = painterResource(id = iconResId)
                                    Icon(painter = iconPainter, contentDescription = null, modifier = Modifier.size(24.dp))
                                } else {
                                    Text(
                                        text = "&",
                                        fontSize = 20.sp,
                                        modifier = Modifier.width(24.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
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
                            onSelectedValueChange(text)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
