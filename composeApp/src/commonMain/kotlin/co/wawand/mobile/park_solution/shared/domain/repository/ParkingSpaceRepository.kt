package co.wawand.mobile.park_solution.shared.domain.repository

import co.wawand.mobile.park_solution.shared.domain.model.ParkingSpace
import co.wawand.mobile.park_solution.shared.domain.model.ParkingSpaceWithUser
import co.wawand.mobile.park_solution.shared.util.RequestState
import kotlinx.coroutines.flow.Flow

interface ParkingSpaceRepository {
    suspend fun saveCompanyParkingSpace(parkingSpace: ParkingSpace)
    suspend fun deleteCompanyParkingSpace(companyConfigId: String)
    suspend fun toggleCompanyParkingSpace(
        spaceId: String,
        userId: String?
    ): RequestState<Unit>

    fun readParkingSpacesFlow(): Flow<RequestState<List<ParkingSpaceWithUser>>>
}