package co.wawand.mobile.park_solution.shared.domain.repository

import co.wawand.mobile.park_solution.shared.domain.model.ParkingSpace

interface ParkingRepository {
    fun createParkingSpace(
        space: ParkingSpace,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}