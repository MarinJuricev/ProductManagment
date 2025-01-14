package org.product.inventory.web.core

import org.product.inventory.shared.MR
import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.toCssColor

class AlertMessageMapper(
    private val dictionary: Dictionary,
) {

    fun map(
        isSuccess: Boolean,
        successMessage: String? = null,
        failureMessage: String? = null,
    ) = AlertMessage(
        message = if (isSuccess) {
            successMessage ?: dictionary.get(StringRes.alertMessageSuccess)
        } else {
            failureMessage ?: dictionary.get(StringRes.alertMessageFailure)
        },
        isSuccess = isSuccess,
        backgroundColor = if (isSuccess) {
            MR.colors.alertMessageSuccessBackground.toCssColor()
        } else {
            MR.colors.alertMessageErrorBackground.toCssColor()
        },
        contentColor = if (isSuccess) {
            MR.colors.alertMessageSuccessContent.toCssColor()
        } else {
            MR.colors.alertMessageContentError.toCssColor()
        },
    )
}
