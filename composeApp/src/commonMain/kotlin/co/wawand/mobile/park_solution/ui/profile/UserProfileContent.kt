package co.wawand.mobile.park_solution.ui.profile

import AppColors
import AppElevation
import AppSpacing
import MessageBarState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import co.wawand.mobile.park_solution.shared.util.displayResult
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Bell
import compose.icons.fontawesomeicons.solid.Building
import compose.icons.fontawesomeicons.solid.Check
import compose.icons.fontawesomeicons.solid.Cog
import compose.icons.fontawesomeicons.solid.Crown
import compose.icons.fontawesomeicons.solid.Envelope
import compose.icons.fontawesomeicons.solid.IdCard
import compose.icons.fontawesomeicons.solid.Pen
import compose.icons.fontawesomeicons.solid.Phone
import compose.icons.fontawesomeicons.solid.User
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserProfileContent(messageBarState: MessageBarState) {
    val viewModel = koinViewModel<UserProfileViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    val userInformationState = viewModel.userInformationState
    val focusManager = LocalFocusManager.current

    userInformationState.displayResult(
        onLoading = { viewModel.setLoadingState(true) },
        onSuccess = { state ->
            viewModel.setLoadingState(false)
            viewModel.setUserInformation(state)
        },
        onError = { message ->
            messageBarState.addError(message)
            viewModel.setLoadingState(false)
        },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
        //.padding(horizontal = AppSpacing.xl, vertical = AppSpacing.lg)
    ) {
        // Header con título y botón de edición
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppSpacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            IconButton(
                onClick = {
                    if (uiState.isEditingProfile) {
                        viewModel.onSaveProfileClicked()
                    } else {
                        viewModel.onEditProfileClicked()
                    }
                },
            ) {
                Icon(
                    imageVector = if (uiState.isEditingProfile)
                        FontAwesomeIcons.Solid.Check
                    else
                        FontAwesomeIcons.Solid.Pen,
                    contentDescription = if (uiState.isEditingProfile) "Save" else "Edit",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Profile Picture & Basic Info Section
        SettingsCard(
            title = "Profile Information",
            icon = FontAwesomeIcons.Solid.User
        ) {
            // Profile Picture Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    // Profile Picture
                    Card(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = AppElevation.medium
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        if (uiState.user?.pictureUrl != null) {
                            // TODO: Use Coil AsyncImage here
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Solid.User,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Solid.User,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Edit Picture Button (only visible when editing)
                    /*AnimatedVisibility(visible = uiState.isEditingProfile) {
                        FloatingActionButton(
                            onClick = {
                                // TODO: Open image picker using Calf
                               // viewModel.selectProfilePicture()
                            },
                            modifier = Modifier.size(32.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Camera,
                                contentDescription = "Change Picture",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }*/
                }

                Spacer(modifier = Modifier.height(AppSpacing.md))
            }

            // Name Field
            if (uiState.isEditingProfile) {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.onNameChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Full Name",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.User,
                            contentDescription = "Name",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    shape = MaterialTheme.shapes.medium,
                )

                Spacer(modifier = Modifier.height(AppSpacing.sm))

                OutlinedTextField(
                    value = uiState.phoneNumber,
                    onValueChange = { viewModel.onPhoneNumberChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Phone Number",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Phone,
                            contentDescription = "Phone",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Unspecified
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    shape = MaterialTheme.shapes.medium,
                )
            } else {
                InfoRow(
                    icon = FontAwesomeIcons.Solid.User,
                    label = "Full Name",
                    value = uiState.user?.name ?: "Not set"
                )

                Spacer(modifier = Modifier.height(AppSpacing.md))

                InfoRow(
                    icon = FontAwesomeIcons.Solid.Phone,
                    label = "Phone Number",
                    value = if (uiState.user?.phoneNumber == null || uiState.user?.phoneNumber == "") "Not set" else uiState.user?.phoneNumber
                        ?: "Not set"
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.md))

        // Account Information Section (Read-only)
        SettingsCard(
            title = "Account Information",
            icon = FontAwesomeIcons.Solid.IdCard
        ) {
            InfoRow(
                icon = FontAwesomeIcons.Solid.Envelope,
                label = "Email Address",
                value = uiState.user?.email ?: "Not available"
            )

            Spacer(modifier = Modifier.height(AppSpacing.md))

            InfoRow(
                icon = FontAwesomeIcons.Solid.Building,
                label = "Company ID",
                value = uiState.user?.companyId ?: "Not assigned"
            )

            if (uiState.user?.isSuperUser == true) {
                Spacer(modifier = Modifier.height(AppSpacing.md))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Crown,
                        contentDescription = "Super User",
                        modifier = Modifier.size(20.dp),
                        tint = AppColors.Warning
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.sm))
                    Column {
                        Text(
                            text = "Account Type",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Administrator",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.xs))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = AppColors.Warning.copy(alpha = 0.1f)
                                ),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "ADMIN",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = AppColors.Warning,
                                    modifier = Modifier.padding(
                                        horizontal = AppSpacing.xs,
                                        vertical = AppSpacing.xxxs
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.md))

        // App Preferences Section
        SettingsCard(
            title = "Preferences",
            icon = FontAwesomeIcons.Solid.Cog
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Bell,
                        contentDescription = "Notifications",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.sm))
                    Column {
                        Text(
                            text = "Push Notifications",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Get notified about parking updates",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    enabled = false,
                    checked = false, //uiState.notificationsEnabled,
                    onCheckedChange = { /*viewModel.onNotificationsToggled(it)*/ },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.md))
    }
}

@Composable
fun SettingsCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AppElevation.small
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.lg)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = AppSpacing.md)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(AppSpacing.sm))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            content()
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(AppSpacing.sm))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


