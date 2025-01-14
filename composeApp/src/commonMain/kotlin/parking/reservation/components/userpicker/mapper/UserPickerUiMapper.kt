package parking.reservation.components.userpicker.mapper

import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.reservation.components.userpicker.interaction.UserPickerState
import user.model.InventoryAppUser

class UserPickerUiMapper(
    private val dictionary: Dictionary,
) {

    fun map(
        users: List<InventoryAppUser>,
        preselectedUser: InventoryAppUser,
        searchValue: String,
    ) = UserPickerState.Content(
        users = users.filter { it.email.contains(other = searchValue, ignoreCase = true) },
        searchValue = searchValue,
        searchPlaceholderText = dictionary.getString(MR.strings.parking_reservation_user_picker_search_placeholder),
        preselectedUser = preselectedUser,
    )
}
