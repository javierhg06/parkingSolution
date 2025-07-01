package co.wawand.mobile.park_solution.ui.companySetUpSettings

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowRight
import compose.icons.fontawesomeicons.solid.Link
import compose.icons.fontawesomeicons.solid.Users
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun JoinCompanyScreen(navigateToHome: () -> Unit) {
    val viewModel = koinViewModel<CompanySetUpSettingsViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(containerColor = Color(0xFFF5F5F5)) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))
            CompanyIcon()
            TitleSection()
            Spacer(Modifier.height(48.dp))
            AccessCodeSection(
                code = uiState.accessCode,
                onCodeChange = viewModel::onAccessCodeChanged,
                errorMessage = uiState.joinedCompanyErrorMessage,
                isLoading = uiState.isJoiningCompany
            )
            Spacer(Modifier.height(32.dp))
            NeedCodeCard()
            Spacer(Modifier.height(24.dp))
            JoinCompanyButton(uiState.accessCode, viewModel::onJoinCompanyClick)
            Spacer(Modifier.height(24.dp))
            SupportText()
            Spacer(Modifier.height(24.dp))
        }
    }

    LaunchedEffect(uiState.redirectToHome) {
        if (uiState.redirectToHome) {
            navigateToHome()
        }
    }
}

@Composable
private fun AccessCodeSection(code: String, onCodeChange: (String) -> Unit, errorMessage: String, isLoading: Boolean) {
    val focusManager = LocalFocusManager.current
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = "Company Access Code",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .background(Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            OutlinedTextField(
                value = code,
                onValueChange = { newValue ->
                    if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                        onCodeChange(newValue)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp),
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 8.sp,
                    textAlign = TextAlign.Center
                ),
                placeholder = {
                    Text(
                        text = "000000",
                        fontSize = 24.sp,
                        color = Color(0xFFCCCCCC),
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 8.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                isError = errorMessage.isNotEmpty(),
                supportingText = {
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2196F3),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )
        }


        Spacer(Modifier.height(12.dp))
        Text(
            text = "Enter the 6-digit code exactly as provided by your company administrator",
            fontSize = 14.sp,
            color = Color(0xFF666666),
            lineHeight = 20.sp
        )
    }
}


@Composable
private fun CompanyIcon() {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(Color(0xFFE3F2FD), shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = FontAwesomeIcons.Solid.Users,
            contentDescription = "Company",
            modifier = Modifier.size(40.dp),
            tint = Color(0xFF2196F3)
        )
    }
}


@Composable
private fun TitleSection() {
    Text(
        text = "Join Your Company",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1A1A1A),
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(16.dp))
    Text(
        text = "Enter the 6-digit code provided by your administrator",
        fontSize = 16.sp,
        color = Color(0xFF666666),
        textAlign = TextAlign.Center,
        lineHeight = 24.sp
    )
}


@Composable
private fun NeedCodeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8FF)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Link,
                contentDescription = "Link",
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp),
                tint = Color(0xFF2196F3)
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = "Need a Code?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2196F3)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Contact your company administrator to get your unique access code. This code links you to your company's parking system.",
                    fontSize = 14.sp,
                    color = Color(0xFF2196F3),
                    lineHeight = 20.sp
                )
            }
        }
    }
}


@Composable
private fun JoinCompanyButton(accessCode: String, onClick: () -> Unit = {}) {
    val isCodeValid = accessCode.length == 6

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = isCodeValid,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isCodeValid) Color(0xFF2196F3) else Color(0xFFCCCCCC),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Join Company",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = FontAwesomeIcons.Solid.ArrowRight,
                contentDescription = "Arrow right",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}


@Composable
private fun SupportText() {
    Text(
        text = "Need help? Contact support",
        fontSize = 14.sp,
        color = Color(0xFF666666),
        textAlign = TextAlign.Center
    )
}
