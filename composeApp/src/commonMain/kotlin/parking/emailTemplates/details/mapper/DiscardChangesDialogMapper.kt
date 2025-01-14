package parking.emailTemplates.details.mapper

import components.QuestionDialogData
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary

class DiscardChangesDialogMapper(
    val dictionary: Dictionary,
) {
    fun map() = QuestionDialogData(
        isVisible = true,
        title = dictionary.getString(MR.strings.general_discard_changes_title),
        question = dictionary.getString(MR.strings.general_discard_changes_message),
        negativeActionText = dictionary.getString(MR.strings.general_discard),
        positiveActionText = dictionary.getString(MR.strings.general_stay),
    )
}
