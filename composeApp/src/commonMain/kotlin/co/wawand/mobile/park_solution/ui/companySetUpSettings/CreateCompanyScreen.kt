package co.wawand.mobile.park_solution.ui.companySetUpSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.wawand.mobile.park_solution.ui.companySetUpSettings.stateContent.ErrorScreen
import co.wawand.mobile.park_solution.ui.companySetUpSettings.stateContent.LoadingScreen
import co.wawand.mobile.park_solution.ui.companySetUpSettings.stateContent.SuccessScreen
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowRight
import compose.icons.fontawesomeicons.solid.Building
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.Key
import compose.icons.fontawesomeicons.solid.MapMarkerAlt
import compose.icons.fontawesomeicons.solid.Minus
import compose.icons.fontawesomeicons.solid.Plus
import compose.icons.fontawesomeicons.solid.SignOutAlt
import compose.icons.fontawesomeicons.solid.Wifi
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun CreateCompanyFlow(
    navigateToHome: () -> Unit = {},
) {
    val viewModel = koinViewModel<CompanySetUpSettingsViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    when (uiState.createCompanyState) {
        CreateCompanyState.Form -> {
            CreateCompanyScreen(viewModel, uiState)
        }

        CreateCompanyState.Loading -> {
            LoadingScreen()
        }

        CreateCompanyState.Success -> {
            SuccessScreen(
                accessCode = uiState.existingConfig?.accessCode ?: "",
                navigateToHome = navigateToHome
            )
        }

        CreateCompanyState.Error -> {
            ErrorScreen(
                errorMessage = uiState.errorMessage,
                onRetry = { viewModel.onRetry() }
            )
        }
    }
}

@Composable
private fun CreateCompanyScreen(
    viewModel: CompanySetUpSettingsViewModel,
    uiState: CompanySetUpSettingsState
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(
                    top = it.calculateTopPadding() + 16.dp,
                    bottom = it.calculateBottomPadding() + 16.dp
                )
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            // Sign Out button at the top
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        // Add sign out logic here - you can pass this as a parameter to the composable
                        // For now, it's just a placeholder
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF64748B)
                    )
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.SignOutAlt,
                        contentDescription = "Sign Out",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign Out",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            CompanyHeader()
            Spacer(Modifier.height(32.dp))
            CompanyForm(uiState, viewModel, focusManager)
            Spacer(Modifier.height(24.dp))
            AccessCodeInfo()
            Spacer(Modifier.height(32.dp))
            CreateCompanyButton(
                onClick = viewModel::saveCompanyConfigAndUpdateCurrentUser,
                enabled = viewModel.isFormValid()
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CompanyHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = Color(0xFF4A90E2).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Building,
                contentDescription = "Company Setup",
                tint = Color(0xFF4A90E2),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Create Company",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Set up your parking management system",
            fontSize = 16.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CompanyForm(
    uiState: CompanySetUpSettingsState,
    viewModel: CompanySetUpSettingsViewModel,
    focusManager: FocusManager
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        LabeledTextField(
            label = "Company Name",
            value = uiState.companyName,
            onValueChange = viewModel::onCompanyNameChanged,
            placeholder = "Enter company name",
            icon = FontAwesomeIcons.Solid.Building,
            imeAction = ImeAction.Next,
            onDone = { focusManager.moveFocus(FocusDirection.Down) }
        )

        LabeledTextField(
            label = "WiFi Network",
            value = uiState.wifiNetwork,
            onValueChange = viewModel::onWifiSSIDChanged,
            placeholder = "Network name (optional)",
            icon = FontAwesomeIcons.Solid.Wifi,
            imeAction = ImeAction.Next,
            onDone = { focusManager.moveFocus(FocusDirection.Down) }
        )

        LabeledTextField(
            label = "Company Address",
            value = uiState.companyAddress,
            onValueChange = viewModel::onCompanyAddressChanged,
            placeholder = "Street address",
            icon = FontAwesomeIcons.Solid.MapMarkerAlt,
            imeAction = ImeAction.Next,
            onDone = { focusManager.moveFocus(FocusDirection.Down) },
            maxLines = 2
        )

        LabeledParkSpaces(
            label = "Parking Spaces",
            value = uiState.totalParkingSpaces,
            onValueChange = { viewModel.onParkingSpacesChanged(it) },
            icon = FontAwesomeIcons.Solid.Car,
        )
    }
}

@Composable
private fun AccessCodeInfo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF4A90E2).copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = FontAwesomeIcons.Solid.Key,
            contentDescription = "Access Code",
            tint = Color(0xFF4A90E2),
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "Access Code",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "You'll receive a 6-digit code for team members to join your company.",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun CreateCompanyButton(onClick: () -> Unit, enabled: Boolean) {
    Button(
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4A90E2),
            disabledContainerColor = Color(0xFF4A90E2).copy(alpha = 0.4f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(
            text = "Create Company",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Spacer(Modifier.width(8.dp))
        Icon(
            imageVector = FontAwesomeIcons.Solid.ArrowRight,
            contentDescription = "Continue",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun LabeledParkSpaces(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFFE2E8F0),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "$value spaces",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Remove button
                IconButton(
                    onClick = {
                        if (value > 1) {
                            onValueChange(value - 1)
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = if (value > 1) Color(0xFFEF4444).copy(alpha = 0.1f) else Color(
                                0xFFF1F5F9
                            ),
                            shape = CircleShape
                        ),
                    enabled = value > 1
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Minus,
                        contentDescription = "Remove space",
                        modifier = Modifier.size(14.dp),
                        tint = if (value > 1) Color(0xFFEF4444) else Color(0xFF94A3B8)
                    )
                }

                // Add button
                IconButton(
                    onClick = {
                        onValueChange(value + 1)
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = Color(0xFF10B981).copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Plus,
                        contentDescription = "Add space",
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF10B981)
                    )
                }
            }
        }
    }
}

@Composable
private fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onDone: () -> Unit,
    maxLines: Int = 1
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF94A3B8),
                    fontSize = 16.sp
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4A90E2),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            maxLines = maxLines,
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(18.dp)
                )
            }
        )
    }
}



