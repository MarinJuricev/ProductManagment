package org.product.inventory.web.pages.crewmanagement

import core.utils.IsEmailValid
import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.BreadcrumbItem
import org.product.inventory.web.components.toProfileInfo
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.models.InventoryAppRoleUi
import org.product.inventory.web.models.toInventoryAppRoleUi
import org.product.inventory.web.pages.Routes
import user.model.InventoryAppRole
import user.model.InventoryAppUser

class CrewManagementUiMapper(
    private val dictionary: Dictionary,
    private val isEmailValid: IsEmailValid,
) {

    private data class StateStaticData(
        val breadcrumbItems: List<BreadcrumbItem>,
        val title: String,
        val registeredUsersLabel: String,
        val addButtonText: String,
    )

    private data class DetailsStaticData(
        val emailLabel: String,
        val roleLabel: String,
        val hasPermanentGarageAccessLabel: String,
        val saveButtonText: String,
        val availableRoles: List<InventoryAppRoleUi>,
    )

    private val stateStaticData by lazy {
        StateStaticData(
            breadcrumbItems = buildBreadcrumbItems(),
            title = dictionary.get(StringRes.crewManagementTitle),
            registeredUsersLabel = dictionary.get(StringRes.crewManagementRegisteredUsersLabel),
            addButtonText = dictionary.get(StringRes.crewManagementAddButtonText),
        )
    }

    fun toUiState(
        isLoading: Boolean,
        users: List<InventoryAppUser>,
        routeToNavigate: String?,
        alertMessage: AlertMessage?,
        closeDetailsDialog: Boolean,
    ) = CrewManagementUiState(
        breadcrumbItems = stateStaticData.breadcrumbItems,
        title = stateStaticData.title,
        registeredUsersLabel = stateStaticData.registeredUsersLabel,
        isLoading = isLoading,
        routeToNavigate = routeToNavigate,
        alertMessage = alertMessage,
        users = users.map(::buildCrewManagementUserUiItem),
        addButtonText = stateStaticData.addButtonText,
        closeDetailsDialog = closeDetailsDialog,
    )

    private fun buildCrewManagementUserUiItem(user: InventoryAppUser) = CrewManagementUserUiItem(
        id = user.id,
        email = user.email,
        profileInfo = user.toProfileInfo(dictionary),
        roleUi = user.role.toInventoryAppRoleUi(dictionary),
        hasPermanentGarageAccess = user.hasPermanentGarageAccess,
    )

    private fun buildAvailableRoles() = listOf(
        InventoryAppRole.User,
        InventoryAppRole.Manager,
        InventoryAppRole.Administrator,
    ).map { it.toInventoryAppRoleUi(dictionary) }

    private fun buildBreadcrumbItems() = listOf(
        BreadcrumbItem(
            text = dictionary.get(StringRes.crewManagementPath1),
            route = Routes.home,
        ),
        BreadcrumbItem(
            text = dictionary.get(StringRes.crewManagementPath2),
            route = Routes.parkingReservation,
        ),
    )

    private val detailsStaticData by lazy {
        DetailsStaticData(
            emailLabel = dictionary.get(StringRes.crewManagementEmailLabel),
            roleLabel = dictionary.get(StringRes.crewManagementRoleLabel),
            hasPermanentGarageAccessLabel = dictionary.get(StringRes.crewManagementHasPermanentGarageAccessLabel),
            saveButtonText = dictionary.get(StringRes.crewManagementSaveButtonText),
            availableRoles = buildAvailableRoles(),
        )
    }

    fun toDetailsUiState(
        userId: String?,
        profileIcon: String?,
        tempEmail: String,
        tempRole: InventoryAppRoleUi,
        tempHasPermanentGarageAccess: Boolean,
        detailsUpdating: Boolean,
    ): CrewManagementUserDetailsState {
        val emailValid = isEmailValid(tempEmail)

        return CrewManagementUserDetailsState(
            userId = userId,
            profileIcon = profileIcon,
            email = tempEmail,
            role = tempRole,
            hasPermanentGarageAccess = tempHasPermanentGarageAccess,
            availableRoles = detailsStaticData.availableRoles,
            isUpdating = detailsUpdating,
            saveButtonText = detailsStaticData.saveButtonText,
            emailLabel = detailsStaticData.emailLabel,
            roleLabel = detailsStaticData.roleLabel,
            hasPermanentGarageAccessLabel = detailsStaticData.hasPermanentGarageAccessLabel,
            saveEnabled = emailValid,
            emailErrorMessage = if (emailValid) null else dictionary.get(StringRes.crewManagementInvalidEmailErrorMessage),
            emailEditable = userId == null,
        )
    }
}
