package co.wawand.mobile.park_solution.ui.auth

import ContentWithMessageBar
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.UserAlt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import parksolution.composeapp.generated.resources.Res
import parksolution.composeapp.generated.resources.google_icon
import rememberMessageBarState

@Composable
fun SignInScreen(
    navigateToHome: () -> Unit = {},
    navigateToCreateCompany: () -> Unit = {},
    navigateToJoinCompany: () -> Unit = {},
) {
    val viewModel = koinViewModel<SignInViewModel>()
    val messageBarState = rememberMessageBarState()
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }

    // Animated gradient background
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient"
    )

    // Floating elements animation
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    ContentWithMessageBar(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        errorMaxLines = 2,
        messageBarState = messageBarState,
        errorContainerColor = Color(0xFFB71C1C),
        errorContentColor = Color.White,
        successContainerColor = Color(0xFF2E7D32),
        successContentColor = Color.White,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2),
                            Color(0xFFf093fb),
                            Color(0xFFf5576c)
                        ),
                        start = Offset(gradientOffset, gradientOffset),
                        end = Offset(gradientOffset + 500f, gradientOffset + 500f)
                    )
                )
        ) {
            // Background decoration circles
            repeat(6) { index ->
                val size = (60 + index * 20).dp
                val xOffset = (index * 120 + 50).dp
                val yOffset = (index * 80 + 100).dp

                Box(
                    modifier = Modifier
                        .offset(
                            x = xOffset,
                            y = yOffset + floatingOffset.dp * (if (index % 2 == 0) 1 else -1)
                        )
                        .size(size)
                        .background(
                            color = Color.White.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                )
            }

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // App logo/icon placeholder with animation
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .offset(y = floatingOffset.dp * 0.5f),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Car,
                            contentDescription = "App Logo",
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF667eea)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Welcome text
                Text(
                    text = "Welcome!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sign in to continue your journey",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(64.dp))

                // Google Sign In Button
                GoogleButtonUiContainerFirebase(
                    linkAccount = false,
                    onResult = { result ->
                        result.onSuccess { user ->
                            viewModel.signInAndRedirectUser(
                                user = user,
                                onSuccess = { redirectAfterSignIn ->
                                    scope.launch {
                                        messageBarState.addSuccess("Successfully signed in.")
                                        delay(1000)
                                        when (redirectAfterSignIn) {
                                            RedirectAfterSignIn.Home -> navigateToHome()
                                            RedirectAfterSignIn.JoinCompany -> navigateToJoinCompany()
                                            RedirectAfterSignIn.CreateCompany -> navigateToCreateCompany()
                                        }
                                        isLoading = false
                                    }
                                },
                                onError = { error -> messageBarState.addError(error) }
                            )
                        }.onFailure { error ->
                            if (error.message?.contains("A network error") == true) {
                                messageBarState.addError("Internet connection error.")
                            } else if (error.message?.contains("IdToken is null") == true) {
                                messageBarState.addError("Sign in cancelled.")
                            } else {
                                println("------->> SignIn Error: ${error.message}")
                                messageBarState.addError("Something went wrong")
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
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.White.copy(alpha = 0.7f),
                            disabledContentColor = Color.Black.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(28.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 12.dp,
                            disabledElevation = 4.dp
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color(0xFF4285F4),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Signing in...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                // Google logo placeholder (you can replace with actual Google logo)
                                Icon(
                                    painter = painterResource(Res.drawable.google_icon),
                                    contentDescription = "Google",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Unspecified
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Sign in with Google",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Additional decorative text
                Text(
                    text = "By signing in, you agree to our Terms of Service",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            // Bottom decorative elements
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 50.dp, y = 50.dp)
                    .size(100.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-30).dp, y = 30.dp)
                    .size(80.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.05f),
                        shape = CircleShape
                    )
            )
        }
    }
}