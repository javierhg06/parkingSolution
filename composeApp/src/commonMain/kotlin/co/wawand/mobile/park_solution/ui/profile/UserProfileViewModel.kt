package co.wawand.mobile.park_solution.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.shared.domain.repository.UserRepository
import co.wawand.mobile.park_solution.shared.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserProfileUiState(
    val user: User? = null,

    // Editable fields
    val name: String = "",
    val phoneNumber: String = "",
    val pictureUrl: String = "",
    val isEditingProfile: Boolean = false,
    val isLoading: Boolean = false,
)

class UserProfileViewModel(userRepository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    private val userInfo = userRepository.readUserFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(50000),
            initialValue = RequestState.Loading
        )

    var userInformationState: RequestState<User> by mutableStateOf(RequestState.Loading)
        private set

    init {
        viewModelScope.launch {
            userInfo.collectLatest { data ->
                if (data.isSuccess()) {
                    val fetchedData = data.getSuccessData()
                    userInformationState = RequestState.Success(data = fetchedData)
                } else if (data.isError()) {
                    userInformationState = RequestState.Error(data.getErrorMessage())
                }
            }
        }
    }

    fun setUserInformation(user: User) {
        _uiState.update { it.copy(user = user) }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onPhoneNumberChanged(phoneNumber: String) {
        _uiState.update { it.copy(phoneNumber = phoneNumber) }
    }

    fun onPictureUrlChanged(pictureUrl: String) {
        _uiState.update { it.copy(pictureUrl = pictureUrl) }
    }

    fun onEditProfileClicked() {
        loadTempState()
    }

    fun onSaveProfileClicked() {
        resetTempState()
    }

    private fun resetTempState() {
        _uiState.update {
            it.copy(
                name = "",
                phoneNumber = "",
                pictureUrl = "",
                isEditingProfile = false
            )
        }
    }

    private fun loadTempState() {
        _uiState.update {
            it.copy(
                name = uiState.value.user?.name ?: "",
                phoneNumber = "Not Set",
                pictureUrl = uiState.value.user?.pictureUrl ?: "",
                isEditingProfile = true
            )
        }
    }
}