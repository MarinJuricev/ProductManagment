package parking.reservation.interaction

import user.model.InventoryAppUser

sealed interface CreateParkingReservationViewEffect {
    data object NavigateToDashboard : CreateParkingReservationViewEffect
    data class ShowMessage(val message: String) : CreateParkingReservationViewEffect
    data class OpenUserPicker(
        val preselectedUser: InventoryAppUser,
    ) : CreateParkingReservationViewEffect
}
