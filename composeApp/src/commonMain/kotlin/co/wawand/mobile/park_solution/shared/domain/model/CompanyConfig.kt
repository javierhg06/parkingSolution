package co.wawand.mobile.park_solution.shared.domain.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
@OptIn(kotlin.uuid.ExperimentalUuidApi::class)
data class CompanyConfig(
    val id: String = Uuid.random().toHexString(),
    val name: String,
    val address: String,
    val wifiNetwork: String,
    val totalParkingSpaces: Int,
    val accessCode: String,
    val ownerId: String
)

@Serializable
data class WorkingHours(
    val start: String,
    val end: String
)