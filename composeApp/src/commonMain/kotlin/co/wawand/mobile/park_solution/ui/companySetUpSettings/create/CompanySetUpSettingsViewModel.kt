package co.wawand.mobile.park_solution.ui.companySetUpSettings.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.wawand.mobile.park_solution.shared.domain.model.CompanyConfig
import co.wawand.mobile.park_solution.shared.domain.model.ParkingSpace
import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.shared.domain.repository.CompanyConfigRepository
import co.wawand.mobile.park_solution.shared.domain.repository.ParkingSpaceRepository
import co.wawand.mobile.park_solution.shared.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class CompanySetUpSettingsState(
    val companyName: String = "",
    val companyAddress: String = "",
    val wifiNetwork: String = "",
    val totalParkingSpaces: Int = 2,
    val existingConfig: CompanyConfig? = null,

    val createCompanyState: CreateCompanyState = CreateCompanyState.Form,
    val errorMessage: String = "",
)

enum class CreateCompanyState {
    Form, Loading, Success, Error
}

class CompanySetUpSettingsViewModel(
    private val companyConfigRepository: CompanyConfigRepository,
    private val parkingSpaceRepository: ParkingSpaceRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompanySetUpSettingsState())
    val uiState: StateFlow<CompanySetUpSettingsState> = _uiState.asStateFlow()

    init {
        getExistingOwnerCompanyConfig()
    }

    fun onCompanyNameChanged(name: String) {
        _uiState.update { it.copy(companyName = name) }
    }

    fun onCompanyAddressChanged(address: String) {
        _uiState.update { it.copy(companyAddress = address) }
    }

    fun onWifiSSIDChanged(ssid: String) {
        _uiState.update { it.copy(wifiNetwork = ssid) }
    }

    fun onParkingSpacesChanged(spaces: Int) {
        _uiState.update { it.copy(totalParkingSpaces = spaces) }
    }

    private fun generateAccessCode(): String {
        return (100000..999999).random().toString()
    }

    fun onRetry() {
        _uiState.update {
            it.copy(
                createCompanyState = CreateCompanyState.Form,
                errorMessage = ""
            )
        }
    }

    fun saveCompanyConfigAndUpdateCurrentUser() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    errorMessage = "",
                    createCompanyState = CreateCompanyState.Loading
                )
            }

            val currentUser = getCurrentUserOrNull()
            if (currentUser == null) {
                setErrorState("Current user not found")
                return@launch
            }

            val companyConfig = buildCompanyConfig(currentUser.id)

            val saveSuccess = try {
                companyConfigRepository.saveCompanyConfigFromSetUp(companyConfig)
                true
            } catch (e: Exception) {
                setErrorState("Failed to save company config: ${e.message}")
                false
            }

            if (!saveSuccess) return@launch

            try {
                for (i in 1..companyConfig.totalParkingSpaces) {
                    parkingSpaceRepository.saveCompanyParkingSpace(
                        ParkingSpace(
                            occupiedBy = null,
                            companyId = companyConfig.id,
                            createdAt = Clock.System.now(),
                        )
                    )
                }
            } catch (e: Exception) {
                setErrorState("Failed to save company spaces: ${e.message}")
            }

            val updateSuccess = try {
                userRepository.updateUser(currentUser.copy(companyId = companyConfig.id))
                true
            } catch (e: Exception) {
                setErrorState("Failed to update user: ${e.message}")
                false
            }

            if (updateSuccess) {
                _uiState.update {
                    it.copy(
                        existingConfig = companyConfig,
                        createCompanyState = CreateCompanyState.Success
                    )
                }
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

    private fun buildCompanyConfig(ownerId: String): CompanyConfig {
        return CompanyConfig(
            name = _uiState.value.companyName,
            address = _uiState.value.companyAddress,
            wifiNetwork = _uiState.value.wifiNetwork,
            totalParkingSpaces = _uiState.value.totalParkingSpaces,
            ownerId = ownerId,
            accessCode = generateAccessCode()
        )
    }

    private fun setErrorState(message: String) {
        _uiState.update {
            it.copy(
                createCompanyState = CreateCompanyState.Error,
                errorMessage = message
            )
        }
    }

    private fun getExistingOwnerCompanyConfig() {
        viewModelScope.launch {
            val ownerId = userRepository.getCurrentUserId()
            try {
                if (ownerId == null) {
                    _uiState.update {
                        it.copy(existingConfig = null)
                    }
                } else {
                    companyConfigRepository.getCompanyByOwnerId(ownerId).collect { companyConfig ->
                        if (companyConfig != null) {
                            _uiState.update {
                                it.copy(
                                    existingConfig = companyConfig,
                                    companyName = companyConfig.name,
                                    companyAddress = companyConfig.address,
                                    wifiNetwork = companyConfig.wifiNetwork,
                                    totalParkingSpaces = companyConfig.totalParkingSpaces,
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                /*_uiState.update {
                    it.copy(
                        createCompanyState = CreateCompanyState.Error,
                        errorMessage = e.message ?: "Unknown error occurred"
                    )
                }*/
            }
        }
    }

    fun isFormValid(): Boolean {
        return _uiState.value.companyName.isNotBlank() &&
                _uiState.value.companyAddress.isNotBlank() &&
                _uiState.value.wifiNetwork.isNotBlank() &&
                _uiState.value.totalParkingSpaces > 0
    }

}