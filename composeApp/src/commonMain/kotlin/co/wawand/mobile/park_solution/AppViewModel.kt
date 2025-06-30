package co.wawand.mobile.park_solution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.wawand.mobile.park_solution.shared.Constants.WEB_CLIENT_ID
import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.shared.domain.repository.UserRepository
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class AppState {
    data object Loading : AppState()
    data class Ready(val startDestination: Screen) : AppState()
    data class Error(val message: String) : AppState()
}

data class AppViewModelState(
    val appState: AppState = AppState.Loading
)

class AppViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppViewModelState())
    val uiState: StateFlow<AppViewModelState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                GoogleAuthProvider.create(
                    credentials = GoogleAuthCredentials(serverId = WEB_CLIENT_ID)
                )

                val user = getCurrentUserOrNull()
                println("-------------->> user: $user")

                val destination = when {
                    user == null -> Screen.SignInScreen
                    user.isSuperUser && user.companyId.isEmpty() -> Screen.CreateCompanySettingsScreen
                    user.companyId.isNotEmpty() -> Screen.MainScreen
                    else -> Screen.JoinCompanyScreen
                }
                _uiState.update { it.copy(appState = AppState.Ready(destination)) }

            } catch (e: Exception) {
                println("-------------->> error ==> AppViewModel: ${e.message}")
                _uiState.update { it.copy(appState = AppState.Error("Could not initialize the app. Please check your connection.")) }
            }
        }
    }

    private suspend fun getCurrentUserOrNull(): User? {
        return try {
            userRepository.getCurrentUserId()?.let { id ->
                userRepository.getUserById(id).firstOrNull()
            }
        } catch (e: Exception) {
            null
        }
    }
}

