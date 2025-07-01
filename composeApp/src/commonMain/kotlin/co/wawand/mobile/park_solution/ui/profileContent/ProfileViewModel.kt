package co.wawand.mobile.park_solution.ui.profileContent

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.shared.domain.repository.UserRepository
import co.wawand.mobile.park_solution.shared.util.RequestState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileState(
    val currentUser: User? = null,
    val editedUser: User? = null,
    val email: String = "",
    val name: String = "",
    val pictureUrl: String? = null,
    val isSuperUser: Boolean = false,
    val companyId: String = "",
)

class ProfileViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val currentUser = userRepository.readUserFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(50000),
            initialValue = RequestState.Loading
        )

    var screenState: RequestState<ProfileState> by mutableStateOf(RequestState.Loading)
        private set

    init {
        viewModelScope.launch {
            currentUser.collectLatest { data ->
                if (data.isSuccess()) {
                    val fetchedData = data.getSuccessData()
                    screenState = RequestState.Success(
                        data = ProfileState(
                            currentUser = fetchedData,
                            email = fetchedData.email,
                            name = fetchedData.name,
                            isSuperUser = fetchedData.isSuperUser,
                            companyId = fetchedData.companyId,
                        )
                    )
                } else if (data.isError()) {
                    screenState = RequestState.Error(data.getErrorMessage())
                }
            }
        }
    }
}