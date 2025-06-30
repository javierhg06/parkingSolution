package co.wawand.mobile.park_solution.ui.companySettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.wawand.mobile.park_solution.shared.DefaultTimeFormat
import co.wawand.mobile.park_solution.shared.domain.model.CompanyConfig
import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.shared.domain.model.WorkingHours
import co.wawand.mobile.park_solution.shared.domain.repository.CompanyConfigRepository
import co.wawand.mobile.park_solution.shared.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format

data class CompanySettingsState(
    val companyName: String = "",
    val companyAddress: String = "",
    val wifiNetwork: String = "",
    val totalParkingSpaces: Int = 2,
    val latitude: String = "",
    val longitude: String = "",
    val existingConfig: CompanyConfig? = null,
    val workingHours: WorkingHours = WorkingHours(
        start = LocalTime(6, 0).format(DefaultTimeFormat),
        end = LocalTime(17, 0).format(DefaultTimeFormat)
    ),

    val createCompanyState: CreateCompanyState = CreateCompanyState.Form,
    val errorMessage: String = "",

    //join company state
    val accessCode: String = "",
    val isJoiningCompany: Boolean = false,
    val joinedCompanyErrorMessage: String = "",
    val redirectToHome: Boolean = false
)

enum class CreateCompanyState {
    Form, Loading, Success, Error
}

