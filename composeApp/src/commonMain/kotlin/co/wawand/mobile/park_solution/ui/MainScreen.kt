package co.wawand.mobile.park_solution.ui

import ContentWithMessageBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.wawand.mobile.park_solution.ui.companySettings.CompanySettingsContent
import co.wawand.mobile.park_solution.ui.parking.ParkingContent
import co.wawand.mobile.park_solution.ui.profileContent.ProfileContent
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Bars
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.CarAlt
import compose.icons.fontawesomeicons.solid.Tools
import compose.icons.fontawesomeicons.solid.User
import compose.icons.fontawesomeicons.solid.UserAlt
import compose.icons.fontawesomeicons.solid.Walking
import kotlinx.coroutines.launch
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

@Composable
fun MainScreen(
    //content: @Composable (PaddingValues) -> Unit
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

    val items = listOf(
        NavigationItems(
            title = "Parking",
            selectedIcon = FontAwesomeIcons.Solid.Car,
            unselectedIcon = FontAwesomeIcons.Solid.CarAlt
        ),
        NavigationItems(
            title = "Settings",
            selectedIcon = FontAwesomeIcons.Solid.Tools,
            unselectedIcon = FontAwesomeIcons.Solid.Tools
        ),
        NavigationItems(
            title = "Profile",
            selectedIcon = FontAwesomeIcons.Solid.User,
            unselectedIcon = FontAwesomeIcons.Solid.UserAlt
        ),
    )

    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var currentUserSpace by remember { mutableStateOf(4) }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(0.8F)) {
                    Column(
                        modifier = Modifier.padding(horizontal = 0.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Menu",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )

                        HorizontalDivider()

                        items.forEachIndexed { index, item ->
                            NavigationDrawerItem(
                                label = { Text(text = item.title) },
                                selected = index == selectedItemIndex,
                                onClick = {
                                    selectedItemIndex = index
                                    scope.launch { drawerState.close() }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (index == selectedItemIndex) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }

                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        NavigationDrawerItem(
                            label = { Text("Sign Out") },
                            selected = false,
                            icon = {
                                Icon(
                                    FontAwesomeIcons.Solid.Walking,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            onClick = {
                                viewModel.signOut(
                                    onSuccess = { navigateToSignIn() },
                                    onError = { message -> messageBarState.addError(message) }
                                )
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold { innerPadding ->
            //content(innerPadding)
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
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.padding(end = 8.dp)) {
                            IconButton(onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) {
                                        drawerState.open()
                                    } else {
                                        drawerState.close()
                                    }
                                }
                            }) {
                                Icon(
                                    FontAwesomeIcons.Solid.Bars,
                                    contentDescription = "Menu",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
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
                    }

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
                        when (selectedItemIndex) {
                            0 -> {
                                ParkingContent(
                                    parkingSpaces = parkingSpaces,
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
}

data class NavigationItems(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null
)

