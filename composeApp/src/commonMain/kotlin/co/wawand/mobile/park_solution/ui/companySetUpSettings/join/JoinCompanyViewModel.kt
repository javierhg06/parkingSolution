package co.wawand.mobile.park_solution.ui.companySetUpSettings.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.wawand.mobile.park_solution.shared.domain.model.CompanyConfig
import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.shared.domain.repository.CompanyConfigRepository
import co.wawand.mobile.park_solution.shared.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class JoinCompanyState(
    val isLoading: Boolean = false,
    val accessCode: String = "",
    val isJoiningCompany: Boolean = false,
    val joinedCompanyErrorMessage: String = "",
    val redirectToHome: Boolean = false
)

class JoinCompanyViewModel(
    private val userRepository: UserRepository,
    private val companyConfigRepository: CompanyConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinCompanyState())
    val uiState: StateFlow<JoinCompanyState> = _uiState.asStateFlow()

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