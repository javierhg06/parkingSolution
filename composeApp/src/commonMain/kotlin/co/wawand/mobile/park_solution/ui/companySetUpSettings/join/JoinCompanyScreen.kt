package co.wawand.mobile.park_solution.ui.companySetUpSettings.join

import ContentWithMessageBar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.wawand.mobile.park_solution.ui.main.MainViewModel
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowRight
import compose.icons.fontawesomeicons.solid.ExclamationTriangle
import compose.icons.fontawesomeicons.solid.Key
import compose.icons.fontawesomeicons.solid.SignOutAlt
import compose.icons.fontawesomeicons.solid.Users
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@Composable
fun JoinCompanyScreen(
    navigateToHome: () -> Unit,
    navigateToSignIn: () -> Unit = {}
) {
    val mainViewModel = koinViewModel<MainViewModel>()
    val viewModel = koinViewModel<JoinCompanyViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val messageBarState = rememberMessageBarState()

    // Animation states
    var visible by remember { mutableStateOf(false) }
    val animatedOffset by animateDpAsState(
        targetValue = if (visible) 0.dp else 50.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "offset"
    )
    val animatedAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        visible = true
    }

    Scaffold(
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF8FAFC),
                            Color(0xFFE2E8F0),
                            Color.White
                        )
                    )
                )
        ) {
            ContentWithMessageBar(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                messageBarState = messageBarState,
                errorMaxLines = 2,
                errorContainerColor = Color(0xFFDC2626),
                errorContentColor = Color.White,
                contentBackgroundColor = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(scrollState)
                        .graphicsLayer {
                            translationY = animatedOffset.toPx()
                            alpha = animatedAlpha
                        },
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
                                mainViewModel.signOut(
                                    onSuccess = {
                                        navigateToSignIn()
                                    },
                                    onError = { message -> messageBarState.addError(message) }
                                )
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

                    EnhancedCompanyIcon()
                    EnhancedTitleSection()
                    Spacer(Modifier.height(48.dp))
                    EnhancedAccessCodeSection(
                        code = uiState.accessCode,
                        onCodeChange = viewModel::onAccessCodeChanged,
                        errorMessage = uiState.joinedCompanyErrorMessage,
                        isLoading = uiState.isJoiningCompany
                    )
                    Spacer(Modifier.height(32.dp))
                    EnhancedJoinCompanyButton(uiState.accessCode, viewModel::onJoinCompanyClick)
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }

    LaunchedEffect(uiState.redirectToHome) {
        if (uiState.redirectToHome) {
            navigateToHome()
        }
    }
}

@Composable
private fun EnhancedAccessCodeSection(
    code: String,
    onCodeChange: (String) -> Unit,
    errorMessage: String,
    isLoading: Boolean
) {
    val focusManager = LocalFocusManager.current

    Column(Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Key,
                contentDescription = "Access Code",
                tint = Color(0xFF3B82F6),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Company Access Code",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B),
            )
        }

        Spacer(Modifier.height(20.dp))

        if (isLoading) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE0F2FE)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF3B82F6),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Verifying code...",
                            fontSize = 14.sp,
                            color = Color(0xFF3B82F6),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { newValue ->
                            if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                                onCodeChange(newValue)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        textStyle = TextStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 8.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF1E293B)
                        ),
                        placeholder = {
                            Text(
                                text = "000000",
                                fontSize = 32.sp,
                                color = Color(0xFFCBD5E1),
                                fontWeight = FontWeight.Black,
                                letterSpacing = 8.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = errorMessage.isNotEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            errorBorderColor = Color(0xFFEF4444),
                            focusedContainerColor = Color(0xFFFAFAFA),
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        )
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.ExclamationTriangle,
                                contentDescription = "Error",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage,
                                color = Color(0xFFEF4444),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = "Enter the 6-digit code exactly as provided by your company administrator",
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EnhancedCompanyIcon() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(120.dp)
    ) {
        // Glow effect
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = Color(0xFF3B82F6).copy(alpha = 0.1f)
        ) {}

        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = Color(0xFF3B82F6).copy(alpha = 0.15f)
        ) {}

        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = Color(0xFFDEF7EC),
            shadowElevation = 8.dp
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Users,
                    contentDescription = "Company",
                    modifier = Modifier.size(32.dp),
                    tint = Color(0xFF3B82F6)
                )
            }
        }
    }
}

@Composable
private fun EnhancedTitleSection() {
    Text(
        text = "👋 Join Your Team",
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF0F172A),
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(12.dp))
    Text(
        text = "Connect with your company's parking workspace using your unique access code",
        fontSize = 18.sp,
        color = Color(0xFF64748B),
        textAlign = TextAlign.Center,
        lineHeight = 26.sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun EnhancedJoinCompanyButton(accessCode: String, onClick: () -> Unit = {}) {
    val isCodeValid = accessCode.length == 6

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        enabled = isCodeValid,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isCodeValid) Color(0xFF3B82F6) else Color(0xFFCBD5E1),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFE2E8F0),
            disabledContentColor = Color(0xFF94A3B8)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isCodeValid) 6.dp else 0.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = isCodeValid,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                Row {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Users,
                        contentDescription = "Join",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                }
            }

            Text(
                text = if (isCodeValid) "Join Company" else "Enter 6-digit code",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            AnimatedVisibility(
                visible = isCodeValid,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                Row {
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.ArrowRight,
                        contentDescription = "Arrow right",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
