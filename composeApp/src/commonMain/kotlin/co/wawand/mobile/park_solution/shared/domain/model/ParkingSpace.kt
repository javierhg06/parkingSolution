package co.wawand.mobile.park_solution.shared.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@OptIn(kotlin.uuid.ExperimentalUuidApi::class)
@Serializable
data class ParkingSpace(
    val id: String = Uuid.random().toHexString(),
    val occupied: Boolean = false,
    val occupiedBy: String?,
    val occupiedAt: Instant? = null,
    val reservedBy: String? = null,
    val reservedUntil: String? = null,
    val companyId: String,
    val createdAt: Instant
)

@Serializable
data class ParkingSpaceWithUser(
    val parkingSpace: ParkingSpace,
    val user: User?
)