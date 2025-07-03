package co.wawand.mobile.park_solution.ui.companySettings

import AppColors
import AppSpacing
import MessageBarState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import co.wawand.mobile.park_solution.shared.util.displayResult
import co.wawand.mobile.park_solution.ui.profile.SettingsCard
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Building
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.Check
import compose.icons.fontawesomeicons.solid.InfoCircle
import compose.icons.fontawesomeicons.solid.MapMarkerAlt
import compose.icons.fontawesomeicons.solid.Minus
import compose.icons.fontawesomeicons.solid.Pen
import compose.icons.fontawesomeicons.solid.Plus
import compose.icons.fontawesomeicons.solid.Wifi
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun CompanySettingsContent(messageBarState: MessageBarState) {
    val viewModel = koinViewModel<CompanyConfigViewModel>()
    val companyConfigState = viewModel.companyConfigState
    val uiState by viewModel.uiState.collectAsState()

    val focusManager = LocalFocusManager.current

    companyConfigState.displayResult(
        onLoading = {
            viewModel.setIsLoading(true)
        },
        onSuccess = { state ->
            viewModel.setIsLoading(false)
            viewModel.setCompanyConfig(state)
        },
        onError = { message ->
            viewModel.setIsLoading(false)
            messageBarState.addError(message)
        }
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
                text = "Company Details",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (uiState.currentUser?.isSuperUser == true) {
                IconButton(
                    onClick = {
                        if (uiState.isEditingCompanyConfig) {
                            viewModel.onCompleteEditCompanyConfigClick(
                                onError = { message ->
                                    messageBarState.addError(message)
                                }
                            )
                        } else {
                            viewModel.onEditCompanyConfigClick()
                        }
                    },
                ) {
                    Icon(
                        imageVector = if (uiState.isEditingCompanyConfig)
                            FontAwesomeIcons.Solid.Check
                        else
                            FontAwesomeIcons.Solid.Pen,
                        contentDescription = if (uiState.isEditingCompanyConfig) "Save" else "Edit",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Company Information Section
        SettingsCard(
            title = "Company Information",
            icon = FontAwesomeIcons.Solid.Building
        ) {
            if (uiState.isEditingCompanyConfig) {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.onNameChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Company Name",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Building,
                            contentDescription = "Company Name",
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
                    maxLines = 2,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    shape = MaterialTheme.shapes.medium,
                )

                Spacer(modifier = Modifier.height(AppSpacing.sm))

                OutlinedTextField(
                    value = uiState.address,
                    onValueChange = { viewModel.onAddressChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Company Address",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.MapMarkerAlt,
                            contentDescription = "Address",
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
                    maxLines = 3,
                    minLines = 1,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    shape = MaterialTheme.shapes.medium,
                )
            } else {
                InfoRow(
                    icon = FontAwesomeIcons.Solid.Building,
                    label = "Name",
                    value = uiState.companyConfig?.name ?: "Not set"
                )

                Spacer(modifier = Modifier.height(AppSpacing.md))

                InfoRow(
                    icon = FontAwesomeIcons.Solid.MapMarkerAlt,
                    label = "Address",
                    value = uiState.companyConfig?.address ?: "Not set"
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.md))

        // WiFi Configuration Section
        SettingsCard(
            title = "WiFi Configuration",
            icon = FontAwesomeIcons.Solid.Wifi
        ) {
            if (uiState.isEditingCompanyConfig) {
                OutlinedTextField(
                    value = uiState.wifiNetworkName,
                    onValueChange = { viewModel.onWifiNetworkNameChanged(it) },
                    placeholder = {
                        Text(
                            text = "WiFi Network Name",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Wifi,
                            contentDescription = "WiFi",
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
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    shape = MaterialTheme.shapes.medium,
                )
            } else {
                InfoRow(
                    icon = FontAwesomeIcons.Solid.Wifi,
                    label = "WiFi Network",
                    value = uiState.companyConfig?.wifiNetwork ?: "Not configured"
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.sm))

            // Info message
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(AppSpacing.sm)
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.InfoCircle,
                    contentDescription = "Info",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(AppSpacing.xs))
                Text(
                    text = "Users must connect to this WiFi to mark parking spaces",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.md))

        // Parking Spaces Management Section
        SettingsCard(
            title = "Parking Spaces Management",
            icon = FontAwesomeIcons.Solid.Car
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.isEditingCompanyConfig) {
                    Column {
                        Text(
                            text = "Total Parking Spaces",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.xxs))
                        Text(
                            text = "${uiState.companyParkingNumber} spaces",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    InfoRow(
                        icon = FontAwesomeIcons.Solid.Car,
                        label = "Total Spaces",
                        value = "${uiState.companyConfig?.totalParkingSpaces ?: 0} spaces"
                    )
                }

                AnimatedVisibility(visible = uiState.isEditingCompanyConfig) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                    ) {
                        // Add button
                        FloatingActionButton(
                            onClick = {
                                viewModel.onCompanyParkingNumberChanged(
                                    (uiState.companyParkingNumber + 1)
                                )
                            },
                            modifier = Modifier.size(48.dp),
                            containerColor = AppColors.Success,
                            contentColor = Color.White,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Plus,
                                contentDescription = "Add space",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Remove button
                        FloatingActionButton(
                            onClick = {
                                if (uiState.companyParkingNumber > 1) {
                                    viewModel.onCompanyParkingNumberChanged(
                                        (uiState.companyParkingNumber - 1)
                                    )
                                }
                            },
                            modifier = Modifier.size(48.dp),
                            containerColor = AppColors.Error,
                            contentColor = Color.White,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Minus,
                                contentDescription = "Remove space",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.md))
    }
}

/*@Composable
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
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            content()
        }
    }
}*/

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
fun CompanySettingsContent(messageBarState: MessageBarState) {
    val viewModel = koinViewModel<CompanyConfigViewModel>()
    val companyConfigState = viewModel.companyConfigState
    val uiState by viewModel.uiState.collectAsState()

    val focusManager = LocalFocusManager.current

    companyConfigState.displayResult(
        onLoading = {
            viewModel.setIsLoading(true)
        },
        onSuccess = { state ->
            viewModel.setIsLoading(false)
            viewModel.setCompanyConfig(state)
        },
        onError = { message ->
            viewModel.setIsLoading(false)
            messageBarState.addError(message)
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Company Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )

            if (uiState.currentUser?.isSuperUser == true) {
                IconButton(
                    onClick = {
                        if (uiState.isEditingCompanyConfig) {
                            viewModel.onCompleteEditCompanyConfigClick(
                                onError = { message ->
                                    messageBarState.addError(message)
                                }
                            )
                        } else {
                            viewModel.onEditCompanyConfigClick()
                        }
                    },
                ) {
                    Icon(
                        imageVector = if (uiState.isEditingCompanyConfig) FontAwesomeIcons.Solid.Check else FontAwesomeIcons.Solid.Pen,
                        contentDescription = "Edit",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Company Information Section
        SettingsCard(
            title = "Company Information",
            icon = FontAwesomeIcons.Solid.Building
        ) {
            if (uiState.isEditingCompanyConfig) {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.onNameChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "Company Name") },
                    leadingIcon = {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Building,
                            contentDescription = "Company Name",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF4A6FE7)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4A6FE7),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    maxLines = 2,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    shape = RoundedCornerShape(8.dp),
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.address,
                    onValueChange = { viewModel.onAddressChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "Company Address") },
                    leadingIcon = {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.MapMarkerAlt,
                            contentDescription = "Address",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF4A6FE7)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4A6FE7),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    maxLines = 3,
                    minLines = 1,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    shape = RoundedCornerShape(8.dp),
                )
            } else {
                InfoRow(
                    icon = FontAwesomeIcons.Solid.Building,
                    label = "Name",
                    value = uiState.companyConfig?.name ?: "Not set"
                )

                Spacer(modifier = Modifier.height(16.dp))

                InfoRow(
                    icon = FontAwesomeIcons.Solid.MapMarkerAlt,
                    label = "Address",
                    value = uiState.companyConfig?.address ?: "Not set"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // WiFi Configuration Section
        SettingsCard(
            title = "WiFi Configuration",
            icon = FontAwesomeIcons.Solid.Wifi
        ) {
            if (uiState.isEditingCompanyConfig) {
                OutlinedTextField(
                    value = uiState.wifiNetworkName,
                    onValueChange = { viewModel.onWifiNetworkNameChanged(it) },
                    placeholder = { Text(text = "WiFi Network Name") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Wifi,
                            contentDescription = "WiFi",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF4A6FE7)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4A6FE7),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    shape = RoundedCornerShape(8.dp),
                )
            } else {
                InfoRow(
                    icon = FontAwesomeIcons.Solid.Wifi,
                    label = "WiFi Network",
                    value = uiState.companyConfig?.wifiNetwork ?: "Not configured"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.InfoCircle,
                    contentDescription = "Info",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Users must connect to this WiFi to mark parking spaces",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Parking Spaces Management Section
        SettingsCard(
            title = "Parking Spaces Management",
            icon = FontAwesomeIcons.Solid.Car
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.isEditingCompanyConfig) {
                    Column {
                        Text(
                            text = "Total Parking Spaces",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF374151)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${uiState.companyParkingNumber} spaces",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A6FE7)
                        )
                    }
                } else {
                    InfoRow(
                        icon = FontAwesomeIcons.Solid.Car,
                        label = "Total Spaces",
                        value = "${uiState.companyConfig?.totalParkingSpaces ?: 0} spaces"
                    )
                }

                AnimatedVisibility(visible = uiState.isEditingCompanyConfig) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Add button
                        FloatingActionButton(
                            onClick = {
                                viewModel.onCompanyParkingNumberChanged(
                                    (uiState.companyParkingNumber + 1)
                                )
                            },
                            modifier = Modifier.size(48.dp),
                            containerColor = Color(0xFF10B981),
                            contentColor = Color.White
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Plus,
                                contentDescription = "Add space",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Remove button
                        FloatingActionButton(
                            onClick = {
                                if (uiState.companyParkingNumber > 1) {
                                    viewModel.onCompanyParkingNumberChanged(
                                        (uiState.companyParkingNumber - 1)
                                    )
                                }
                            },
                            modifier = Modifier.size(48.dp),
                            containerColor = Color(0xFFEF4444),
                            contentColor = Color.White
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Minus,
                                contentDescription = "Remove space",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
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
                */
/*Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF4A6FE7)
                )
                Spacer(modifier = Modifier.width(12.dp))*//*

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
