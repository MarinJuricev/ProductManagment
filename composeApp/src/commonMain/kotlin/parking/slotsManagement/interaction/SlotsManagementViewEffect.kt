package parking.slotsManagement.interaction

sealed interface SlotsManagementViewEffect {
    data class ShowMessage(val message: String) : SlotsManagementViewEffect
}
