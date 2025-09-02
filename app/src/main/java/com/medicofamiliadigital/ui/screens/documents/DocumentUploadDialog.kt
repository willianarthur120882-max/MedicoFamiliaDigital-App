package com.medicofamiliadigital.ui.screens.documents

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.medicofamiliadigital.R
import com.medicofamiliadigital.data.model.MedicalDocument
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentUploadDialog(
    onDismiss: () -> Unit,
    onUpload: (String, String, ByteArray) -> Unit
) {
    var documentName by remember { mutableStateOf("") }
    var documentType by remember { mutableStateOf(MedicalDocument.TYPE_OTHER) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

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
                    text = "Fazer Upload de Documento",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Document Name Field
                OutlinedTextField(
                    value = documentName,
                    onValueChange = { documentName = it },
                    label = { Text("Nome do Documento") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Document Type Dropdown
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = documentType,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Tipo de Documento") },
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
                            MedicalDocument.TYPE_EXAM,
                            MedicalDocument.TYPE_RECIPE,
                            MedicalDocument.TYPE_REPORT,
                            MedicalDocument.TYPE_OTHER
                        ).forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    documentType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // File Picker Button
                Button(
                    onClick = { pickFileLauncher.launch("*/*") }, // Allow all file types for now
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = selectedFileUri?.lastPathSegment ?: "Selecionar Arquivo")
                }

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
                            selectedFileUri?.let { uri ->
                                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                                val fileBytes = inputStream?.readBytes()
                                inputStream?.close()

                                if (fileBytes != null) {
                                    onUpload(documentName.trim(), documentType, fileBytes)
                                }
                            }
                        },
                        enabled = documentName.isNotBlank() && selectedFileUri != null
                    ) {
                        Text("Upload")
                    }
                }
            }
        }
    }
}

