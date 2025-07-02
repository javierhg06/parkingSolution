package co.wawand.mobile.park_solution.ui.parking.redesign

import MessageBarState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import co.wawand.mobile.park_solution.ui.parking.ParkingViewModel
import co.wawand.mobile.park_solution.ui.utils.formatToHourMinute
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.regular.Circle
import compose.icons.fontawesomeicons.solid.ArrowRight
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.CheckCircle
import compose.icons.fontawesomeicons.solid.CircleNotch
import compose.icons.fontawesomeicons.solid.Comment
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParkingContentRedesigned(messageBarState: MessageBarState) {
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

    ImprovedParkingGrid(
        spaces = uiState.parkingSpaces,
        currentUser = uiState.currentUser,
        isLoading = uiState.isLoading,
        messageBarState = messageBarState,
        currentUserSpace = uiState.parkingSpaces.indexOfFirst { it.user == uiState.currentUser } + 1,
        selectedSpaceInfo = uiState.parkingSpaces.find { it.user == uiState.currentUser },
        onSpaceClicked = viewModel::toggleCompanyParkingSpace
    )
}

@Composable
fun ImprovedCurrentParkingStatus(
    currentSpace: Int,
    selectedSpaceInfo: ParkingSpaceWithUser?,
    onLeaveClicked: () -> Unit
) {
    if (currentSpace != 0) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4CAF50)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Car,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Space $currentSpace",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Parked since ${selectedSpaceInfo?.parkingSpace?.occupiedAt.formatToHourMinute()}",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Button(
                    onClick = onLeaveClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.ArrowRight, // ArrowRightFromBracket,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Leave",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ImprovedParkingGrid(
    spaces: List<ParkingSpaceWithUser>,
    currentUser: User?,
    currentUserSpace: Int,
    selectedSpaceInfo: ParkingSpaceWithUser?,
    isLoading: Boolean,
    messageBarState: MessageBarState,
    onSpaceClicked: (parkingSpace: ParkingSpaceWithUser, onError: (String) -> Unit) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Current parking status
        item(span = { GridItemSpan(maxLineSpan) }) {
            ImprovedCurrentParkingStatus(
                currentSpace = currentUserSpace,
                selectedSpaceInfo = selectedSpaceInfo,
                onLeaveClicked = {
                    onSpaceClicked(
                        spaces[currentUserSpace - 1],
                    ) { message ->
                        messageBarState.addError(message)
                    }
                }
            )
        }

        // Header with stats
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
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
                            tint = Color(0xFF4CAF50)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Stats row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val availableSpaces = spaces.count { !it.parkingSpace.occupied }
                    val occupiedSpaces = spaces.count { it.parkingSpace.occupied }

                    ParkingStatChip(
                        icon = FontAwesomeIcons.Solid.CheckCircle,
                        text = "$availableSpaces Available",
                        color = Color(0xFF4CAF50)
                    )

                    ParkingStatChip(
                        icon = FontAwesomeIcons.Solid.Car,
                        text = "$occupiedSpaces Occupied",
                        color = Color(0xFF757575)
                    )
                }
            }
        }

        // Parking spaces
        itemsIndexed(spaces) { index, space ->
            ImprovedParkingSpaceCard(
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
fun ParkingStatChip(
    icon: ImageVector,
    text: String,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
fun ImprovedParkingSpaceCard(
    space: ParkingSpaceWithUser,
    spaceNumber: Int,
    currentUser: User?,
    onClick: () -> Unit
) {
    val isCurrentUser = space.user == currentUser
    val isOccupied = space.parkingSpace.occupied
    val isAvailable = !isOccupied

    val backgroundColor = when {
        isCurrentUser -> Color(0xFF4CAF50)
        isOccupied -> Color(0xFFFFEBEE)
        else -> Color.White
    }

    val borderColor = when {
        isCurrentUser -> Color(0xFF4CAF50)
        isOccupied -> Color(0xFFFFCDD2)
        else -> Color(0xFFE0E0E0)
    }

    val textColor = if (isCurrentUser) Color.White else Color.Black

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(enabled = isAvailable || isCurrentUser) { onClick() }
            .border(
                width = if (isCurrentUser) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentUser) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Status indicator
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = when {
                                isCurrentUser -> Color.White.copy(alpha = 0.2f)
                                isOccupied -> Color(0xFFFFCDD2)
                                else -> Color(0xFFE8F5E8)
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            isCurrentUser -> FontAwesomeIcons.Solid.CheckCircle
                            isOccupied -> FontAwesomeIcons.Solid.Car
                            else -> FontAwesomeIcons.Regular.Circle
                        },
                        contentDescription = null,
                        tint = when {
                            isCurrentUser -> Color.White
                            isOccupied -> Color(0xFFE57373)
                            else -> Color(0xFF4CAF50)
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Space $spaceNumber",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Text(
                    text = when {
                        isCurrentUser -> "Your Space"
                        isOccupied -> space.user?.name ?: "Occupied"
                        else -> "Available"
                    },
                    fontSize = 13.sp,
                    color = textColor.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                if (isOccupied) {
                    Text(
                        text = "Since ${space.parkingSpace.occupiedAt}",
                        fontSize = 11.sp,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            }

            // Message indicator for occupied spaces
            if (isOccupied && !isCurrentUser) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(20.dp)
                        .background(
                            Color(0xFF2196F3),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Comment,
                        contentDescription = "Message",
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }

            // Availability pulse animation
            if (isAvailable) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.0f,
                    targetValue = 0.3f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "alpha"
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color(0xFF4CAF50).copy(alpha = alpha),
                            RoundedCornerShape(16.dp)
                        )
                )
            }
        }
    }
}