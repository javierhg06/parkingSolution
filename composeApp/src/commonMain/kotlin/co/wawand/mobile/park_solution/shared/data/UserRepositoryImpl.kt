package co.wawand.mobile.park_solution.shared.data

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
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl : UserRepository {

    override fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override fun getUserById(id: String) = flow {
        val document = Firebase.firestore.collection("users").document(id).get()
        emit(document.data<User>())
    }

    override fun isInSuperUserList(email: String) = flow {
        val querySnapshot = Firebase.firestore.collection("super_users")
            .where { "email" equalTo email }
            .limit(1)
            .get()

        emit(querySnapshot.documents.isNotEmpty())
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
                    phoneNumber = user.phoneNumber ?: "",
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

    override suspend fun saveUser(user: User) {
        try {
            val userCollection = Firebase.firestore.collection("users")
            userCollection.document(user.id).set(user)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateUser(user: User) {
        Firebase.firestore.collection("users")
            .document(user.id).set(user)
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
                            name = document.get(field = "name"),
                            phoneNumber = document.get(field = "phoneNumber"),
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