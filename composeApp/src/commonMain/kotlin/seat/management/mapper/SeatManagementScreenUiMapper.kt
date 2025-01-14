package seat.management.mapper

import components.ImageType
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import seat.management.components.OfficeItemOption
import seat.management.interaction.SeatManagementScreenState.Content
import seat.management.interaction.SeatManagementScreenTexts
import seat.management.model.SeatManagementDialogs
import seatreservation.model.Office

class SeatManagementScreenUiMapper(
    val dictionary: Dictionary,
) {
    fun map(
        uiTexts: SeatManagementScreenTexts,
        offices: List<Office>,
        officeName: String,
        seats: Int?,
        dialogs: SeatManagementDialogs,
    ) = Content(
        screenTexts = uiTexts,
        offices = offices,
        officeName = officeName,
        seatsNumber = seats?.toString() ?: "",
        addButtonEnabled = officeName.isNotBlank() && seats != null,
        deleteDialog = dialogs.deleteDialogData,
        editDialog = dialogs.editOfficeDialogData,
        availableOptions = listOf(
            OfficeItemOption.Edit(
                displayedText = dictionary.getString(MR.strings.slots_management_level_option_edit),
                iconResource = ImageType.Resource(MR.images.edit_icon),
            ),
            OfficeItemOption.Delete(
                displayedText = dictionary.getString(MR.strings.slots_management_level_option_delete),
                iconResource = ImageType.Resource(MR.images.delete_icon),
            ),
        ),
        itemMenuIconResource = ImageType.Resource(MR.images.dots),
        officeNamePlaceHolder = dictionary.getString(MR.strings.seat_reservation_office_name_placeholder),
        seatsNumberPlaceholder = dictionary.getString(MR.strings.seat_reservation_seats_placeholder),
    )
}
