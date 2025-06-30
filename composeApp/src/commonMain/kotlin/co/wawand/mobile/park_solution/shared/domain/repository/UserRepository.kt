package co.wawand.mobile.park_solution.shared.domain.repository

import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.shared.util.RequestState
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUserId(): String?

    fun getUserById(id: String): Flow<User?>

    fun isInSuperUserList(email: String): Flow<Boolean>

    suspend fun createUser(
        user: FirebaseUser?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    suspend fun saveUser(user: User)

    suspend fun updateUser(user: User)

    fun readUserFlow(): Flow<RequestState<User>>
    suspend fun signOut(): RequestState<Unit>
}