class CompanySettingsViewModel(
    private val companyConfigRepository: CompanyConfigRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompanySettingsState())
    val uiState: StateFlow<CompanySettingsState> = _uiState.asStateFlow()

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

    fun onLatitudeChanged(latitude: String) {
        _uiState.update { it.copy(latitude = latitude) }
    }

    fun onLongitudeChanged(longitude: String) {
        _uiState.update { it.copy(longitude = longitude) }
    }

    fun onParkingSpacesChanged(spaces: Int) {
        _uiState.update { it.copy(totalParkingSpaces = spaces) }
    }

    fun onWorkingHoursChanged(start: String, end: String) {
        _uiState.update { it.copy(workingHours = WorkingHours(start, end)) }
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


    //============ save company config for the current user==========================
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

            val config = try {
                createCompanyConfig(currentUser.id)
            } catch (e: Exception) {
                setErrorState("Invalid company data: ${currentUser.id} - ${e.message}")
                return@launch
            }

            val saveSuccess = try {
                companyConfigRepository.saveCompanyConfig(config)
                true
            } catch (e: Exception) {
                setErrorState("Failed to save company config: ${e.message}")
                false
            }

            if (!saveSuccess) return@launch

            val updateSuccess = try {
                userRepository.updateUser(currentUser.copy(companyId = config.id))
                true
            } catch (e: Exception) {
                setErrorState("Failed to update user: ${e.message}")
                false
            }

            if (updateSuccess) {
                _uiState.update {
                    it.copy(
                        existingConfig = config,
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

    private fun createCompanyConfig(ownerId: String): CompanyConfig {
        val state = _uiState.value
        val pan = CompanyConfig(
            name = state.companyName,
            address = state.companyAddress,
            wifiNetwork = state.wifiNetwork,
            totalParkingSpaces = state.totalParkingSpaces,
            ownerId = ownerId,
            latitude = state.latitude.toDouble(),
            longitude = state.longitude.toDouble(),
            accessCode = generateAccessCode()
        )
        println("----------->> pan: $pan")
        return pan
    }

    private fun setErrorState(message: String) {
        _uiState.update {
            it.copy(
                createCompanyState = CreateCompanyState.Error,
                errorMessage = message
            )
        }
    }
    // Until here ==================================================================

    // ================= Join Company ==============================================

    fun onJoinCompanyClick() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isJoiningCompany = true, joinedCompanyErrorMessage = "")
            }

            val company = findCompanyByAccessCode(_uiState.value.accessCode)
            if (company == null) {
                setJoinError("Company not found")
                return@launch
            }

            val currentUser = getCurrentUserOrNull()
            if (currentUser == null) {
                setJoinError("Something went wrong - current user not found")
                return@launch
            }

            try {
                userRepository.saveUser(currentUser.copy(companyId = company.id))
                _uiState.update {
                    it.copy(
                        isJoiningCompany = false,
                        joinedCompanyErrorMessage = "",
                        redirectToHome = true
                    )
                }
            } catch (e: Exception) {
                setJoinError("Error joining company: ${e.message}")
            }
        }
    }


    private suspend fun findCompanyByAccessCode(code: String): CompanyConfig? {
        return try {
            companyConfigRepository.getCompanyByAccessCode(code).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    private fun setJoinError(message: String) {
        _uiState.update {
            it.copy(isJoiningCompany = false, joinedCompanyErrorMessage = message)
        }
    }


    fun onAccessCodeChanged(accessCode: String) {
        _uiState.update { it.copy(accessCode = accessCode, joinedCompanyErrorMessage = "") }
    }

    fun isAccessCodeValid(accessCode: String): Boolean {
        return accessCode.length == 6
    }


    // Until here ================================================================


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


    fun addCompanyConfig() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    errorMessage = "",
                    createCompanyState = CreateCompanyState.Loading
                )
            }
            try {
                val ownerId = userRepository.getCurrentUserId()
                val companyConfig = CompanyConfig(
                    name = _uiState.value.companyName,
                    address = _uiState.value.companyAddress,
                    wifiNetwork = _uiState.value.wifiNetwork,
                    totalParkingSpaces = _uiState.value.totalParkingSpaces,
                    ownerId = ownerId ?: "",
                    latitude = _uiState.value.latitude.toDouble(),
                    longitude = _uiState.value.longitude.toDouble(),
                    accessCode = generateAccessCode()
                )
                println("---------->> companyConfig: $companyConfig")
                companyConfigRepository.saveCompanyConfig(companyConfig)
                _uiState.update {
                    it.copy(
                        existingConfig = companyConfig,
                        createCompanyState = CreateCompanyState.Success,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = e.message ?: "\"Unknown error occurred\"",
                        createCompanyState = CreateCompanyState.Error
                    )
                }
            }
        }
    }

    fun updateCompanyConfig() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    errorMessage = "",
                    createCompanyState = CreateCompanyState.Loading
                )
            }
            try {
                val companyConfig = CompanyConfig(
                    id = _uiState.value.existingConfig?.id ?: "",
                    name = _uiState.value.companyName,
                    address = _uiState.value.companyAddress,
                    wifiNetwork = _uiState.value.wifiNetwork,
                    totalParkingSpaces = _uiState.value.totalParkingSpaces,
                    ownerId = _uiState.value.existingConfig?.ownerId ?: "",
                    latitude = _uiState.value.latitude.toDouble(),
                    longitude = _uiState.value.longitude.toDouble(),
                    accessCode = _uiState.value.existingConfig?.accessCode ?: "",
                )
                println("---------->> companyConfig: $companyConfig")
                companyConfigRepository.updateCompanyConfig(companyConfig)
                _uiState.update {
                    it.copy(
                        existingConfig = companyConfig,
                        createCompanyState = CreateCompanyState.Success
                    )
                }
            } catch (e: Exception) {
                println("------------------>> updateCompanyConfig: 🔥 Firestore error: ${e.message}")
                _uiState.update {
                    it.copy(
                        errorMessage = e.message ?: "Unknown error occurred",
                        createCompanyState = CreateCompanyState.Error
                    )
                }
            }
        }
    }

    fun isFormValid(): Boolean {
        return _uiState.value.companyName.isNotBlank() &&
                _uiState.value.companyAddress.isNotBlank() &&
                _uiState.value.wifiNetwork.isNotBlank() &&
                _uiState.value.totalParkingSpaces > 0 &&
                _uiState.value.latitude.isNotBlank() &&
                _uiState.value.longitude.isNotBlank()
    }

}