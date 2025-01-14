package org.product.inventory.web.pages.newrequest

import org.product.inventory.web.components.ProfileInfo
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.di.DiJs
import org.product.inventory.web.models.toInventoryAppRoleUi
import user.model.InventoryAppUser

sealed interface UserListDialogState {

    data object Loading : UserListDialogState

    data class Error(val message: String) : UserListDialogState

    data class Success(val users: List<UserListItem>) : UserListDialogState
}

fun UserListDialogState.mapSuccess(
    transform: (List<UserListItem>) -> List<UserListItem>,
): UserListDialogState = when (this) {
    is UserListDialogState.Success -> UserListDialogState.Success(transform(users))
    else -> this
}

data class UserListItem(
    val id: String,
    val profileInfo: ProfileInfo,
)

fun InventoryAppUser.toUserListItem(
    dictionary: Dictionary = DiJs.get(),
) = UserListItem(
    id = id,
    profileInfo = ProfileInfo(
        icon = profileImageUrl,
        email = email,
        role = role.toInventoryAppRoleUi(dictionary).text,
    ),
)
