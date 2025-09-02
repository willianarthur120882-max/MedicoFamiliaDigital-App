package com.medicofamiliadigital.ui.screens.documents

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medicofamiliadigital.R
import com.medicofamiliadigital.data.model.MedicalDocument
import com.medicofamiliadigital.data.model.Profile
import com.medicofamiliadigital.ui.components.ProfileSelector
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(
    viewModel: DocumentsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { fileUri ->
            val inputStream = context.contentResolver.openInputStream(fileUri)
            val fileBytes = inputStream?.readBytes()
            inputStream?.close()

            fileBytes?.let { bytes ->
                // TODO: Get document name and type from user input in a dialog
                viewModel.uploadDocument("Novo Documento", MedicalDocument.TYPE_OTHER, bytes)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.documents))
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.showUploadDialog() }) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = "Upload Documento"
                    )
                }
            }
        )

        // Profile Selector
        if (uiState.profiles.isNotEmpty()) {
            ProfileSelector(
                profiles = uiState.profiles,
                selectedProfileId = uiState.selectedProfileId,
                onProfileSelected = { viewModel.selectProfile(it) }
            )
        } else if (!uiState.isLoading) {
            Text(
                text = "Nenhum perfil encontrado. Crie um perfil para adicionar documentos.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Content
        if (uiState.isLoading && uiState.documents.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.documents) { document ->
                    DocumentCard(
                        document = document,
                        onViewClick = { /* TODO: Implement document view */ },
                        onDeleteClick = { viewModel.deleteDocument(document.id) }
                    )
                }

                if (uiState.documents.isEmpty() && !uiState.isLoading) {
                    item {
                        EmptyDocumentsState(
                            onUploadDocumentClick = { viewModel.showUploadDialog() }
                        )
                    }
                }
            }
        }
    }

    // Upload Document Dialog
    if (uiState.showUploadDialog) {
        DocumentUploadDialog(
            onDismiss = { viewModel.hideUploadDialog() },
            onUpload = { name, type, bytes ->
                viewModel.uploadDocument(name, type, bytes)
            }
        )
    }

    // Error Snackbar
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar
            viewModel.clearError()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentCard(
    document: MedicalDocument,
    onViewClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (document.documentType) {
                    MedicalDocument.TYPE_EXAM -> Icons.Default.MedicalServices
                    MedicalDocument.TYPE_RECIPE -> Icons.Default.Receipt
                    MedicalDocument.TYPE_REPORT -> Icons.Default.Description
                    else -> Icons.Default.FileCopy
                },
                contentDescription = document.documentType,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = document.documentName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tipo: ${document.documentType}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                document.uploadDate?.let { timestamp ->
                    Text(
                        text = "Upload: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timestamp.toDate())}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onViewClick) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Visualizar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyDocumentsState(
    onUploadDocumentClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.FolderOpen,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Nenhum documento encontrado",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Faça upload de exames, receitas e laudos para organizá-los aqui.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = Alignment.CenterHorizontally,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onUploadDocumentClick
        ) {
            Icon(
                imageVector = Icons.Default.UploadFile,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Fazer Upload de Documento")
        }
    }
}

