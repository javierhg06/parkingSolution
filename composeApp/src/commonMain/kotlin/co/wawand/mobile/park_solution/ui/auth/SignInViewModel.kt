package co.wawand.mobile.park_solution.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.wawand.mobile.park_solution.shared.domain.repository.UserRepository
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class SignInViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun createUser(user: FirebaseUser?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.createUser(user, onSuccess, onError)
        }
    }
}