package parking.crewManagement.edit.mapper

import core.utils.IsEmailValid
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.crewManagement.edit.interaction.EditUserSheetState
import user.model.InventoryAppRole
import user.model.InventoryAppUser

class EditUserSheetUiMapper(
    private val dictionary: Dictionary,
    private val isEmailValid: IsEmailValid,
) {
    fun map(
        selectedUser: InventoryAppUser?,
        editableFields: EditUserEditableFields?,
        saveButtonLoading: Boolean?,
        uiTexts: EditUserUiTexts,
    ): EditUserSheetState {
        val displayedRole = editableFields?.role ?: selectedUser?.role ?: InventoryAppRole.User
        val displayedGarageAccess =
            editableFields?.permanentGarageAccess ?: selectedUser?.hasPermanentGarageAccess ?: false
        val emailError = editableFields?.email
            ?.takeIf { it.isNotEmpty() && !isEmailValid(it) }
            ?.let { dictionary.getString(MR.strings.general_email_validation_error) }
        val selectedEmail = selectedUser?.email ?: editableFields?.email.orEmpty()
        return EditUserSheetState.Content(
            userEmail = selectedEmail,
            userProfileImageUrl = selectedUser?.profileImageUrl.orEmpty(),
            availableRoles = availableRoles,
            selectedRole = displayedRole,
            permanentGarageAccess = displayedGarageAccess,
            saveButtonLoading = saveButtonLoading ?: false,
            emailFieldEnabled = selectedUser == null,
            emailFieldError = emailError,
            saveButtonVisible = emailError == null && selectedEmail.isNotBlank(),
            sheetTitle = if (selectedUser != null) uiTexts.editUserTitle else uiTexts.createUserTitle,
            emailFieldIdentifier = uiTexts.emailFieldIdentifier,
            rolePickerIdentifier = uiTexts.rolePickerIdentifier,
            garageAccessIdentifier = uiTexts.garageAccessIdentifier,
            saveButtonText = uiTexts.saveButtonText,
        )
    }
}

private val availableRoles = listOf(
    InventoryAppRole.User,
    InventoryAppRole.Manager,
    InventoryAppRole.Administrator,
)

data class EditUserEditableFields(
    val role: InventoryAppRole? = null,
    val permanentGarageAccess: Boolean? = null,
    val email: String? = null,
)
