package co.wawand.mobile.park_solution.ui.companySetUpSettings.stateContent

import AppColors
import AppSpacing
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign


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
                .padding(horizontal = AppSpacing.xl), // Márgenes de pantalla
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Error Creating Company",
                style = MaterialTheme.typography.headlineMedium, // Título de pantalla
                color = AppColors.Error, // Color de error del sistema
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(AppSpacing.md)) // Espaciado base

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge, // Texto principal
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Texto secundario
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(AppSpacing.xl)) // Espaciado grande

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary // Color primario del tema
                ),
                shape = MaterialTheme.shapes.medium // Forma estándar para botones
            ) {
                Text(
                    text = "Try Again",
                    style = MaterialTheme.typography.labelLarge // Texto para botones
                )
            }
        }
    }
}







/*


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
}*/
