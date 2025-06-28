package co.wawand.mobile.park_solution.ui.profileContent

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import co.wawand.mobile.park_solution.shared.domain.model.CompanyConfig
import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.shared.util.displayResult
import coil3.compose.AsyncImage
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.BusinessTime
import compose.icons.fontawesomeicons.solid.DoorOpen
import compose.icons.fontawesomeicons.solid.IdBadge
import compose.icons.fontawesomeicons.solid.MailBulk
import compose.icons.fontawesomeicons.solid.Pen
import compose.icons.fontawesomeicons.solid.Star
import compose.icons.fontawesomeicons.solid.UserAlt
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileContent(
    onUpdateUser: (User) -> Unit,
    onLogout: () -> Unit,
    currentLanguage: String = "en", // "en" or "es"
    onLanguageChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val viewModel = koinViewModel<ProfileViewModel>()
    val screenState = viewModel.screenState

    var isEditing by remember { mutableStateOf(false) }

    /* LaunchedEffect(user) {
         editedUser = user
     }*/

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        screenState.displayResult(
            onLoading = {},
            onSuccess = { state ->
                if (isEditing && state.currentUser != null) {
                    var editedUser by remember { mutableStateOf(state.currentUser) }

                    EditProfileContent(
                        editedUser = editedUser,
                        onUserChange = { editedUser = it },
                        onSave = {
                            onUpdateUser(editedUser)
                            isEditing = false
                        },
                        onCancel = {
                            editedUser = state.currentUser
                            isEditing = false
                        }
                    )
                } else {
                    DisplayProfileContent(
                        email = state.email,
                        name = state.name,
                        pictureUrl = state.pictureUrl,
                        companyId = state.companyId,
                        isSuperUser = state.isSuperUser,
                        onEditClick = { isEditing = true },
                        onLogout = onLogout,
                        currentLanguage = currentLanguage,
                        onLanguageChange = onLanguageChange
                    )
                }
            },
            onError = {}
        )
    }
}

@Composable
private fun DisplayProfileContent(
    pictureUrl: String?,
    name: String,
    email: String,
    companyId: String,
    isSuperUser: Boolean,
    onEditClick: () -> Unit,
    onLogout: () -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Profile Picture Section
        ProfilePictureSection(
            pictureUrl = pictureUrl,
            name = name,
            modifier = Modifier.size(120.dp)
        )

        // User Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profile Information",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Pen, //Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                HorizontalDivider()

                ProfileInfoItem(
                    label = "Name",
                    value = name,
                    icon = FontAwesomeIcons.Solid.UserAlt,// Icons.Default.Person
                )

                ProfileInfoItem(
                    label = "Email",
                    value = email,
                    icon = FontAwesomeIcons.Solid.MailBulk//Icons.Default.Email
                )

                ProfileInfoItem(
                    label = "Company ID",
                    value = companyId.toString(),
                    icon = FontAwesomeIcons.Solid.BusinessTime//Icons.Default.Business
                )

                if (isSuperUser) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Star,//Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Super User",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFFD700)
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HorizontalDivider()

                    // Logout Button
                    Button(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.DoorOpen,//Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentLanguage == "es") "Cerrar Sesión" else "Logout",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Logout Confirmation Dialog
                if (showLogoutDialog) {
                    AlertDialog(
                        onDismissRequest = { showLogoutDialog = false },
                        title = {
                            Text(if (currentLanguage == "es") "Cerrar Sesión" else "Logout")
                        },
                        text = {
                            Text(if (currentLanguage == "es") "¿Estás seguro de que quieres cerrar sesión?" else "Are you sure you want to logout?")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showLogoutDialog = false
                                    onLogout()
                                }
                            ) {
                                Text(
                                    text = if (currentLanguage == "es") "Sí, cerrar sesión" else "Yes, logout",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showLogoutDialog = false }
                            ) {
                                Text(if (currentLanguage == "es") "Cancelar" else "Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EditProfileContent(
    editedUser: User,
    onUserChange: (User) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
                Button(onClick = onSave) {
                    Text("Save")
                }
            }
        }

        HorizontalDivider()

        // Profile Picture Section (in edit mode)
        ProfilePictureSection(
            pictureUrl = editedUser.pictureUrl,
            name = editedUser.name,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally),
            isEditable = true,
            onPictureUrlChange = { newUrl ->

            }
        )

        // Editable Fields
        OutlinedTextField(
            value = editedUser.name,
            onValueChange = {},
            label = { Text("Name") },
            leadingIcon = {
                Icon(
                    FontAwesomeIcons.Solid.UserAlt,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Super User Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Star,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (editedUser.isSuperUser) Color(0xFFFFD700) else Color.Gray.copy(alpha = 0.6f)
                )
                Text(
                    text = "Super User",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Switch(
                checked = editedUser.isSuperUser,
                onCheckedChange = { onUserChange(editedUser.copy(isSuperUser = it)) },
            )
        }
    }
}

@Composable
private fun ProfileInfoItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            //tint = Color.Blue.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                //color = .current.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ProfilePictureSection(
    pictureUrl: String?,
    name: String,
    modifier: Modifier = Modifier,
    isEditable: Boolean = false,
    onPictureUrlChange: ((String?) -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    var urlInput by remember { mutableStateOf(pictureUrl ?: "") }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (pictureUrl != null && pictureUrl.isNotBlank()) {
            AsyncImage(
                model = pictureUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentScale = ContentScale.Crop,
//                error = {
//                    ProfileInitials(name = name)
//                }
            )
        } else {
            ProfileInitials(
                name = name,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (isEditable) {
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Pen,
                    contentDescription = "Edit Picture",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }

    if (showDialog && isEditable) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Update Profile Picture") },
            text = {
                Column {
                    Text("Enter image URL:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("Image URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onPictureUrlChange?.invoke(urlInput.takeIf { it.isNotBlank() })
                        showDialog = false
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        urlInput = pictureUrl ?: ""
                        showDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProfileInitials(
    name: String,
    modifier: Modifier = Modifier
) {
    val initials = remember(name) {
        name.split(" ")
            .take(2)
            .map { it.firstOrNull()?.uppercaseChar() ?: "" }
            .joinToString("")
    }

    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}