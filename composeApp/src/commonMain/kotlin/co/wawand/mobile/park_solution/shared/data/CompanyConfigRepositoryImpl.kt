package co.wawand.mobile.park_solution.shared.data

import co.wawand.mobile.park_solution.shared.domain.model.CompanyConfig
import co.wawand.mobile.park_solution.shared.domain.model.ParkingSpace
import co.wawand.mobile.park_solution.shared.domain.model.User
import co.wawand.mobile.park_solution.shared.domain.repository.CompanyConfigRepository
import co.wawand.mobile.park_solution.shared.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock

class CompanyConfigRepositoryImpl : CompanyConfigRepository {
    override fun readCompanyConfig(): Flow<RequestState<CompanyConfig>?> = channelFlow {
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
                .collection("company_configs")
                .document(user.companyId)
                .snapshots()
                .collectLatest { querySnapshot ->
                    val liveConfig = querySnapshot.data<CompanyConfig>()
                    send(RequestState.Success(data = liveConfig))
                }
        } catch (e: Exception) {
            send(RequestState.Error(e.message ?: "Unexpected error"))
        }
    }

    override fun getCompaniesConfig() = flow {
        Firebase.firestore.collection("company_configs").snapshots.collect { querySnapshot ->
            val companies = querySnapshot.documents.map { documentSnapshot ->
                documentSnapshot.data<CompanyConfig>()
            }
            emit(companies)
        }
    }

    override fun getCompanyConfigById(id: String) = flow {
        val doc = Firebase.firestore
            .collection("company_configs")
            .document(id)
            .get()
        emit(doc.data<CompanyConfig>())
    }

    override fun getCompanyByOwnerId(ownerId: String) = flow {
        val querySnapshot = Firebase.firestore
            .collection("company_configs")
            .where { "ownerId" equalTo ownerId }
            .limit(1)
            .get()


        val config = querySnapshot.documents.firstOrNull()?.data<CompanyConfig>()
        println("---------->> getCompanyByOwnerId ownerId: $ownerId")
        println("---------->> getCompanyByOwnerId  config: $config")
        emit(config)
    }

    override fun getCompanyByAccessCode(accessCode: String) = flow {
        val querySnapshot = Firebase.firestore
            .collection("company_configs")
            .where { "accessCode" equalTo accessCode }
            .limit(1)
            .get()


        val config = querySnapshot.documents.firstOrNull()?.data<CompanyConfig>()
        println("---------->> getCompanyByAccessCode ownerId: $accessCode")
        println("---------->> getCompanyByAccessCode  config: $config")
        emit(config)
    }

    override suspend fun saveCompanyConfigFromSetUp(companyConfig: CompanyConfig) {
        Firebase.firestore.collection("company_configs")
            .document(companyConfig.id)
            .set(companyConfig)
    }

    override suspend fun saveCompanyConfig(companyConfig: CompanyConfig): RequestState<Unit> {
        return try {
            // Save company Config
            val configRef = Firebase.firestore
                .collection("company_configs")
                .document(companyConfig.id)

            configRef.set(companyConfig)

            // Get current number of company parking spaces ordered by the newest to the oldest one
            val parkingSpacesRef = Firebase.firestore.collection("company_parking_spaces")

            val currentSpacesSnapshot = parkingSpacesRef
                .where { "companyId" equalTo companyConfig.id }
                .orderBy("createdAt")
                .get()

            val currentSpaces = currentSpacesSnapshot.documents.map { it.data<ParkingSpace>() }

            val currentCount = currentSpaces.size
            val desiredCount = companyConfig.totalParkingSpaces

            when {
                currentCount > desiredCount -> {
                    // if there are more spaces than desired -> delete them
                    val toDelete = currentSpaces.takeLast(currentCount - desiredCount)
                    toDelete.forEach { space ->
                        parkingSpacesRef.document(space.id).delete()
                    }
                }

                currentCount < desiredCount -> {
                    // if there are less spaces than desired -> add them
                    val toAdd = desiredCount - currentCount
                    repeat(toAdd) {
                        val newSpace = ParkingSpace(
                            companyId = companyConfig.id,
                            occupied = false,
                            occupiedBy = null,
                            occupiedAt = null,
                            createdAt = Clock.System.now(),
                        )
                        parkingSpacesRef.document(newSpace.id).set(newSpace)
                    }
                }
            }

            RequestState.Success(Unit)
        } catch (e: Exception) {
            RequestState.Error(e.message ?: "Unexpected error")
        }
    }


    override suspend fun deleteCompanyConfig(companyConfig: CompanyConfig) {
        Firebase.firestore.collection("company_configs")
            .document(companyConfig.id)
            .delete()
    }


    private fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }
}
