package co.wawand.mobile.park_solution.ui.parking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.wawand.mobile.park_solution.shared.DefaultTimeFormat
import co.wawand.mobile.park_solution.ui.ParkingSpace
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowAltCircleRight
import compose.icons.fontawesomeicons.solid.Bell
import compose.icons.fontawesomeicons.solid.BusinessTime
import compose.icons.fontawesomeicons.solid.Clock
import compose.icons.fontawesomeicons.solid.Directions
import compose.icons.fontawesomeicons.solid.Info
import compose.icons.fontawesomeicons.solid.Parking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

@Composable
fun ParkingContent1(
    parkingSpaces: List<ParkingSpace>,
    currentUserSpace: Int,
    onSpaceClicked: (Int) -> Unit,
    onLeaveClicked: (Int) -> Unit
) {
    ParkingGrid(
        spaces = parkingSpaces,
        currentUserSpace = currentUserSpace,
        onLeaveClicked = onLeaveClicked,
        onSpaceClicked = onSpaceClicked
    )
}

@Composable
fun ParkingHeader(
    modifier: Modifier = Modifier,
    freeSpaces: Int,
    totalSpaces: Int,
) {
    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Park Solution",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Connected",
                color = Color.White,
                fontSize = 14.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault()).time.format(DefaultTimeFormat),
                color = Color.White,
                fontSize = 16.sp
            )

            Text(
                text = if (freeSpaces == 0) "No spaces available" else "$freeSpaces of $totalSpaces spaces available",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun CurrentParkingStatus(
    currentSpace: Int,
    onLeaveClicked: () -> Unit
) {
    if (currentSpace != 0) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE8F5E8)
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),

                ) {
                Text(
                    text = "You're parked in Space $currentSpace",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E7D32)
                )
                Text(
                    text = "Since 9:15 AM",
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50)
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        onClick = onLeaveClicked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "↗ I'm Leaving",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParkingGrid(
    spaces: List<ParkingSpace>,
    currentUserSpace: Int,
    onSpaceClicked: (Int) -> Unit,
    onLeaveClicked: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            // Current parking status
            CurrentParkingStatus(
                currentSpace = currentUserSpace,
                onLeaveClicked = { onLeaveClicked(currentUserSpace) }
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Parking Spaces",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        items(spaces) { space ->
            ParkingSpaceCard(
                space = space,
                onClick = { onSpaceClicked(space.id) }
            )
        }
    }
}

@Composable
fun ParkingSpaceCard(
    space: ParkingSpace,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        space.isCurrentUser -> Color(0xFFE8F5E8)
        space.isOccupied -> Color(0xFFFFEBEE)
        else -> Color(0xFFF5F5F5)
    }

    val borderColor = when {
        space.isCurrentUser -> Color(0xFF4CAF50)
        space.isOccupied -> Color(0xFFE57373)
        else -> Color(0xFFE0E0E0)
    }

    val iconColor = when {
        space.isCurrentUser -> Color(0xFF4CAF50)
        space.isOccupied -> Color(0xFFE57373)
        else -> Color(0xFF9E9E9E)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(enabled = !space.isOccupied || space.isCurrentUser) { onClick() }
            .border(
                width = if (space.isCurrentUser) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Directions,//Icons.Default.DirectionsCar,
                contentDescription = "Car",
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Space ${space.id}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Text(
                text = when {
                    space.isCurrentUser -> "You"
                    space.isOccupied -> space.occupantName ?: ""
                    else -> "Available"
                },
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            if (space.isOccupied && space.occupiedSince != null) {
                Text(
                    text = "Since ${space.occupiedSince}",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            if (space.isOccupied && !space.isCurrentUser) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "💬",
                    fontSize = 12.sp
                )
            }
        }
    }
}

//////////////////////////////

@Composable
fun ParkingContent(
    parkingSpaces: List<ParkingSpace>,
    currentUserSpace: Int,
    onSpaceClicked: (Int) -> Unit,
    onLeaveClicked: (Int) -> Unit,
    isLoading: Boolean = false,
    hasCompany: Boolean = true,
    currentLanguage: String = "en" // "en" or "es"
) {
    when {
        isLoading -> {
            LoadingState(currentLanguage = currentLanguage)
        }
        !hasCompany -> {
            NoCompanyState(currentLanguage = currentLanguage)
        }
        parkingSpaces.isEmpty() -> {
            EmptyParkingState(currentLanguage = currentLanguage)
        }
        else -> {
            ParkingGrid(
                spaces = parkingSpaces,
                currentUserSpace = currentUserSpace,
                onLeaveClicked = onLeaveClicked,
                onSpaceClicked = onSpaceClicked
            )
        }
    }
}

