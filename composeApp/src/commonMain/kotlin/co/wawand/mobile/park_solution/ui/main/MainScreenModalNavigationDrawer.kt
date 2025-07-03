package co.wawand.mobile.park_solution.ui.main

import ContentWithMessageBar
import MessageBarState
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.wawand.mobile.park_solution.ui.companySettings.CompanySettingsContent
import co.wawand.mobile.park_solution.ui.parking.redesign.ParkingContentRedesigned
import co.wawand.mobile.park_solution.ui.profile.UserProfileContent
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Bars
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.Cog
import compose.icons.fontawesomeicons.solid.SignOutAlt
import compose.icons.fontawesomeicons.solid.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@Composable
fun MainScreenModalNavigationDrawer(navigateToSignIn: () -> Unit) {
    val viewModel = koinViewModel<MainViewModel>()
    val messageBarState = rememberMessageBarState()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

    ModalNavigationDrawer(
        drawerContent = {
            MainDrawer(
                selectedItemIndex = selectedItemIndex,
                onItemSelected = { selectedItemIndex = it; scope.launch { drawerState.close() } },
                onSignOut = {
                    viewModel.signOut(
                        onSuccess = navigateToSignIn,
                        onError = { message -> messageBarState.addError(message) }
                    )
                }
            )
        },
        drawerState = drawerState
    ) {
        Scaffold {
            ContentWithMessageBar(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                messageBarState = messageBarState,
                errorMaxLines = 2,
                errorContainerColor = Color(0xFFDC2626),
                errorContentColor = Color.White,
                contentBackgroundColor = Color.Transparent
            ) {
                MainContent(
                    drawerState = drawerState,
                    selectedItemIndex = selectedItemIndex,
                    scope = scope,
                    messageBarState = messageBarState
                )
            }
        }
    }
}

@Composable
private fun MainDrawer(
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit,
    onSignOut: () -> Unit
) {
    val items = listOf(
        NavigationItems(
            title = "Parking",
            selectedIcon = FontAwesomeIcons.Solid.Car,
            unselectedIcon = FontAwesomeIcons.Solid.Car//FontAwesomeIcons.Regular.Car
        ),
        NavigationItems(
            title = "Settings",
            selectedIcon = FontAwesomeIcons.Solid.Cog,
            unselectedIcon = FontAwesomeIcons.Solid.Cog//FontAwesomeIcons.Regular.Cog
        ),
        NavigationItems(
            title = "Profile",
            selectedIcon = FontAwesomeIcons.Solid.User,
            unselectedIcon = FontAwesomeIcons.Solid.User//FontAwesomeIcons.Regular.User
        )
    )

    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(0.85f),
        drawerContainerColor = Color.White,
        drawerContentColor = Color.Black
    ) {
        Box(modifier = Modifier.fillMaxHeight()) {
            Column {
                // Enhanced Header Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF4A6FE7),
                                    Color(0xFF3B5CE8)
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        // App Logo/Icon
                        Card(
                            modifier = Modifier.size(60.dp),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Solid.Car,
                                    contentDescription = "App Logo",
                                    modifier = Modifier.size(28.dp),
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Park Solution",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Smart Parking Management",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Navigation Items
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = item.title,
                                    fontWeight = if (index == selectedItemIndex) FontWeight.SemiBold else FontWeight.Medium,
                                    fontSize = 16.sp
                                )
                            },
                            selected = index == selectedItemIndex,
                            onClick = { onItemSelected(index) },
                            icon = {
                                Icon(
                                    imageVector = if (index == selectedItemIndex) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = Color(0xFF4A6FE7).copy(alpha = 0.1f),
                                selectedIconColor = Color(0xFF4A6FE7),
                                selectedTextColor = Color(0xFF4A6FE7),
                                unselectedIconColor = Color(0xFF6B7280),
                                unselectedTextColor = Color(0xFF374151)
                            ),
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 4.dp
                            )
                        )
                    }
                }
            }

            // Sign Out Section
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = Color(0xFFE5E7EB)
                )

                NavigationDrawerItem(
                    label = {
                        Text(
                            text = "Sign Out",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color(0xFFDC2626)
                        )
                    },
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.SignOutAlt,
                            contentDescription = "Sign Out",
                            modifier = Modifier.size(22.dp),
                            tint = Color(0xFFDC2626)
                        )
                    },
                    onClick = onSignOut,
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(horizontal = 0.dp)
                )
            }
        }
    }
}

@Composable
private fun MainContent(
    drawerState: DrawerState,
    selectedItemIndex: Int,
    scope: CoroutineScope,
    messageBarState: MessageBarState,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4A6FE7),
                        Color(0xFF3B5CE8)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(8.dp))
            TopBar(
                onMenuClick = {
                    scope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    }
                },
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Content Area with rounded corners
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F9FA)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    when (selectedItemIndex) {
                        0 -> ParkingContentRedesigned(messageBarState = messageBarState)
                        1 -> CompanySettingsContent(messageBarState = messageBarState)
                        2 -> UserProfileContent(messageBarState = messageBarState)
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    onMenuClick: () -> Unit,
    isConnected: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier
                .size(44.dp)
                .background(
                    Color.White.copy(alpha = 0.15f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Bars,
                contentDescription = "Menu",
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
        }
    }
}
