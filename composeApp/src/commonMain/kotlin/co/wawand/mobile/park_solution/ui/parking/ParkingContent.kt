package co.wawand.mobile.park_solution.ui.parking

import MessageBarState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.wawand.mobile.park_solution.shared.domain.model.ParkingSpaceWithUser
import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.shared.util.displayResult
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Bell
import compose.icons.fontawesomeicons.solid.CircleNotch
import compose.icons.fontawesomeicons.solid.Clock
import compose.icons.fontawesomeicons.solid.Directions
import compose.icons.fontawesomeicons.solid.Parking
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParkingContent(messageBarState: MessageBarState) {
    val viewModel = koinViewModel<ParkingViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val parkingState = viewModel.parkingSpacesState

    parkingState.displayResult(
        onLoading = { viewModel.setLoadingState(true) },
        onSuccess = { state ->
            viewModel.setLoadingState(false)
            viewModel.setParkingSpaces(state)
        },
        onError = { message ->
            messageBarState.addError(message)
            viewModel.setLoadingState(false)
        },
    )

    ParkingGrid(
        spaces = uiState.parkingSpaces,
        currentUser = uiState.currentUser,
        isLoading = uiState.isLoading,
        messageBarState = messageBarState,
        currentUserSpace = uiState.parkingSpaces.indexOfFirst { it.user == uiState.currentUser } + 1,
        onSpaceClicked = viewModel::toggleCompanyParkingSpace
    )
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
    spaces: List<ParkingSpaceWithUser>,
    currentUser: User?,
    currentUserSpace: Int,
    isLoading: Boolean,
    messageBarState: MessageBarState,
    onSpaceClicked: (parkingSpace: ParkingSpaceWithUser, onError: (String) -> Unit) -> Unit,
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
                onLeaveClicked = {
                    onSpaceClicked(
                        spaces[currentUserSpace - 1],
                    ) { message ->
                        messageBarState.addError(message)
                    }
                }
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Parking Spaces",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                if (isLoading) {
                    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "spin"
                    )

                    Icon(
                        imageVector = FontAwesomeIcons.Solid.CircleNotch,
                        contentDescription = "Loading",
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(rotation),
                        tint = Color.Red
                    )
                }
            }
        }

        itemsIndexed(spaces) { index, space ->
            ParkingSpaceCard(
                space = space,
                currentUser = currentUser,
                spaceNumber = index + 1,
                onClick = {
                    onSpaceClicked(
                        space
                    ) { message ->
                        messageBarState.addError(message)
                    }
                }
            )
        }
    }
}

@Composable
fun ParkingSpaceCard(
    space: ParkingSpaceWithUser,
    spaceNumber: Int,
    currentUser: User?,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        space.user == currentUser -> Color(0xFFE8F5E8)
        space.parkingSpace.occupied -> Color(0xFFFFEBEE)
        else -> Color(0xFFF5F5F5)
    }

    val borderColor = when {
        space.user == currentUser -> Color(0xFF4CAF50)
        space.parkingSpace.occupied -> Color(0xFFE57373)
        else -> Color(0xFFE0E0E0)
    }

    val iconColor = when {
        space.user == currentUser -> Color(0xFF4CAF50)
        space.parkingSpace.occupied -> Color(0xFFE57373)
        else -> Color(0xFF9E9E9E)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(enabled = !space.parkingSpace.occupied || space.user == currentUser) { onClick() }
            .border(
                width = if (space.user == currentUser) 2.dp else 1.dp,
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
                text = "Space $spaceNumber",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Text(
                text = when {
                    space.user == currentUser -> "You"
                    space.parkingSpace.occupied -> space.user?.name ?: ""
                    else -> "Available"
                },
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            if (space.parkingSpace.occupied) {
                Text(
                    text = "Since ${space.parkingSpace.occupiedAt}",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            if (space.parkingSpace.occupied && space.user != currentUser) {
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
private fun EmptyParkingState(
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
                text = "No Parking Spaces",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Description
            Text(
                text = "Your company hasn't set up parking spaces yet. Spaces will appear here once they're added by the administrator.",
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
                    title = "In Setup",
                    description = "Spaces are being configured",
                )

                StatusCard(
                    icon = FontAwesomeIcons.Solid.Bell,//Icons.Default.Notifications,
                    title = "We'll Notify You",
                    description = "You'll get notified when they're ready",
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