package co.wawand.mobile.park_solution.ui.companySetUpSettings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowRight
import compose.icons.fontawesomeicons.solid.Building
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.Check
import compose.icons.fontawesomeicons.solid.Copy
import compose.icons.fontawesomeicons.solid.Key
import compose.icons.fontawesomeicons.solid.MapMarkerAlt
import compose.icons.fontawesomeicons.solid.Wifi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
fun CreateCompanyScreen(viewModel: CompanySetUpSettingsViewModel, uiState: CompanySetUpSettingsState) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CompanyHeader()
            CompanyDescription()
            Spacer(Modifier.height(40.dp))
            CompanyForm(uiState, viewModel, focusManager)
            Spacer(Modifier.height(32.dp))
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
    Surface(
        modifier = Modifier.size(80.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFE8D5FF)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Building,
                contentDescription = "Building",
                tint = Color(0xFF8B5CF6),
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun CompanyDescription() {
    Spacer(Modifier.height(24.dp))
    Text(
        text = "Create Your Company",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1F2937),
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = "Set up your parking management system",
        fontSize = 18.sp,
        color = Color(0xFF6B7280),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun CompanyForm(
    uiState: CompanySetUpSettingsState,
    viewModel: CompanySetUpSettingsViewModel,
    focusManager: FocusManager
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        LabeledTextField(
            label = "Company Name",
            value = uiState.companyName,
            onValueChange = viewModel::onCompanyNameChanged,
            placeholder = "Enter your company name",
            icon = FontAwesomeIcons.Solid.Building,
            imeAction = ImeAction.Next,
            onDone = { focusManager.moveFocus(FocusDirection.Down) }
        )

        LabeledTextField(
            label = "WiFi Network",
            value = uiState.wifiNetwork,
            onValueChange = viewModel::onWifiSSIDChanged,
            placeholder = "Enter WiFi network name",
            icon = FontAwesomeIcons.Solid.Wifi,
            imeAction = ImeAction.Next,
            onDone = { focusManager.moveFocus(FocusDirection.Down) }
        )

        LabeledTextField(
            label = "Company Address",
            value = uiState.companyAddress,
            onValueChange = viewModel::onCompanyAddressChanged,
            placeholder = "Enter your company address",
            icon = FontAwesomeIcons.Solid.MapMarkerAlt,
            imeAction = ImeAction.Next,
            onDone = { focusManager.moveFocus(FocusDirection.Down) },
            maxLines = 3
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LabeledTextField(
                label = "Latitude",
                value = uiState.latitude,
                onValueChange = viewModel::onLatitudeChanged,
                placeholder = "e.g., 10.9639",
                icon = FontAwesomeIcons.Solid.MapMarkerAlt,
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
                onDone = { focusManager.moveFocus(FocusDirection.Right) },
                modifier = Modifier.weight(1f)
            )

            LabeledTextField(
                label = "Longitude",
                value = uiState.longitude,
                onValueChange = viewModel::onLongitudeChanged,
                placeholder = "e.g., -74.7813",
                icon = FontAwesomeIcons.Solid.MapMarkerAlt,
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
                onDone = { focusManager.moveFocus(FocusDirection.Down) },
                modifier = Modifier.weight(1f)
            )
        }

        LabeledTextField(
            label = "Total Parking Spaces",
            value = uiState.totalParkingSpaces.toString(),
            onValueChange = {
                val parsed = it.toIntOrNull()
                if (it.all(Char::isDigit) && (parsed == null || parsed in 1..100)) {
                    viewModel.onParkingSpacesChanged(parsed ?: 2)
                }
            },
            placeholder = "e.g., 10",
            icon = FontAwesomeIcons.Solid.Car,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
            onDone = { focusManager.clearFocus() }
        )
    }
}

@Composable
private fun AccessCodeInfo() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF3F4F6)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Key,
                contentDescription = "Access Code",
                tint = Color(0xFF8B5CF6),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "Access Code",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF8B5CF6)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "After creating your company, you'll receive a unique 6-digit code that your team members can use to join.",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 20.sp
                )
            }
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
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF8B5CF6)
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
            contentDescription = "Arrow Right",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
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
                Text(text = placeholder, color = Color(0xFF9CA3AF))
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF8B5CF6),
                unfocusedBorderColor = Color(0xFFD1D5DB)
            ),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            maxLines = maxLines,
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(20.dp)
                )
            }
        )
    }
}

@Composable
fun LoadingScreen() {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CompanyDescription()

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Color(0xFF8B5CF6),
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Creating your company...",
                fontSize = 16.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SuccessScreen(
    accessCode: String,
    navigateToHome: () -> Unit
) {

    Scaffold { innerPadding ->
        val clipboardManager = LocalClipboardManager.current
        var showCopiedMessage by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CompanyDescription()

            Spacer(modifier = Modifier.height(48.dp))

            // Success Icon
            Surface(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFD1FAE5)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Check,
                        contentDescription = "Success",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Company Created Successfully!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your company access code is:",
                fontSize = 18.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Access Code Display
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF9FAFB),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = accessCode,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        textAlign = TextAlign.Center,
                        letterSpacing = 4.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                           clipboardManager.setText(AnnotatedString(accessCode))
                            showCopiedMessage = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Copy,
                            contentDescription = "Copy",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (showCopiedMessage) "Copied!" else "Copy Code",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Share this code with your team members so they can join your company.",
                fontSize = 16.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Redirecting to parking dashboard...",
                fontSize = 16.sp,
                color = Color(0xFF3B82F6),
                textAlign = TextAlign.Center
            )
        }

        // Reset copied message after 2 seconds
        LaunchedEffect(showCopiedMessage) {
            if (showCopiedMessage) {
                delay(2000)
                showCopiedMessage = false
            }
        }

        scope.launch {
            delay(2000)
            navigateToHome()
        }
    }
}

@Composable
fun ErrorScreen(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Error Creating Company",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFDC2626),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = errorMessage,
                fontSize = 16.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B5CF6)
                )
            ) {
                Text("Try Again")
            }
        }
    }
}