/*


@Composable
fun UserProfileContent(messageBarState: MessageBarState) {
    val viewModel = koinViewModel<UserProfileViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    val userInformationState = viewModel.userInformationState

    val focusManager = LocalFocusManager.current

    userInformationState.displayResult(
        onLoading = { viewModel.setLoadingState(true) },
        onSuccess = { state ->
            viewModel.setLoadingState(false)
            viewModel.setUserInformation(state)
        },
        onError = { message ->
            messageBarState.addError(message)
            viewModel.setLoadingState(false)
        },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )

            IconButton(
                onClick = {
                     if (uiState.isEditingProfile) {
                         // Save profile changes
                         viewModel.onSaveProfileClicked()
                     } else {
                         viewModel.onEditProfileClicked()
                     }
                },
            ) {
                Icon(
                    imageVector = if (uiState.isEditingProfile) FontAwesomeIcons.Solid.Check else FontAwesomeIcons.Solid.Pen,
                    contentDescription = if (uiState.isEditingProfile) "Save" else "Edit",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Profile Picture & Basic Info Section
        SettingsCard(
            title = "Profile Information",
            icon = FontAwesomeIcons.Solid.User
        ) {
            // Profile Picture Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    // Profile Picture
                    Card(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        if (uiState.user?.pictureUrl != null) {
                            // TODO: Use Coil AsyncImage here
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFE5E7EB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Solid.User,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.size(40.dp),
                                    tint = Color(0xFF9CA3AF)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFE5E7EB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Solid.User,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.size(40.dp),
                                    tint = Color(0xFF9CA3AF)
                                )
                            }
                        }
                    }

                    // Edit Picture Button (only visible when editing)
                    */
/*AnimatedVisibility(visible = uiState.isEditingProfile) {
                        FloatingActionButton(
                            onClick = {
                                // TODO: Open image picker using Calf
                               // viewModel.selectProfilePicture()
                            },
                            modifier = Modifier.size(32.dp),
                            containerColor = Color(0xFF4A6FE7),
                            contentColor = Color.White
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Camera,
                                contentDescription = "Change Picture",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }*//*

                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Name Field
            if (uiState.isEditingProfile) {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.onNameChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "Full Name") },
                    leadingIcon = {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.User,
                            contentDescription = "Name",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF4A6FE7)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4A6FE7),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    shape = RoundedCornerShape(8.dp),
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.phoneNumber,
                    onValueChange = { viewModel.onPhoneNumberChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "Phone Number") },
                    leadingIcon = {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Phone,
                            contentDescription = "Phone",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF4A6FE7)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4A6FE7),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Phone
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    shape = RoundedCornerShape(8.dp),
                )
            } else {
                InfoRow(
                    icon = FontAwesomeIcons.Solid.User,
                    label = "Full Name",
                    value = uiState.user?.name ?: "Not set"
                )

                Spacer(modifier = Modifier.height(16.dp))

                InfoRow(
                    icon = FontAwesomeIcons.Solid.Phone,
                    label = "Phone Number",
                    value = "Not set"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Account Information Section (Read-only)
        SettingsCard(
            title = "Account Information",
            icon = FontAwesomeIcons.Solid.IdCard
        ) {
            InfoRow(
                icon = FontAwesomeIcons.Solid.Envelope,
                label = "Email Address",
                value = uiState.user?.email ?: "Not available"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(
                icon = FontAwesomeIcons.Solid.Building,
                label = "Company ID",
                value = uiState.user?.companyId ?: "Not assigned"
            )

            if (uiState.user?.isSuperUser == true) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Crown,
                        contentDescription = "Super User",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFFF59E0B)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Account Type",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Administrator",
                                fontSize = 16.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFEF3C7)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "ADMIN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App Preferences Section
        SettingsCard(
            title = "Preferences",
            icon = FontAwesomeIcons.Solid.Cog
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Bell,
                        contentDescription = "Notifications",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Push Notifications",
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Get notified about parking updates",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                Switch(
                    enabled = false,
                    checked = false,//uiState.notificationsEnabled,
                    onCheckedChange = { */
/*viewModel.onNotificationsToggled(it)*//*
 },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4A6FE7),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE5E7EB)
                    )
                )
            }
        }
    }
}

@Composable
fun SettingsCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF4A6FE7)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            content()
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = Color(0xFF6B7280)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}*/
