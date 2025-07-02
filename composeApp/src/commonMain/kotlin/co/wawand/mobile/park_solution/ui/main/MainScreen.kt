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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.wawand.mobile.park_solution.ui.companySettings.CompanySettingsContent
import co.wawand.mobile.park_solution.ui.parking.ParkingContent
import co.wawand.mobile.park_solution.ui.profileContent.newDesign.UserProfileContent
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Bars
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.CarAlt
import compose.icons.fontawesomeicons.solid.Tools
import compose.icons.fontawesomeicons.solid.User
import compose.icons.fontawesomeicons.solid.UserAlt
import compose.icons.fontawesomeicons.solid.Walking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@Composable
fun MainScreen(navigateToSignIn: () -> Unit) {
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
                errorContainerColor = Color(0xFFB71C1C),
                errorContentColor = Color.White,
                contentBackgroundColor = Color.Blue
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
        NavigationItems("Parking", FontAwesomeIcons.Solid.Car, FontAwesomeIcons.Solid.CarAlt),
        NavigationItems("Settings", FontAwesomeIcons.Solid.Tools, FontAwesomeIcons.Solid.Tools),
        NavigationItems("Profile", FontAwesomeIcons.Solid.User, FontAwesomeIcons.Solid.UserAlt)
    )

    ModalDrawerSheet {
        Box(Modifier.fillMaxHeight().fillMaxWidth(0.8f)) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Spacer(Modifier.height(12.dp))
                Text("Menu", Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()

                items.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        selected = index == selectedItemIndex,
                        onClick = { onItemSelected(index) },
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

                HorizontalDivider(Modifier.padding(vertical = 8.dp))
            }

            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                NavigationDrawerItem(
                    label = { Text("Sign Out") },
                    selected = false,
                    icon = {
                        Icon(
                            FontAwesomeIcons.Solid.Walking,
                            contentDescription = null,
                            Modifier.size(24.dp)
                        )
                    },
                    onClick = onSignOut,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.height(12.dp))
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
    Column(Modifier.fillMaxSize()) {
        TopBar(onMenuClick = {
            scope.launch {
                if (drawerState.isClosed) drawerState.open() else drawerState.close()
            }
        })

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(16.dp)
        ) {
            when (selectedItemIndex) {
                0 -> ParkingContent(messageBarState = messageBarState)
                1 -> CompanySettingsContent(messageBarState = messageBarState)
                2 -> UserProfileContent(messageBarState = messageBarState)
            }
        }
    }
}


@Composable
private fun TopBar(onMenuClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(FontAwesomeIcons.Solid.Bars, contentDescription = "Menu", Modifier.size(32.dp))
        }

        Column(Modifier.fillMaxWidth()) {
            Text(
                "Park Solution",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text("Connected", color = Color.White, fontSize = 14.sp)
        }
    }
}

data class NavigationItems(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null
)

