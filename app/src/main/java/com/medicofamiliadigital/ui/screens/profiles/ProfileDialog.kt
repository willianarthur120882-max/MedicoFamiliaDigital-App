package com.medicofamiliadigital.ui.screens.profiles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.medicofamiliadigital.R
import com.medicofamiliadigital.data.model.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDialog(
    profile: Profile?,
    onDismiss: () -> Unit,
    onSave: (Profile) -> Unit
) {
    var name by remember { mutableStateOf(profile?.name ?: "") }
    var age by remember { mutableStateOf(profile?.age?.toString() ?: "") }
    var gender by remember { mutableStateOf(profile?.gender ?: Profile.GENDER_MALE) }
    var medicalHistory by remember { mutableStateOf(profile?.medicalHistory ?: "") }
    var allergies by remember { mutableStateOf(profile?.allergies ?: "") }
    var expanded by remember { mutableStateOf(false) }

    val isEditing = profile != null
    val title = if (isEditing) "Editar Perfil" else "Criar Perfil"
    val saveButtonText = if (isEditing) "Salvar" else "Criar"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Age Field
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text(stringResource(R.string.age)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Gender Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(stringResource(R.string.gender)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf(
                            Profile.GENDER_MALE to stringResource(R.string.male),
                            Profile.GENDER_FEMALE to stringResource(R.string.female),
                            Profile.GENDER_OTHER to stringResource(R.string.other)
                        ).forEach { (value, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    gender = value
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Medical History Field
                OutlinedTextField(
                    value = medicalHistory,
                    onValueChange = { medicalHistory = it },
                    label = { Text(stringResource(R.string.medical_history)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Allergies Field
                OutlinedTextField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    label = { Text(stringResource(R.string.allergies)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val ageInt = age.toIntOrNull() ?: 0
                            val newProfile = Profile(
                                id = profile?.id ?: "",
                                userId = profile?.userId ?: "",
                                name = name.trim(),
                                age = ageInt,
                                gender = gender,
                                medicalHistory = medicalHistory.trim().takeIf { it.isNotEmpty() },
                                allergies = allergies.trim().takeIf { it.isNotEmpty() },
                                isChild = ageInt < 13,
                                createdAt = profile?.createdAt ?: com.google.firebase.Timestamp.now(),
                                updatedAt = com.google.firebase.Timestamp.now()
                            )
                            onSave(newProfile)
                        },
                        enabled = name.isNotBlank() && age.isNotBlank()
                    ) {
                        Text(saveButtonText)
                    }
                }
            }
        }
    }
}

