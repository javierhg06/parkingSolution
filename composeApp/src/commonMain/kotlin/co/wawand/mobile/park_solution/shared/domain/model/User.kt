package co.wawand.mobile.park_solution.shared.domain.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
@OptIn(kotlin.uuid.ExperimentalUuidApi::class)
data class User(
    val id: String = Uuid.random().toHexString(),
    val email: String,
    val name: String,
    val isSuperUser: Boolean,
    val phoneNumber: String,
    val companyId: String,
    val pictureUrl: String? = null
)
