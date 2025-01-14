package user.model

import kotlinx.serialization.Serializable

@Serializable
data class InventoryAppUser(
    val id: String,
    val email: String,
    val profileImageUrl: String,
    val role: InventoryAppRole,
    val hasPermanentGarageAccess: Boolean = false,
)

fun InventoryAppUser.getUsername() = email.substringBefore("@")

@Serializable
sealed class InventoryAppRole(val authLevel: Int) : Comparable<InventoryAppRole> {

    override fun compareTo(other: InventoryAppRole) = authLevel.compareTo(other.authLevel)

    @Serializable
    data object User : InventoryAppRole(authLevel = 1)

    @Serializable
    data object Manager : InventoryAppRole(authLevel = 2)

    @Serializable
    data object Administrator : InventoryAppRole(authLevel = 3)
}
