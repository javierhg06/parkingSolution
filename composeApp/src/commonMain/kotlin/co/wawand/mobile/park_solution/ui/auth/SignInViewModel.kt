package co.wawand.mobile.park_solution.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.shared.domain.repository.CompanyConfigRepository
import co.wawand.mobile.park_solution.shared.domain.repository.UserRepository
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignInState(
    val email: String = "",
    val currentUserId: String? = null,
    val currentUser: User? = null,
    val isInSuperUserList: Boolean = false,
    val isLoading: Boolean = false,
    val redirectAfterSignIn: RedirectAfterSignIn = RedirectAfterSignIn.Home,
    val errorMessage: String? = null
)

enum class RedirectAfterSignIn {
    Home, CreateCompany, JoinCompany
}

class SignInViewModel(
    private val userRepository: UserRepository,
    private val companyConfigRepository: CompanyConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInState())
    val uiState: StateFlow<SignInState> = _uiState.asStateFlow()

    fun createUser(user: FirebaseUser?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.createUser(user, onSuccess, onError)
        }
    }


    fun signInAndRedirectUser(
        user: FirebaseUser?,
        onSuccess: (RedirectAfterSignIn) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (user == null) {
                onError("User is not available.")
                return@launch
            }

            try {
                val baseUser = getCurrentUserOrNull() ?: User(
                    id = user.uid,
                    email = user.email.orEmpty(),
                    name = user.displayName.orEmpty(),
                    isSuperUser = false,
                    pictureUrl = user.photoURL,
                    companyId = ""
                )

                val finalUser = if (!user.email.isNullOrEmpty()) {
                    val isSuper = try {
                        userRepository.isInSuperUserList(user.email!!).first()
                    } catch (e: Exception) {
                        false
                    }

                    println("--------------------->> isSuper: $isSuper")
                    if (isSuper) {
                        val company = companyConfigRepository.getCompanyByOwnerId(user.uid).first()
                        println("--------------------->> company: $company")
                        val updatedUser = baseUser.copy(
                            isSuperUser = true,
                            companyId = company?.id.orEmpty()
                        )
                        _uiState.update {
                            it.copy(
                                redirectAfterSignIn = if (company == null)
                                    RedirectAfterSignIn.CreateCompany
                                else
                                    RedirectAfterSignIn.Home
                            )
                        }
                        updatedUser
                    } else {
                        println("--------------------->> baseUser 1: $baseUser")
                        if (baseUser.companyId.isNotEmpty()) {
                            _uiState.update { it.copy(redirectAfterSignIn = RedirectAfterSignIn.Home) }
                        } else {
                            _uiState.update { it.copy(redirectAfterSignIn = RedirectAfterSignIn.JoinCompany) }
                        }
                        baseUser
                    }
                } else {
                    println("--------------------->> baseUser 2: $baseUser")
                    if (baseUser.companyId.isNotEmpty()) {
                        _uiState.update { it.copy(redirectAfterSignIn = RedirectAfterSignIn.Home) }
                    } else {
                        _uiState.update { it.copy(redirectAfterSignIn = RedirectAfterSignIn.JoinCompany) }
                    }
                    baseUser
                }

                userRepository.saveUser(finalUser)
                onSuccess(_uiState.value.redirectAfterSignIn)

            } catch (e: Exception) {
                onError(e.message ?: "An error occurred.")
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


    fun addUser(user: FirebaseUser?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (user != null) {
                    val newUser = User(
                        id = user.uid,
                        email = user.email ?: "",
                        name = user.displayName ?: "",
                        isSuperUser = false,
                        pictureUrl = user.photoURL,
                        companyId = ""
                    )

                    userRepository.saveUser(newUser)

                    getCurrentUser(newUser) // update current user in the state
                } else {
                    //onError("User is not available.")
                }
            } catch (e: Exception) {
                //onError(e.message ?: "An error occurred.")
            }
        }
    }

    private fun getCurrentUser(user: User?) {
        _uiState.update { it.copy(currentUser = user) }
    }

    fun isInSuperUserList(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                userRepository.isInSuperUserList(email).collect { isInSuperUserList ->
                    _uiState.value = _uiState.value.copy(isInSuperUserList = isInSuperUserList)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}


/*
      fun signInAndReadUser(
        user: FirebaseUser?,
        onSuccess: (RedirectAfterSignIn) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (user != null) {
                    var newUser = User(
                        id = user.uid,
                        email = user.email ?: "",
                        name = user.displayName ?: "",
                        isSuperUser = false,
                        pictureUrl = user.photoURL,
                        companyId = ""
                    )

                    if (user.email != null) {
                        userRepository.isInSuperUserList(user.email!!)
                            .collect { isInSuperUserList ->
                                if (isInSuperUserList) {
                                    newUser = User(
                                        id = user.uid,
                                        email = user.email!!,
                                        name = user.displayName ?: "",
                                        isSuperUser = true,
                                        pictureUrl = user.photoURL,
                                        companyId = ""
                                    )
                                    companyConfigRepository.getCompanyByOwnerId(newUser.id)
                                        .collect { companyConfig ->
                                            if (companyConfig == null) {
                                                _uiState.update { it.copy(redirectAfterSignIn = RedirectAfterSignIn.CreateCompany) }
                                            } else {
                                                newUser = User(
                                                    id = user.uid,
                                                    email = user.email!!,
                                                    name = user.displayName ?: "",
                                                    isSuperUser = true,
                                                    pictureUrl = user.photoURL,
                                                    companyId = companyConfig.id
                                                )
                                                _uiState.update { it.copy(redirectAfterSignIn = RedirectAfterSignIn.Home) }
                                            }
                                        }
                                } else {
                                    _uiState.update { it.copy(redirectAfterSignIn = RedirectAfterSignIn.JoinCompany) }
                                }
                            }
                    } else {
                        _uiState.update { it.copy(redirectAfterSignIn = RedirectAfterSignIn.JoinCompany) }
                    }

                    userRepository.saveUser(newUser)
                    onSuccess(_uiState.value.redirectAfterSignIn)

                } else {
                    onError("User is not available.")
                }
            } catch (e: Exception) {
                onError(e.message ?: "An error occurred.")
            }
        }
    }

    fun getCompanyConfigByOwnerId() {
        viewModelScope.launch {
            val ownerId = userRepository.getCurrentUserId()
            try {
                companyConfigRepository.getCompanyByOwnerId(ownerId).collect { companyConfig ->
                    if (companyConfig != null) {

                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserId = userRepository.getCurrentUserId()
            try {
                userRepository.getUserById(currentUserId).collect { user ->
                    _uiState.value = _uiState.value.copy(currentUser = user)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }*/