package co.wawand.mobile.park_solution.shared.data

import co.wawand.mobile.park_solution.shared.domain.model.CompanyConfig
import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.shared.domain.repository.UserRepository
import co.wawand.mobile.park_solution.shared.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class UserRepositoryImpl : UserRepository {

    override fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override suspend fun createUser(
        user: FirebaseUser?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            if (user != null) {
                val userCollection = Firebase.firestore.collection(collectionPath = "users")
                val newUser = User(
                    id = user.uid,
                    email = user.email ?: "",
                    name = user.displayName ?: "",
                    isSuperUser = false,
                    pictureUrl = user.photoURL,
                    companyId = ""
                )

                val userExists = userCollection.document(newUser.id).get().exists
                if (userExists) {
                    onSuccess()
                } else {
                    userCollection.document(newUser.id).set(newUser)
                    onSuccess()
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError(e.message ?: "An error occurred.")
        }
    }

    override fun readUserFlow(): Flow<RequestState<User>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val userCollection = Firebase.firestore.collection(collectionPath = "users")
                val userSnapshot = userCollection.document(userId).snapshots
                userSnapshot.collectLatest { document ->
                    if (document.exists) {
                        val user = User(
                            id = document.id,
                            email = document.get(field = "email"),
                            name = document.get(field ="name"),
                            isSuperUser = document.get(field = "isSuperUser"),
                            companyId = document.get(field = "companyId")
                        )
                        send(RequestState.Success(data = user))
                    } else {
                        send(RequestState.Error("Queried user does not exist."))
                    }
                }
            } else {
                send(RequestState.Error("User is not available."))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while reading user: ${e.message}"))
        }
    }

    override suspend fun signOut(): RequestState<Unit> {
        return try {
            Firebase.auth.signOut()
            RequestState.Success(data = Unit)
        } catch (e: Exception) {
            RequestState.Error("Error while signing out: ${e.message}")
        }
    }

}