package parking.slotsManagement.interaction

import parking.reservation.model.GarageLevelData

sealed interface SlotsManagementEvent {
    data object FabClick : SlotsManagementEvent
    data object LevelCreatorDismissRequest : SlotsManagementEvent
    data class EditLevelClick(val garageLevel: GarageLevelData) : SlotsManagementEvent
    data class DeleteLevelClick(val garageLevel: GarageLevelData) : SlotsManagementEvent
    data object DeleteLevelConfirmed : SlotsManagementEvent
    data object DeleteLevelCanceled : SlotsManagementEvent
}
