// Theme.kt
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==================== COLORES ====================
// Paleta minimalista con pocos colores
object AppColors {
    // Colores principales
    val Primary = Color(0xFF6366F1)      // Indigo moderno
    val PrimaryVariant = Color(0xFF4F46E5) // Indigo más oscuro

    // Colores de superficie - Tema Claro
    val BackgroundLight = Color(0xFFFAFAFA)
    val SurfaceLight = Color(0xFFFFFFFF)
    val OnBackgroundLight = Color(0xFF1A1A1A)
    val OnSurfaceLight = Color(0xFF2D2D2D)
    val OnSurfaceVariantLight = Color(0xFF666666)

    // Colores de superficie - Tema Oscuro
    val BackgroundDark = Color(0xFF0F0F0F)
    val SurfaceDark = Color(0xFF1A1A1A)
    val OnBackgroundDark = Color(0xFFF5F5F5)
    val OnSurfaceDark = Color(0xFFE5E5E5)
    val OnSurfaceVariantDark = Color(0xFFB0B0B0)

    // Colores de estado
    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFF59E0B)
    val Error = Color(0xFFEF4444)
}

// ==================== ESQUEMAS DE COLOR ====================
private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = Color.White,
    primaryContainer = AppColors.Primary.copy(alpha = 0.1f),
    onPrimaryContainer = AppColors.PrimaryVariant,

    background = AppColors.BackgroundLight,
    onBackground = AppColors.OnBackgroundLight,
    surface = AppColors.SurfaceLight,
    onSurface = AppColors.OnSurfaceLight,
    surfaceVariant = AppColors.BackgroundLight,
    onSurfaceVariant = AppColors.OnSurfaceVariantLight,

    outline = AppColors.OnSurfaceVariantLight.copy(alpha = 0.3f),
    outlineVariant = AppColors.OnSurfaceVariantLight.copy(alpha = 0.1f)
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.Primary,
    onPrimary = Color.White,
    primaryContainer = AppColors.Primary.copy(alpha = 0.2f),
    onPrimaryContainer = AppColors.Primary.copy(alpha = 0.8f),

    background = AppColors.BackgroundDark,
    onBackground = AppColors.OnBackgroundDark,
    surface = AppColors.SurfaceDark,
    onSurface = AppColors.OnSurfaceDark,
    surfaceVariant = AppColors.BackgroundDark,
    onSurfaceVariant = AppColors.OnSurfaceVariantDark,

    outline = AppColors.OnSurfaceVariantDark.copy(alpha = 0.3f),
    outlineVariant = AppColors.OnSurfaceVariantDark.copy(alpha = 0.1f)
)

// ==================== TIPOGRAFÍA ====================
object AppTypography {
    val headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    )

    val headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    )

    val titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    )

    val titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 26.sp
    )

    val bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

    val bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp
    )

    val bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp
    )

    val labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
}

// ==================== ESPACIADO ====================
object AppSpacing {
    // Sistema de espaciado basado en múltiplos de 4dp
    val xxxs = 2.dp   // Para separadores muy finos
    val xxs = 4.dp    // Espaciado mínimo
    val xs = 8.dp     // Entre elementos relacionados
    val sm = 12.dp    // Espaciado pequeño
    val md = 16.dp    // Espaciado estándar (base)
    val lg = 24.dp    // Entre secciones
    val xl = 32.dp    // Espaciado grande
    val xxl = 48.dp   // Para separar secciones importantes
    val xxxl = 64.dp  // Espaciado máximo
}

// ==================== ELEVACIONES ====================
object AppElevation {
    val none = 0.dp
    val small = 2.dp   // Cards simples
    val medium = 4.dp  // Cards importantes
    val large = 8.dp   // Dialogs, bottom sheets
}

// ==================== FORMAS ====================
object AppShapes {
    val small = RoundedCornerShape(8.dp)    // Botones pequeños, chips
    val medium = RoundedCornerShape(12.dp)  // Cards, inputs
    val large = RoundedCornerShape(16.dp)   // Dialogs, bottom sheets
}

// ==================== TEMA PRINCIPAL ====================
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            headlineLarge = AppTypography.headlineLarge,
            headlineMedium = AppTypography.headlineMedium,
            titleLarge = AppTypography.titleLarge,
            titleMedium = AppTypography.titleMedium,
            bodyLarge = AppTypography.bodyLarge,
            bodyMedium = AppTypography.bodyMedium,
            bodySmall = AppTypography.bodySmall,
            labelLarge = AppTypography.labelLarge
        ),
        shapes = Shapes(
            small = AppShapes.small,
            medium = AppShapes.medium,
            large = AppShapes.large
        ),
        content = content
    )
}

// ==================== GUÍAS DE USO ====================
/*
REGLAS DE ESPACIADO:
1. Usa AppSpacing.md (16dp) como espaciado base
2. AppSpacing.xs (8dp) entre elementos muy relacionados
3. AppSpacing.lg (24dp) entre secciones diferentes
4. AppSpacing.xl (32dp) para márgenes de pantalla
5. Usa Modifier.padding() de forma consistente

REGLAS DE COLORES:
1. Usa MaterialTheme.colorScheme.primary para acciones principales
2. Usa MaterialTheme.colorScheme.onSurface para texto principal
3. Usa MaterialTheme.colorScheme.onSurfaceVariant para texto secundario
4. Usa MaterialTheme.colorScheme.surface para fondos de cards
5. Usa MaterialTheme.colorScheme.outline para bordes sutiles

REGLAS DE TIPOGRAFÍA:
1. MaterialTheme.typography.headlineMedium para títulos de pantalla
2. MaterialTheme.typography.titleLarge para títulos de sección
3. MaterialTheme.typography.bodyLarge para texto principal
4. MaterialTheme.typography.bodyMedium para texto secundario
5. MaterialTheme.typography.labelLarge para labels y botones

REGLAS DE COMPONENTES:
1. Usa Card con elevation = AppElevation.small para elementos simples
2. Usa Card con elevation = AppElevation.medium para elementos importantes
3. Usa shape = MaterialTheme.shapes.medium para la mayoría de cards
4. Mantén consistencia en el uso de fillMaxWidth vs wrapContentSize
*/