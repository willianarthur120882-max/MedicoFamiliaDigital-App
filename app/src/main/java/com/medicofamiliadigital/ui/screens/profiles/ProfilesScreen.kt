package com.medicofamiliadigital.ui.screens.profiles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medicofamiliadigital.R
import com.medicofamiliadigital.data.model.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(
    viewModel: ProfilesViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.my_profiles))
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
                IconButton(onClick = { viewModel.showCreateDialog() }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_profile)
                    )
                }
            }
        )

        // Content
        if (uiState.isLoading && uiState.profiles.isEmpty()) {
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
                items(uiState.profiles) { profile ->
                    ProfileCard(
                        profile = profile,
                        onEditClick = { viewModel.showEditDialog(profile) },
                        onDeleteClick = { viewModel.deleteProfile(profile.id) }
                    )
                }

                if (uiState.profiles.isEmpty()) {
                    item {
                        EmptyProfilesState(
                            onAddProfileClick = { viewModel.showCreateDialog() }
                        )
                    }
                }
            }
        }
    }

    // Create Profile Dialog
    if (uiState.showCreateDialog) {
        ProfileDialog(
            profile = null,
            onDismiss = { viewModel.hideCreateDialog() },
            onSave = { profile -> viewModel.createProfile(profile) }
        )
    }

    // Edit Profile Dialog
    if (uiState.showEditDialog && uiState.editingProfile != null) {
        ProfileDialog(
            profile = uiState.editingProfile,
            onDismiss = { viewModel.hideEditDialog() },
            onSave = { profile -> viewModel.updateProfile(profile) }
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
fun ProfileCard(
    profile: Profile,
    onEditClick: () -> Unit,
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
            // Profile Avatar
            Card(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = if (profile.isChild) 
                        MaterialTheme.colorScheme.secondaryContainer 
                    else 
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (profile.isChild) "👶" else "👤",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Profile Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (profile.isChild) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                text = "Criança",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${profile.age} anos • ${profile.gender}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (profile.allergies?.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Alergias: ${profile.allergies}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Action Buttons
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
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
fun EmptyProfilesState(
    onAddProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.PersonAdd,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Nenhum perfil criado",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Adicione perfis para você e sua família para começar a usar o aplicativo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onAddProfileClick
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.add_profile))
        }
    }
}

