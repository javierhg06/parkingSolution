package co.wawand.mobile.park_solution.shared.data

import co.wawand.mobile.park_solution.shared.domain.model.ParkingSpace
import co.wawand.mobile.park_solution.shared.domain.model.ParkingSpaceWithUser
import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.shared.domain.repository.ParkingSpaceRepository
import co.wawand.mobile.park_solution.shared.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.QuerySnapshot
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Clock

class ParkingSpaceRepositoryImpl : ParkingSpaceRepository {

    private fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override suspend fun saveCompanyParkingSpace(parkingSpace: ParkingSpace) {
        Firebase.firestore.collection("company_parking_spaces")
            .document(parkingSpace.id)
            .set(parkingSpace)
    }

    override suspend fun deleteCompanyParkingSpace(companyConfigId: String) {
        Firebase.firestore.collection("company_parking_spaces")
            .where { "companyConfigId" equalTo companyConfigId }
            .where { "occupied" equalTo false }
            .orderBy("createdAt")

        val collection = Firebase.firestore.collection("company_parking_spaces")

        // 1. Obtener todos los espacios desocupados, ordenados por createdAt descendente
        val query = collection
            .where { "companyConfigId" equalTo companyConfigId }
            //.where { "isOccupied" equalTo false }
            .orderBy(field = "createdAt", direction = Direction.DESCENDING)// más nuevos primero
            .limit(1)
            .get()

        // 2. Eliminar cada documento
        query.documents.forEach { doc ->
            doc.reference.delete()
        }
    }

    override suspend fun toggleCompanyParkingSpace(
        spaceId: String,
        userId: String?
    ): RequestState<Unit> {
        if (userId == null) return RequestState.Error("User not logged in")

        return try {
            val collection = Firebase.firestore.collection("company_parking_spaces")

            val targetRef = collection.document(spaceId)
            val targetSnapshot = targetRef.get()

            if (!targetSnapshot.exists) {
                return RequestState.Error("Parking space does not exist.")
            }

            val targetSpace = targetSnapshot.data<ParkingSpace>()
                ?: return RequestState.Error("Invalid space data")

            // 🔄 1. If trying to occupy a space, first clear any other spaces occupied by user
            if (!targetSpace.occupied || targetSpace.occupiedBy == userId) {
                // Step 1: free all other spaces occupied by this user
                val userSpacesSnapshot = collection
                    .where { "occupiedBy" equalTo userId }
                    .get()

                userSpacesSnapshot.documents.forEach { doc ->
                    val space = doc.data<ParkingSpace>() ?: return@forEach
                    if (space.id != spaceId) {
                        // Desocupar otros espacios distintos del seleccionado
                        collection.document(space.id).set(
                            space.copy(
                                occupied = false,
                                occupiedBy = null,
                                occupiedAt = null
                            )
                        )
                    }
                }

                // Step 2: toggle current target space
                val updatedSpace = if (targetSpace.occupiedBy == userId) {
                    // User is leaving this space
                    targetSpace.copy(
                        occupied = false,
                        occupiedBy = null,
                        occupiedAt = null
                    )
                } else {
                    // User is moving into this space
                    targetSpace.copy(
                        occupied = true,
                        occupiedBy = userId,
                        occupiedAt = Clock.System.now()
                    )
                }

                targetRef.set(updatedSpace)
                return RequestState.Success(Unit)
            }

            return RequestState.Error("Parking space is already occupied.")

        } catch (e: Exception) {
            RequestState.Error("Error performing action: ${e.message}")
        }
    }


    override fun readParkingSpacesFlow(): Flow<RequestState<List<ParkingSpaceWithUser>>> =
        channelFlow {
            try {
                val userId = getCurrentUserId()
                if (userId == null) {
                    send(RequestState.Error("User is not available."))
                    return@channelFlow
                }

                val documentSnapshot = Firebase.firestore
                    .collection("users")
                    .document(userId)
                    .get()

                val user = if (documentSnapshot.exists) {
                    documentSnapshot.data<User>()
                } else {
                    null
                }

                if (user == null) {
                    send(RequestState.Error("User data not found."))
                    return@channelFlow
                }

                Firebase.firestore
                    .collection("company_parking_spaces")
                    .where { "companyId" equalTo user.companyId }
                    .orderBy("createdAt")
                    .snapshots()
                    .collectLatest { querySnapshot ->
                        val liveSpaces = mapSnapshotToParkingWithUser(querySnapshot)
                        send(RequestState.Success(data = liveSpaces))
                    }
            } catch (e: Exception) {
                send(RequestState.Error(e.message ?: "Unexpected error"))
            }
        }


    private suspend fun mapSnapshotToParkingWithUser(snapshot: QuerySnapshot): List<ParkingSpaceWithUser> {
        return snapshot.documents.map { doc ->
            val space = doc.data<ParkingSpace>()
            val user = space.occupiedBy?.let { uid ->
                try {
                    Firebase.firestore
                        .collection("users")
                        .document(uid)
                        .get()
                        .data<User>()
                } catch (_: Exception) {
                    null
                }
            }
            ParkingSpaceWithUser(space, user)
        }
    }
}