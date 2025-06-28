package co.wawand.mobile.park_solution.shared.data

import co.wawand.mobile.park_solution.shared.domain.model.ParkingSpace
import co.wawand.mobile.park_solution.shared.domain.repository.ParkingRepository

class ParkingRepositoryImpl: ParkingRepository {
    override fun createParkingSpace(
        space: ParkingSpace,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

}