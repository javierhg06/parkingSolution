package co.wawand.mobile.park_solution.ui.companySettings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.wawand.mobile.park_solution.shared.domain.model.CompanyConfig
import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.shared.domain.repository.CompanyConfigRepository
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

data class CompanyConfigState(
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val companyConfig: CompanyConfig? = null,

    val name: String = "",
    val address: String = "",
    val wifiNetworkName: String = "",
    val companyParkingNumber: Int = 2,
    val isEditingCompanyConfig: Boolean = false,
)

class CompanyConfigViewModel(
    private val userRepository: UserRepository,
    private val companyConfigRepository: CompanyConfigRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CompanyConfigState())
    val uiState: StateFlow<CompanyConfigState> = _uiState.asStateFlow()

    private val companyConfig = companyConfigRepository.readCompanyConfig()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(50000),
            initialValue = RequestState.Loading
        )

    var companyConfigState: RequestState<CompanyConfig> by mutableStateOf(RequestState.Loading)
        private set


    init {
        viewModelScope.launch {
            getCurrentUserOrNull()?.let { user ->
                setCurrentUser(user)
            }

            companyConfig.collectLatest { data ->
                if (data == null) {
                    companyConfigState = RequestState.Error("Company Config not found.")
                    return@collectLatest
                }

                data.let {
                    if (data.isSuccess()) {
                        companyConfigState = RequestState.Success(data = data.getSuccessData())
                    } else if (data.isError()) {
                        companyConfigState = RequestState.Error(data.getErrorMessage())
                    }
                }
            }
        }
    }

    fun onEditCompanyConfigClick() {
        _uiState.update {
            it.copy(
                name = it.companyConfig?.name ?: "",
                address = it.companyConfig?.address ?: "",
                wifiNetworkName = it.companyConfig?.wifiNetwork ?: "",
                companyParkingNumber = it.companyConfig?.totalParkingSpaces ?: 2,
                isEditingCompanyConfig = true
            )
        }
    }

    fun onCompleteEditCompanyConfigClick(onError: (String) -> Unit) {
        viewModelScope.launch {
            val companyConfig = _uiState.value.companyConfig

            if (companyConfig == null) {
                onError("Company Config is null")
                return@launch
            }

            if (!checkIfThereAreAnyChanges(companyConfig)) {
                resetTempState()
                return@launch
            }

            val result = withContext(Dispatchers.IO) {
                companyConfigRepository.saveCompanyConfig(
                    companyConfig.copy(
                        name = _uiState.value.name,
                        address = _uiState.value.address,
                        wifiNetwork = _uiState.value.wifiNetworkName,
                        totalParkingSpaces = _uiState.value.companyParkingNumber
                    )
                )
            }

            if (result.isSuccess()) {
                resetTempState()
            }

            if (result.isError()) {
                onError(result.getErrorMessage())
            }
        }
    }

    private fun resetTempState() {
        _uiState.update {
            it.copy(
                name = "",
                address = "",
                wifiNetworkName = "",
                companyParkingNumber = 2,
                isEditingCompanyConfig = false
            )
        }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onAddressChanged(address: String) {
        _uiState.update { it.copy(address = address) }
    }

    fun onWifiNetworkNameChanged(wifiNetworkName: String) {
        _uiState.update { it.copy(wifiNetworkName = wifiNetworkName) }
    }

    fun onCompanyParkingNumberChanged(companyParkingNumber: Int) {
        _uiState.update { it.copy(companyParkingNumber = companyParkingNumber) }
    }

    fun setIsLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun setCompanyConfig(config: CompanyConfig) {
        _uiState.update { it.copy(companyConfig = config) }
    }


    private fun checkIfThereAreAnyChanges(companyConfig: CompanyConfig): Boolean {
        val nameChanged = companyConfig.name != _uiState.value.name
        val addressChanged = companyConfig.address != _uiState.value.address
        val wifiNetworkNameChanged = companyConfig.wifiNetwork != _uiState.value.wifiNetworkName
        val companyParkingNumberChanged =
            companyConfig.totalParkingSpaces != _uiState.value.companyParkingNumber

        return nameChanged || addressChanged || wifiNetworkNameChanged || companyParkingNumberChanged
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