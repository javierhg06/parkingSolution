package co.wawand.mobile.park_solution.shared.domain.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
@OptIn(kotlin.uuid.ExperimentalUuidApi::class)
data class CompanyConfig(
    val id: String =Uuid.random().toHexString(),
    val name: String,
    val allowedWifiSSID: String,
    val totalSpaces: Int,
    val autoResetTime: Boolean,
    val ownerId: String
    //val workingHours: WorkingHours,
    //val location: Location
)

@Serializable
data class Location(
    val lat: Double,
    val lng: Double
)

@Serializable
data class WorkingHours(
    val start: String,
    val end: String
)