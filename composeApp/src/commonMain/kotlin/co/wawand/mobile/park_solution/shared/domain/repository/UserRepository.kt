package co.wawand.mobile.park_solution.shared.domain.repository

import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.shared.util.RequestState
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUserId(): String?

    suspend fun createUser(
        user: FirebaseUser?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    fun readUserFlow(): Flow<RequestState<User>>
    suspend fun signOut():RequestState<Unit>
}