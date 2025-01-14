package parking.crewManagement.edit.interaction

sealed interface EditUserSheetViewEffect {
    data class ShowMessage(val message: String) : EditUserSheetViewEffect
    data object CloseDetails : EditUserSheetViewEffect
}
