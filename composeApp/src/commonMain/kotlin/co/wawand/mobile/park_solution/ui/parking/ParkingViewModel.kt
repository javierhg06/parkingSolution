package co.wawand.mobile.park_solution.ui.parking

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.wawand.mobile.park_solution.shared.domain.model.ParkingSpaceWithUser
import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.shared.domain.repository.ParkingSpaceRepository
import co.wawand.mobile.park_solution.shared.domain.repository.UserRepository
import co.wawand.mobile.park_solution.shared.util.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ParkingUiState(
    val isLoading: Boolean = false,
    val parkingSpaces: List<ParkingSpaceWithUser> = emptyList(),
    val currentUser: User? = null,
)

class ParkingViewModel(
    private val userRepository: UserRepository,
    private val parkingSpaceRepository: ParkingSpaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParkingUiState())
    val uiState: StateFlow<ParkingUiState> = _uiState.asStateFlow()

    private val parkingSpace = parkingSpaceRepository.readParkingSpacesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(50000),
            initialValue = RequestState.Loading
        )

    var parkingSpacesState: RequestState<List<ParkingSpaceWithUser>> by mutableStateOf(RequestState.Loading)
        private set

    init {
        viewModelScope.launch {
            getCurrentUserOrNull()?.let { user ->
                setCurrentUser(user)
            }

            parkingSpace.collectLatest { data ->
                if (data.isSuccess()) {
                    val fetchedData = data.getSuccessData()
                    parkingSpacesState = RequestState.Success(data = fetchedData)
                } else if (data.isError()) {
                    parkingSpacesState = RequestState.Error(data.getErrorMessage())
                }
            }
        }
    }

     fun toggleCompanyParkingSpace(
        parkingSpace: ParkingSpaceWithUser,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                parkingSpaceRepository.toggleCompanyParkingSpace(
                    spaceId = parkingSpace.parkingSpace.id,
                    userId = _uiState.value.currentUser?.id
                )
            }

            if (result.isError()) {
                onError(result.getErrorMessage())
            }
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun setParkingSpaces(parkingSpaces: List<ParkingSpaceWithUser>) {
        _uiState.update { it.copy(parkingSpaces = parkingSpaces) }
    }

    private fun setCurrentUser(user: User?) {
        _uiState.update { it.copy(currentUser = user) }
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