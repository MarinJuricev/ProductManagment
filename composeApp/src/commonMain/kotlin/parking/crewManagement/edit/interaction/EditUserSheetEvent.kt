package parking.crewManagement.edit.interaction

import user.model.InventoryAppRole
import user.model.InventoryAppUser

sealed interface EditUserSheetEvent {

    data class SheetShown(val user: InventoryAppUser?) : EditUserSheetEvent
    data class RoleChanged(
        val newRole: InventoryAppRole,
    ) : EditUserSheetEvent

    data class GarageAccessChanged(val hasPermanentAccess: Boolean) : EditUserSheetEvent

    data class EmailChanged(val email: String) : EditUserSheetEvent
    data class SaveClick(
        val email: String,
        val role: InventoryAppRole,
        val hasPermanentAccess: Boolean,
    ) : EditUserSheetEvent
}
