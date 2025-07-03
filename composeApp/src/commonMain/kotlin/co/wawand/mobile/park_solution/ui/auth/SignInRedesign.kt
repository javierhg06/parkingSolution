package co.wawand.mobile.park_solution.ui.auth

import AppColors
import AppElevation
import AppShapes
import AppSpacing
import AppTypography
import ContentWithMessageBar
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.Clock
import compose.icons.fontawesomeicons.solid.MapMarkerAlt
import compose.icons.fontawesomeicons.solid.Users
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import parksolution.composeapp.generated.resources.Res
import parksolution.composeapp.generated.resources.google_icon
import rememberMessageBarState


@Composable
fun SignInScreenRedesigned(
    navigateToHome: () -> Unit = {},
    navigateToCreateCompany: () -> Unit = {},
    navigateToJoinCompany: () -> Unit = {},
) {
    val viewModel = koinViewModel<SignInViewModel>()
    val messageBarState = rememberMessageBarState()
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }

    // Subtle floating animation for parking spaces
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    // Gentle pulsing animation for the logo
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    ContentWithMessageBar(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        errorMaxLines = 2,
        messageBarState = messageBarState,
        errorContainerColor = AppColors.Error,
        errorContentColor = Color.White,
        successContainerColor = AppColors.Success,
        successContentColor = Color.White,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AppColors.Primary,
                            AppColors.PrimaryVariant,
                            AppColors.PrimaryVariant.copy(alpha = 0.8f)
                        )
                    )
                )
        ) {
            // Background parking space decorations
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xl),
                contentPadding = PaddingValues(
                    horizontal = AppSpacing.xxl,
                    vertical = AppSpacing.xxxl
                )
            ) {
                items(8) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(
                                x = if (index % 2 == 0) (-20).dp else 20.dp,
                                y = floatingOffset.dp * if (index % 3 == 0) 1f else -0.5f
                            ),
                        horizontalArrangement = if (index % 2 == 0) Arrangement.Start else Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(40.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.08f),
                                    shape = AppShapes.small
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.12f),
                                    shape = AppShapes.small
                                )
                        )
                    }
                }
            }

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // App logo with company focus
                Card(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulseScale),
                    shape = AppShapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = AppElevation.large
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Car,
                                contentDescription = "Park Solution Logo",
                                modifier = Modifier.size(48.dp),
                                tint = AppColors.Primary
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            Text(
                                text = "PARK",
                                style = AppTypography.titleLarge,
                                color = AppColors.Primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.xxxl))

                // Main heading
                Text(
                    text = "Park Solution",
                    style = AppTypography.headlineLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(AppSpacing.sm))

                Text(
                    text = "Smart Parking Management",
                    style = AppTypography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                Text(
                    text = "Streamline your company's parking spaces with intelligent allocation and real-time management",
                    style = AppTypography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(AppSpacing.xxxl))

                // Google Sign In Button with improved styling
                GoogleButtonUiContainerFirebase(
                    linkAccount = false,
                    onResult = { result ->
                        result.onSuccess { user ->
                            viewModel.signInAndRedirectUser(
                                user = user,
                                onSuccess = { redirectAfterSignIn ->
                                    scope.launch {
                                        messageBarState.addSuccess("Welcome to Park Solution!")
                                        delay(1500)
                                        when (redirectAfterSignIn) {
                                            RedirectAfterSignIn.Home -> navigateToHome()
                                            RedirectAfterSignIn.JoinCompany -> navigateToJoinCompany()
                                            RedirectAfterSignIn.CreateCompany -> navigateToCreateCompany()
                                        }
                                        isLoading = false
                                    }
                                },
                                onError = { error ->
                                    messageBarState.addError("Failed to sign in: $error")
                                    isLoading = false
                                }
                            )
                        }.onFailure { error ->
                            if (error.message?.contains("A network error") == true) {
                                messageBarState.addError("Please check your internet connection and try again.")
                            } else if (error.message?.contains("IdToken is null") == true) {
                                messageBarState.addError("Sign in was cancelled.")
                            } else {
                                messageBarState.addError("Unable to sign in. Please try again.")
                            }
                            isLoading = false
                        }
                    }
                ) {
                    Button(
                        onClick = {
                            isLoading = true
                            this@GoogleButtonUiContainerFirebase.onClick()
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF1F1F1F),
                            disabledContainerColor = Color.White.copy(alpha = 0.8f),
                            disabledContentColor = Color(0xFF1F1F1F).copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(32.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = AppElevation.medium,
                            pressedElevation = AppElevation.large,
                            disabledElevation = AppElevation.small
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = AppColors.Primary,
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.width(AppSpacing.md))
                                Text(
                                    text = "Signing you in...",
                                    style = AppTypography.bodyLarge
                                )
                            } else {
                                Icon(
                                    painter = painterResource(Res.drawable.google_icon),
                                    contentDescription = "Google",
                                    modifier = Modifier.size(28.dp),
                                    tint = Color.Unspecified
                                )
                                Spacer(modifier = Modifier.width(AppSpacing.md))
                                Text(
                                    text = "Continue with Google",
                                    style = AppTypography.bodyLarge
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.xxl))

                // Feature highlights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FeatureItem(
                        icon = FontAwesomeIcons.Solid.Users,
                        text = "Team\nManagement"
                    )
                    FeatureItem(
                        icon = FontAwesomeIcons.Solid.MapMarkerAlt,
                        text = "Real-time\nTracking"
                    )
                    FeatureItem(
                        icon = FontAwesomeIcons.Solid.Clock,
                        text = "Smart\nScheduling"
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.xl))

                // Footer text
                Text(
                    text = "By continuing, you agree to our Terms of Service and Privacy Policy",
                    style = AppTypography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            // Subtle corner decorations
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-40).dp)
                    .size(120.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.04f),
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-60).dp, y = 60.dp)
                    .size(160.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.03f),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    text: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.White.copy(alpha = 0.9f)
            )
        }
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        Text(
            text = text,
            style = AppTypography.bodySmall.copy(fontSize = 11.sp),
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}









