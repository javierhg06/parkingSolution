package co.wawand.mobile.park_solution.ui.companySettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.wawand.mobile.park_solution.shared.DefaultTimeFormat
import co.wawand.mobile.park_solution.shared.domain.model.CompanyConfig
import co.wawand.mobile.park_solution.shared.domain.model.WorkingHours
import co.wawand.mobile.park_solution.shared.domain.repository.CompanyConfigRepository
import co.wawand.mobile.park_solution.shared.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format

data class CompanySettingsState(
    val companyName: String = "",
    val companyAddress: String = "",
    val wifiSSID: String = "",
    val parkingSpaces: Int = 0,
    val existingConfig: CompanyConfig? = null,
    val workingHours: WorkingHours = WorkingHours(
        start = LocalTime(6, 0).format(DefaultTimeFormat),
        end = LocalTime(17, 0).format(DefaultTimeFormat)
    ),

    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false

)

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
        _uiState.update { it.copy(wifiSSID = ssid) }
    }

    fun onParkingSpacesChanged(spaces: Int) {
        _uiState.update { it.copy(parkingSpaces = spaces) }
    }

    fun onWorkingHoursChanged(start: String, end: String) {
        _uiState.update { it.copy(workingHours = WorkingHours(start, end)) }
    }

    private fun getExistingOwnerCompanyConfig() {
        viewModelScope.launch {
            val ownerId = userRepository.getCurrentUserId()
            _uiState.update { it.copy(error = null, isLoading = true) }
            try {
                if (ownerId == null) {
                    _uiState.update {
                        it.copy(
                            existingConfig = null,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    companyConfigRepository.getCompanyByOwnerId(ownerId).collect { companyConfig ->
                        if (companyConfig != null) {
                            _uiState.update {
                                it.copy(
                                    existingConfig = companyConfig,
                                    companyName = companyConfig.name,
                                    wifiSSID = companyConfig.allowedWifiSSID,
                                    parkingSpaces = companyConfig.totalSpaces,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(success = false, error = e.message, isLoading = false) }
            }
        }
    }


    fun addCompanyConfig() {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null, isLoading = true) }
            try {
                val ownerId = userRepository.getCurrentUserId()
                val companyConfig = CompanyConfig(
                    name = _uiState.value.companyName,
                    allowedWifiSSID = _uiState.value.wifiSSID,
                    totalSpaces = _uiState.value.parkingSpaces,
                    ownerId = ownerId ?: "",
                    autoResetTime = true
                )
                println("---------->> companyConfig: $companyConfig")
                companyConfigRepository.addCompanyConfig(companyConfig)
                _uiState.update {
                    it.copy(
                        existingConfig = companyConfig,
                        success = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(success = false, error = e.message, isLoading = false) }
            }
        }
    }

    fun updateCompanyConfig() {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null, isLoading = true) }
            try {
                val companyConfig = CompanyConfig(
                    id = _uiState.value.existingConfig?.id ?: "",
                    name = _uiState.value.companyName,
                    allowedWifiSSID = _uiState.value.wifiSSID,
                    totalSpaces = _uiState.value.parkingSpaces,
                    ownerId = _uiState.value.existingConfig?.ownerId ?: "",
                    autoResetTime = true
                )
                println("---------->> companyConfig: $companyConfig")
                companyConfigRepository.updateCompanyConfig(companyConfig)
                _uiState.update {
                    it.copy(
                        existingConfig = companyConfig,
                        success = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                println("------------------>> updateCompanyConfig: 🔥 Firestore error: ${e.message}")
                _uiState.update { it.copy(success = false, error = e.message, isLoading = false) }
            }
        }
    }

}