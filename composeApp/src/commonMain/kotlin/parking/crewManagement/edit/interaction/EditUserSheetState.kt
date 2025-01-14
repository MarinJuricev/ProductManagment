package parking.crewManagement.edit.interaction

import user.model.InventoryAppRole

sealed interface EditUserSheetState {

    data object Error : EditUserSheetState
    data object Loading : EditUserSheetState
    data class Content(
        val userEmail: String,
        val userProfileImageUrl: String,
        val availableRoles: List<InventoryAppRole>,
        val selectedRole: InventoryAppRole,
        val permanentGarageAccess: Boolean,
        val sheetTitle: String,
        val emailFieldEnabled: Boolean,
        val emailFieldError: String?,
        val emailFieldIdentifier: String,
        val rolePickerIdentifier: String,
        val garageAccessIdentifier: String,
        val saveButtonVisible: Boolean,
        val saveButtonText: String,
        val saveButtonLoading: Boolean,
    ) : EditUserSheetState
}