/*


@Composable
fun SignInScreenRedesigned(
    navigateToHome: () -> Unit = {},
    navigateToCreateCompany: () -> Unit = {},
    navigateToJoinCompany: () -> Unit = {},
) {
    val viewModel = koinViewModel<SignInViewModel>()
    val messageBarState = rememberMessageBarState()
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }

    // Subtle floating animation for parking spaces
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    // Gentle pulsing animation for the logo
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    ContentWithMessageBar(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        errorMaxLines = 2,
        messageBarState = messageBarState,
        errorContainerColor = Color(0xFFD32F2F),
        errorContentColor = Color.White,
        successContainerColor = Color(0xFF388E3C),
        successContentColor = Color.White,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4A90E2), // Professional blue
                            Color(0xFF357ABD), // Deeper blue
                            Color(0xFF2E5B8C)  // Dark blue
                        )
                    )
                )
        ) {
            // Background parking space decorations
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                contentPadding = PaddingValues(horizontal = 40.dp, vertical = 60.dp)
            ) {
                items(8) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(
                                x = if (index % 2 == 0) (-20).dp else 20.dp,
                                y = floatingOffset.dp * if (index % 3 == 0) 1f else -0.5f
                            ),
                        horizontalArrangement = if (index % 2 == 0) Arrangement.Start else Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(40.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // App logo with company focus
                Card(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulseScale),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Car,
                                contentDescription = "Park Solution Logo",
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFF4A90E2)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "PARK",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A90E2)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Main heading
                Text(
                    text = "Park Solution",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Smart Parking Management",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Streamline your company's parking spaces with intelligent allocation and real-time management",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(64.dp))

                // Google Sign In Button with improved styling
                GoogleButtonUiContainerFirebase(
                    linkAccount = false,
                    onResult = { result ->
                        result.onSuccess { user ->
                            viewModel.signInAndRedirectUser(
                                user = user,
                                onSuccess = { redirectAfterSignIn ->
                                    scope.launch {
                                        messageBarState.addSuccess("Welcome to Park Solution!")
                                        delay(1500)
                                        when (redirectAfterSignIn) {
                                            RedirectAfterSignIn.Home -> navigateToHome()
                                            RedirectAfterSignIn.JoinCompany -> navigateToJoinCompany()
                                            RedirectAfterSignIn.CreateCompany -> navigateToCreateCompany()
                                        }
                                        isLoading = false
                                    }
                                },
                                onError = { error ->
                                    messageBarState.addError("Failed to sign in: $error")
                                    isLoading = false
                                }
                            )
                        }.onFailure { error ->
                            if (error.message?.contains("A network error") == true) {
                                messageBarState.addError("Please check your internet connection and try again.")
                            } else if (error.message?.contains("IdToken is null") == true) {
                                messageBarState.addError("Sign in was cancelled.")
                            } else {
                                messageBarState.addError("Unable to sign in. Please try again.")
                            }
                            isLoading = false
                        }
                    }
                ) {
                    Button(
                        onClick = {
                            isLoading = true
                            this@GoogleButtonUiContainerFirebase.onClick()
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF1F1F1F),
                            disabledContainerColor = Color.White.copy(alpha = 0.8f),
                            disabledContentColor = Color(0xFF1F1F1F).copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(32.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 12.dp,
                            pressedElevation = 16.dp,
                            disabledElevation = 6.dp
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color(0xFF4A90E2),
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Signing you in...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Icon(
                                    painter = painterResource(Res.drawable.google_icon),
                                    contentDescription = "Google",
                                    modifier = Modifier.size(28.dp),
                                    tint = Color.Unspecified
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Continue with Google",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Feature highlights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FeatureItem(
                        icon = FontAwesomeIcons.Solid.Users,
                        text = "Team\nManagement"
                    )
                    FeatureItem(
                        icon = FontAwesomeIcons.Solid.MapMarkerAlt,
                        text = "Real-time\nTracking"
                    )
                    FeatureItem(
                        icon = FontAwesomeIcons.Solid.Clock,
                        text = "Smart\nScheduling"
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Footer text
                Text(
                    text = "By continuing, you agree to our Terms of Service and Privacy Policy",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }

            // Subtle corner decorations
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-40).dp)
                    .size(120.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.04f),
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-60).dp, y = 60.dp)
                    .size(160.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.03f),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    text: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.White.copy(alpha = 0.9f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
            fontSize = 11.sp
        )
    }
}*/
