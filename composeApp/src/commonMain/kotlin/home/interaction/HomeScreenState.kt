package home.interaction

import cafe.adriel.voyager.navigator.tab.Tab
import components.QuestionDialogData
import user.model.InventoryAppUser

sealed interface HomeScreenState {
    data class Content(
        val user: InventoryAppUser,
        val sideMenuOptions: List<Tab>,
        val dialogData: QuestionDialogData,
    ) : HomeScreenState

    data object Loading : HomeScreenState
    data object Retry : HomeScreenState
}
