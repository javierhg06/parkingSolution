package co.wawand.mobile.park_solution.ui

import ContentWithMessageBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.ui.companySettings.CompanySettingsContent
import co.wawand.mobile.park_solution.ui.parking.ParkingContent
import co.wawand.mobile.park_solution.ui.parking.ParkingHeader
import co.wawand.mobile.park_solution.ui.profileContent.ProfileContent
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Directions
import compose.icons.fontawesomeicons.solid.Tools
import compose.icons.fontawesomeicons.solid.UserAlt
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

// Data classes
data class ParkingSpace(
    val id: Int,
    val isOccupied: Boolean,
    val occupantName: String? = null,
    val occupiedSince: LocalTime? = null,
    val isCurrentUser: Boolean = false
)

// Main screen composable
@Composable
fun MainScreen(
    navigateToSignIn: () -> Unit
) {
    val viewModel = koinViewModel<MainViewModel>()
    val messageBarState = rememberMessageBarState()
    var parkingSpaces by remember {
        mutableStateOf(
            listOf(
                ParkingSpace(1, false),
                ParkingSpace(2, true, "Sarah M.", LocalTime(8, 45)),
                ParkingSpace(3, false),
                ParkingSpace(4, true, "You", LocalTime(9, 15), true),
                ParkingSpace(5, true, "Mike R.", LocalTime(8, 45)),
                ParkingSpace(6, false)
            )
        )
    }

    var currentUserSpace by remember { mutableStateOf(4) }
    var showSpaceSelector by remember { mutableStateOf(false) }

    var tabIndex by remember { mutableStateOf(0) }
    val onTabSelected: (Int) -> Unit = { index -> tabIndex = index }

    var freeSpaces by remember { mutableStateOf(3) }
    var totalSpaces by remember { mutableStateOf(6) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        ContentWithMessageBar(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            messageBarState = messageBarState,
            errorMaxLines = 2,
            errorContainerColor = Color(0xFFB71C1C),
            errorContentColor = Color.White,
            contentBackgroundColor = Color.Blue
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                ParkingHeader(
                    modifier = Modifier,
                    freeSpaces = freeSpaces,
                    totalSpaces = totalSpaces
                )

                // Main content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.White,
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .padding(16.dp)
                ) {
                    // Navigation tabs
                    NavigationTabs(
                        selectedTabIndex = tabIndex,
                        onTabSelected = onTabSelected
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    when (tabIndex) {
                        0 -> {
                            ParkingContent(
                                parkingSpaces = emptyList(),//parkingSpaces,
                                currentUserSpace = currentUserSpace,
                                onSpaceClicked = { spaceId ->
                                    val space = parkingSpaces.find { it.id == spaceId }
                                    if (space != null && !space.isOccupied && currentUserSpace != 0) {
                                        // Move user to new space
                                        parkingSpaces = parkingSpaces.map { s ->
                                            when (s.id) {
                                                currentUserSpace -> s.copy(
                                                    isOccupied = false,
                                                    occupantName = null,
                                                    occupiedSince = null,
                                                    isCurrentUser = false
                                                )

                                                spaceId -> s.copy(
                                                    isOccupied = true,
                                                    occupantName = "You",
                                                    occupiedSince = Clock.System.now()
                                                        .toLocalDateTime(TimeZone.currentSystemDefault()).time,
                                                    isCurrentUser = true
                                                )

                                                else -> s
                                            }
                                        }
                                        currentUserSpace = spaceId
                                    } else if (space != null && !space.isOccupied && currentUserSpace == 0) {
                                        // User not parked, park in selected space
                                        parkingSpaces = parkingSpaces.map { s ->
                                            if (s.id == spaceId) {
                                                s.copy(
                                                    isOccupied = true,
                                                    occupantName = "You",
                                                    occupiedSince = Clock.System.now()
                                                        .toLocalDateTime(TimeZone.currentSystemDefault()).time,
                                                    isCurrentUser = true
                                                )
                                            } else s
                                        }
                                        currentUserSpace = spaceId
                                    }
                                },
                                onLeaveClicked = {
                                    parkingSpaces = parkingSpaces.map { space ->
                                        if (space.id == currentUserSpace) {
                                            space.copy(
                                                isOccupied = false,
                                                occupantName = null,
                                                occupiedSince = null,
                                                isCurrentUser = false
                                            )
                                        } else space
                                    }
                                    currentUserSpace = 0
                                }
                            )
                        }

                        1 -> {
                            CompanySettingsContent()
                        }

                        2 -> {
                            ProfileContent(
                                onUpdateUser = {},
                                onLogout = {
                                    viewModel.signOut(
                                        onSuccess = { navigateToSignIn() },
                                        onError = { message -> messageBarState.addError(message) }
                                    )
                                },
                                currentLanguage = "en",
                                onLanguageChange = {}
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationTabs(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    SecondaryTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Tab(
            selected = selectedTabIndex == 0,
            onClick = { onTabSelected(0) },
            text = { Text(text = "Parking") },
            icon = {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Directions,
                    contentDescription = "Parking",
                    modifier = Modifier.size(24.dp)
                )
            },
            selectedContentColor = Color(0xFF4A6FE7),
            unselectedContentColor = Color.Gray
        )
        Tab(
            selected = selectedTabIndex == 1,
            onClick = { onTabSelected(1) },
            text = { Text(text = "Settings") },
            icon = {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Tools,
                    contentDescription = "Parking",
                    modifier = Modifier.size(24.dp)
                )
            },
            selectedContentColor = Color(0xFF4A6FE7),
            unselectedContentColor = Color.Gray
        )
        Tab(
            selected = selectedTabIndex == 2,
            onClick = { onTabSelected(2) },
            text = { Text(text = "Profile") },
            icon = {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.UserAlt,
                    contentDescription = "Parking",
                    modifier = Modifier.size(24.dp)
                )
            },
            selectedContentColor = Color(0xFF4A6FE7),
            unselectedContentColor = Color.Gray
        )
    }
}

