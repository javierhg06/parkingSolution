package co.wawand.mobile.park_solution.ui.companySettings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.wawand.mobile.park_solution.shared.domain.model.CompanyConfig
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Check
import compose.icons.fontawesomeicons.solid.Clock
import compose.icons.fontawesomeicons.solid.Minus
import compose.icons.fontawesomeicons.solid.Pen
import compose.icons.fontawesomeicons.solid.Plus
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CompanySettingsContent() {
    val viewModel = koinViewModel<CompanySettingsViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Company Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )

            IconButton(
                onClick = {
                    if (isEditing) {
                        if (uiState.existingConfig != null) {
                            viewModel.updateCompanyConfig()
                        } else {
                            viewModel.addCompanyConfig()
                        }

                        isEditing = false
                    } else {
                        isEditing = true
                    }
                },
            ) {
                Icon(
                    imageVector = if (isEditing) FontAwesomeIcons.Solid.Check else FontAwesomeIcons.Solid.Pen,
                    contentDescription = "Edit",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Company Information Section
        SettingsCard(
            title = "Company Information"
        ) {
            OutlinedTextField(
                value = uiState.companyName,
                onValueChange = { viewModel.onCompanyNameChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Company Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4A6FE7),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                maxLines = 2,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),

                shape = RoundedCornerShape(8.dp),
                readOnly = !isEditing
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // WiFi Configuration Section
        SettingsCard(
            title = "WiFi Configuration"
        ) {
            OutlinedTextField(
                value = uiState.wifiSSID,
                onValueChange = { viewModel.onWifiSSIDChanged(it) },
                placeholder = { Text(text = "WiFi Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4A6FE7),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.clearFocus()
                    }
                ),
                shape = RoundedCornerShape(8.dp),
                readOnly = !isEditing
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Users must connect to this WiFi to mark parking spaces",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Parking Spaces Management Section
        SettingsCard(
            title = "Parking Spaces Management"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Spaces: ${uiState.parkingSpaces}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                AnimatedVisibility(visible = isEditing) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Add button
                        FloatingActionButton(
                            onClick = { viewModel.onParkingSpacesChanged(uiState.parkingSpaces + 1) },
                            modifier = Modifier.size(48.dp),
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Plus,
                                contentDescription = "Add space",
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Remove button
                        FloatingActionButton(
                            onClick = {
                                if (uiState.parkingSpaces > 2) viewModel.onParkingSpacesChanged(
                                    uiState.parkingSpaces - 1
                                ) // Minimum 2 spaces
                            },
                            modifier = Modifier.size(48.dp),
                            containerColor = Color(0xFFE53E3E),
                            contentColor = Color.White
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Minus,
                                contentDescription = "Add space",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Working Hours Section
        SettingsCard(
            title = "Working Hours"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Start Time
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Start Time",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    TimePickerField(
                        time = LocalTime(6, 0),
                        onClick = { /*showStartTimePicker = true*/ }
                    )
                }

                // End Time
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "End Time",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    TimePickerField(
                        time = LocalTime(17, 0), // Replace with your endTime,
                        onClick = { /*showEndTimePicker = true*/ }
                    )
                }
            }
        }
    }

    // Time Pickers (Note: In real implementation, you'd use a proper time picker dialog)
    if (showStartTimePicker) {
        TimePickerDialog(
            time = LocalTime.parse(uiState.workingHours.start),
            onTimeSelected = { newTime ->
                viewModel.onWorkingHoursChanged(
                    newTime.toString(),
                    uiState.workingHours.end
                ) // Replace with your endTime = newTime
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false }
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            time = LocalTime.parse(uiState.workingHours.end),
            onTimeSelected = { newTime ->
                viewModel.onWorkingHoursChanged(uiState.workingHours.start, newTime.toString())
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false }
        )
    }
}

@Composable
fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            content()
        }
    }
}

@Composable
fun TimePickerField(
    time: LocalTime,
    onClick: () -> Unit
) {
    val timeFormat = LocalTime.Format {
        hour(padding = Padding.SPACE)
        char(':')
        minute(padding = Padding.SPACE)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = time.format(timeFormat),
                fontSize = 16.sp,
                color = Color.Black
            )

            Icon(
                imageVector = FontAwesomeIcons.Solid.Clock,
                contentDescription = "Select time",
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Simplified time picker dialog (in real app, use Material3 TimePicker)
@Composable
fun TimePickerDialog(
    time: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timeFormat = LocalTime.Format {
        hour(padding = Padding.SPACE)
        char(':')
        minute(padding = Padding.SPACE)
        char(' ')
        amPmMarker("AM", "PM")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Select Time")
        },
        text = {
            // This is a simplified version. In a real app, you'd use:
            // TimePicker or a custom time picker implementation
            Column {
                Text("Current time: ${time.format(timeFormat)}")
                Text(
                    "(Use Material3 TimePicker in production)",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onTimeSelected(time) }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}