@Composable
private fun LoadingState(
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )
            Text(
                text = if (currentLanguage == "es") "Cargando espacios..." else "Loading spaces...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NoCompanyState(
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Icon
            Icon(
                imageVector = FontAwesomeIcons.Solid.BusinessTime,//Icons.Default.Business,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )

            // Title
            Text(
                text = if (currentLanguage == "es") "Sin Empresa Asignada" else "No Company Assigned",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Description
            Text(
                text = if (currentLanguage == "es")
                    "No estás asignado a ninguna empresa. Contacta a tu administrador para obtener acceso a los espacios de estacionamiento."
                else
                    "You're not assigned to any company. Contact your administrator to get access to parking spaces.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )

            // Support Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Info,//Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = if (currentLanguage == "es")
                            "Necesitas ayuda? Contacta al soporte técnico."
                        else
                            "Need help? Contact technical support.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyParkingState(
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp).verticalScroll(rememberScrollState())
        ) {
            // Icon
            Icon(
                imageVector = FontAwesomeIcons.Solid.Parking,//Icons.Default.LocalParking,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )

            // Title
            Text(
                text = if (currentLanguage == "es") "Sin Espacios Disponibles" else "No Parking Spaces",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Description
            Text(
                text = if (currentLanguage == "es")
                    "Tu empresa aún no ha configurado espacios de estacionamiento. Los espacios aparecerán aquí una vez que sean añadidos por el administrador."
                else
                    "Your company hasn't set up parking spaces yet. Spaces will appear here once they're added by the administrator.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )

            // Status Cards
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusCard(
                    icon = FontAwesomeIcons.Solid.Clock,
                    title = if (currentLanguage == "es") "En Configuración" else "In Setup",
                    description = if (currentLanguage == "es")
                        "Los espacios se están configurando"
                    else
                        "Spaces are being configured",
                    currentLanguage = currentLanguage
                )

                StatusCard(
                    icon = FontAwesomeIcons.Solid.Bell,//Icons.Default.Notifications,
                    title = if (currentLanguage == "es") "Te Notificaremos" else "We'll Notify You",
                    description = if (currentLanguage == "es")
                        "Recibirás una notificación cuando estén listos"
                    else
                        "You'll get notified when they're ready",
                    currentLanguage = currentLanguage
                )
            }
        }
    }
}

@Composable
private fun StatusCard(
    icon: ImageVector,
    title: String,
    description: String,
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Alternative version with refresh functionality
@Composable
fun ParkingContentWithRefresh(
    parkingSpaces: List<ParkingSpace>,
    currentUserSpace: Int,
    onSpaceClicked: (Int) -> Unit,
    onLeaveClicked: (Int) -> Unit,
    isLoading: Boolean = false,
    hasCompany: Boolean = true,
    currentLanguage: String = "en",
    onRefresh: () -> Unit = {}
) {
    when {
        isLoading -> {
            LoadingState(currentLanguage = currentLanguage)
        }
        !hasCompany -> {
            NoCompanyStateWithRefresh(
                currentLanguage = currentLanguage,
                onRefresh = onRefresh
            )
        }
        parkingSpaces.isEmpty() -> {
            EmptyParkingStateWithRefresh(
                currentLanguage = currentLanguage,
                onRefresh = onRefresh
            )
        }
        else -> {
            ParkingGrid(
                spaces = parkingSpaces,
                currentUserSpace = currentUserSpace,
                onLeaveClicked = onLeaveClicked,
                onSpaceClicked = onSpaceClicked
            )
        }
    }
}

@Composable
private fun NoCompanyStateWithRefresh(
    currentLanguage: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Icon
            Icon(
                imageVector = FontAwesomeIcons.Solid.BusinessTime,//Icons.Default.Business,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )

            // Title
            Text(
                text = if (currentLanguage == "es") "Sin Empresa Asignada" else "No Company Assigned",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Description
            Text(
                text = if (currentLanguage == "es")
                    "No estás asignado a ninguna empresa. Contacta a tu administrador para obtener acceso a los espacios de estacionamiento."
                else
                    "You're not assigned to any company. Contact your administrator to get access to parking spaces.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )

            // Refresh Button
            OutlinedButton(
                onClick = onRefresh,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.ArrowAltCircleRight,//Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (currentLanguage == "es") "Actualizar" else "Refresh")
            }
        }
    }
}

@Composable
private fun EmptyParkingStateWithRefresh(
    currentLanguage: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Icon
            Icon(
                imageVector = FontAwesomeIcons.Solid.Parking,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )

            // Title
            Text(
                text = if (currentLanguage == "es") "Sin Espacios Disponibles" else "No Parking Spaces",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Description
            Text(
                text = if (currentLanguage == "es")
                    "Tu empresa aún no ha configurado espacios de estacionamiento. Toca actualizar para verificar si ya están disponibles."
                else
                    "Your company hasn't set up parking spaces yet. Tap refresh to check if they're now available.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )

            // Refresh Button
            Button(
                onClick = onRefresh,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.ArrowAltCircleRight,//Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (currentLanguage == "es") "Verificar Espacios" else "Check for Spaces")
            }
        }
    }
}