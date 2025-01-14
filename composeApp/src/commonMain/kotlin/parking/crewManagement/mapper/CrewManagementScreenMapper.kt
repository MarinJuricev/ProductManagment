package parking.crewManagement.mapper

import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.crewManagement.interaction.CrewManagementScreenState.Content
import user.model.InventoryAppUser

class CrewManagementScreenMapper(
    private val dictionary: Dictionary,
) {
    fun map(users: List<InventoryAppUser>): Content = Content(
        users = users,
        formTitle = dictionary.getString(MR.strings.crew_management_form_title),
    )
}
