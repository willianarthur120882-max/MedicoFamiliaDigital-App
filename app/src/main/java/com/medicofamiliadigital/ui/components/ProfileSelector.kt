package com.medicofamiliadigital.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medicofamiliadigital.data.model.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSelector(
    profiles: List<Profile>,
    selectedProfileId: String?,
    onProfileSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedProfile = profiles.find { it.id == selectedProfileId }

    Column(modifier = Modifier.padding(16.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedProfile?.name ?: "Selecione um Perfil",
                onValueChange = {},
                readOnly = true,
                label = { Text("Perfil") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                profiles.forEach { profile ->
                    DropdownMenuItem(
                        text = { Text(profile.name) },
                        onClick = {
                            onProfileSelected(profile.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

