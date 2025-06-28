package co.wawand.mobile.park_solution.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ParkingSpace(
    val id: Int,
    val occupied: Boolean,
    val occupiedBy: String?,
    val occupiedAt: String?,
    val reservedBy: String?,
    val reservedUntil: String?,
    val companyId: String
)