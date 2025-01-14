package parking.crewManagement.edit.mapper

import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary

class EditUserUiTextsMapper(
    val dictionary: Dictionary,
) {
    fun map() = EditUserUiTexts(
        emailFieldIdentifier = dictionary.getString(MR.strings.crew_management_form_email_title),
        rolePickerIdentifier = dictionary.getString(MR.strings.crew_management_form_role),
        garageAccessIdentifier = dictionary.getString(MR.strings.crew_management_form_garage_access),
        saveButtonText = dictionary.getString(MR.strings.parking_reservation_user_request_save),
        editUserTitle = dictionary.getString(MR.strings.crew_management_edit_user_sheet_title),
        createUserTitle = dictionary.getString(MR.strings.crew_management_create_user_sheet_title),
    )
}

data class EditUserUiTexts(
    val emailFieldIdentifier: String,
    val rolePickerIdentifier: String,
    val garageAccessIdentifier: String,
    val saveButtonText: String,
    val editUserTitle: String,
    val createUserTitle: String,
)
