package co.wawand.mobile.park_solution.ui.main

import AppColors
import AppElevation
import AppSpacing
import ContentWithMessageBar
import MessageBarState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.wawand.mobile.park_solution.ui.companySettings.CompanySettingsContent
import co.wawand.mobile.park_solution.ui.parking.redesign.ParkingContentRedesigned
import co.wawand.mobile.park_solution.ui.profile.UserProfileContent
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.Cog
import compose.icons.fontawesomeicons.solid.User
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@Composable
fun MainScreenBottomNav(navigateToSignIn: () -> Unit) {
    val viewModel = koinViewModel<MainViewModel>()
    val messageBarState = rememberMessageBarState()

    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            MainBottomNavigation(
                selectedItemIndex = selectedItemIndex,
                onItemSelected = { selectedItemIndex = it }
            )
        }
    ) { paddingValues ->
        ContentWithMessageBar(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            messageBarState = messageBarState,
            errorMaxLines = 2,
            errorContainerColor = AppColors.Error,
            errorContentColor = Color.White,
            contentBackgroundColor = Color.Transparent
        ) {
            MainContent(
                selectedItemIndex = selectedItemIndex,
                messageBarState = messageBarState,
            )
        }
    }
}

@Composable
private fun MainBottomNavigation(
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        NavigationItems(
            title = "Parking",
            selectedIcon = FontAwesomeIcons.Solid.Car,
            unselectedIcon = FontAwesomeIcons.Solid.Car
        ),
        NavigationItems(
            title = "Settings",
            selectedIcon = FontAwesomeIcons.Solid.Cog,
            unselectedIcon = FontAwesomeIcons.Solid.Cog
        ),
        NavigationItems(
            title = "Profile",
            selectedIcon = FontAwesomeIcons.Solid.User,
            unselectedIcon = FontAwesomeIcons.Solid.User
        )
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        tonalElevation = AppElevation.medium
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (index == selectedItemIndex) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (index == selectedItemIndex) FontWeight.SemiBold else FontWeight.Medium
                        )
                    )
                },
                selected = index == selectedItemIndex,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
private fun MainContent(
    selectedItemIndex: Int,
    messageBarState: MessageBarState,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = AppSpacing.lg, horizontal = AppSpacing.sm) // Margen consistente de pantalla
    ) {
        when (selectedItemIndex) {
            0 -> ParkingContentRedesigned(messageBarState = messageBarState)
            1 -> CompanySettingsContent(messageBarState = messageBarState)
            2 -> UserProfileContent(messageBarState = messageBarState)
        }
    }
}

/*
@Composable
fun MainScreenBottomNav(navigateToSignIn: () -> Unit) {
    val viewModel = koinViewModel<MainViewModel>()
    val messageBarState = rememberMessageBarState()

    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
           MainBottomNavigation(
                selectedItemIndex = selectedItemIndex,
                onItemSelected = { selectedItemIndex = it }
            )
        }
    ) { paddingValues ->
        ContentWithMessageBar(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            messageBarState = messageBarState,
            errorMaxLines = 2,
            errorContainerColor = Color(0xFFDC2626),
            errorContentColor = Color.White,
            contentBackgroundColor = Color.Transparent
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            MainContent(
                selectedItemIndex = selectedItemIndex,
                messageBarState = messageBarState,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MainBottomNavigation(
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        NavigationItems(
            title = "Parking",
            selectedIcon = FontAwesomeIcons.Solid.Car,
            unselectedIcon = FontAwesomeIcons.Solid.Car
        ),
        NavigationItems(
            title = "Settings",
            selectedIcon = FontAwesomeIcons.Solid.Cog,
            unselectedIcon = FontAwesomeIcons.Solid.Cog
        ),
        NavigationItems(
            title = "Profile",
            selectedIcon = FontAwesomeIcons.Solid.User,
            unselectedIcon = FontAwesomeIcons.Solid.User
        )
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF6B7280),
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (index == selectedItemIndex) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = if (index == selectedItemIndex) FontWeight.SemiBold else FontWeight.Medium
                    )
                },
                selected = index == selectedItemIndex,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF4A6FE7),
                    selectedTextColor = Color(0xFF4A6FE7),
                    unselectedIconColor = Color(0xFF6B7280),
                    unselectedTextColor = Color(0xFF6B7280),
                    indicatorColor = Color(0xFF4A6FE7).copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Composable
private fun MainContent(
    selectedItemIndex: Int,
    messageBarState: MessageBarState,
) {
    Column(modifier = Modifier.fillMaxSize()) {
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
}*/
