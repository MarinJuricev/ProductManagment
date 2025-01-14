package org.product.inventory.web.pages.seatreservationtmanagement

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.opacity
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.forms.TextInput
import com.varabyte.kobweb.silk.components.graphics.Image
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.product.inventory.shared.MR
import org.product.inventory.web.components.CircularProgressBar
import org.product.inventory.web.components.ConfirmationDialog
import org.product.inventory.web.components.ConfirmationDialogState
import org.product.inventory.web.components.Dialog
import org.product.inventory.web.components.ErrorText
import org.product.inventory.web.components.LoadingButton
import org.product.inventory.web.components.PopUpMenu
import org.product.inventory.web.components.Text
import org.product.inventory.web.components.TitleWithPath
import org.product.inventory.web.components.clickable
import org.product.inventory.web.components.closeDialog
import org.product.inventory.web.components.hideModalOnClick
import org.product.inventory.web.components.provideCustomColorScheme
import org.product.inventory.web.components.roundedCornersShape
import org.product.inventory.web.components.showModalOnClick
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.toCssColor

@Composable
fun SeatReservationManagement(
    state: SeatReservationManagementUiState,
    detailsData: OfficeDetailsData?,
    deleteData: ConfirmationDialogState?,
    onEvent: (SeatReservationManagementEvent) -> Unit,
) {
    if (state.closeDeleteDialog) {
        closeDialog(DELETE_OFFICE_DIALOG_ID)
    }

    if (state.closeEditDialog) {
        closeDialog(EDIT_OFFICE_DIALOG_ID)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            TitleWithPath(
                items = state.breadcrumbItems,
                title = state.title,
                onPathClick = { onEvent(SeatReservationManagementEvent.PathClick(it)) },
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressBar()
                }
            } else {
                InputBox(
                    state = state,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onEvent = onEvent,
                )
            }
        }
    }

    EditDialog(
        detailsData = detailsData,
        onEvent = onEvent,
    )

    ConfirmationDialog(
        id = DELETE_OFFICE_DIALOG_ID,
        dialogState = deleteData,
        closableOutside = false,
        isLoading = state.deleteInProgress,
        onClose = { onEvent(SeatReservationManagementEvent.DeleteDialogClosed) },
        onPositiveClick = { onEvent(SeatReservationManagementEvent.DeleteOfficeConfirmClick) },
        onNegativeClick = { onEvent(SeatReservationManagementEvent.DeleteOfficeCancelClick) },
    )
}

@Composable
private fun InputBox(
    state: SeatReservationManagementUiState,
    modifier: Modifier = Modifier,
    onEvent: (SeatReservationManagementEvent) -> Unit,
) {
    Column(
        modifier = modifier
            .margin(top = 100.px)
            .fillMaxWidth(percent = 50.percent)
            .padding(50.px)
            .margin(bottom = 20.px)
            .background(color = Colors.White)
            .roundedCornersShape(23.px),
    ) {
        HeaderLabels(
            officeLabel = state.officeLabel,
            numberOfSeatsLabel = state.numberOfSeatsLabel,
            modifier = Modifier.fillMaxWidth().margin(bottom = 20.px),
        )

        state.offices.forEach { office ->
            OfficeItem(
                name = office.name,
                numberOfSeats = office.numberOfSeats,
                modifier = Modifier.fillMaxWidth().margin(bottom = 20.px),
                iconContent = {
                    PopUpMenu(
                        items = state.popupOptions,
                        itemContent = { _, popupItem, modifier ->
                            OfficePopupItem(
                                popupItem = popupItem,
                                officeUI = office,
                                modifier = modifier,
                                onEvent = onEvent,
                            )
                        },
                    )
                },
            )
        }

        OfficeItem(
            name = state.newOfficeName,
            numberOfSeats = state.newOfficeNumberOfSeats,
            modifier = Modifier.fillMaxWidth(),
            errorText = state.newOfficeNameErrorText,
            enabled = !state.submitActive,
            placeholder = state.newOfficeNamePlaceholder,
            iconContent = {
                val enabled = state.validOfficeData && !state.submitActive
                Image(
                    src = ImageRes.seatReservationAdd,
                    modifier = Modifier
                        .width(22.px)
                        .height(26.px)
                        .opacity(if (enabled) 1f else 0.3f)
                        .thenIf(
                            condition = enabled,
                            other = Modifier.clickable {
                                onEvent(
                                    SeatReservationManagementEvent.AddOfficeClick(
                                        name = state.newOfficeName,
                                        numberOfSeats = state.newOfficeNumberOfSeats,
                                    ),
                                )
                            },
                        ),
                )
            },
            onNameChanged = { onEvent(SeatReservationManagementEvent.NewOfficeNameChange(it)) },
            onNumberOfSeatsChanged = { onEvent(SeatReservationManagementEvent.NewOfficeNumberOfSeatsChange(it)) },
        )
    }
}

@Composable
private fun HeaderLabels(
    officeLabel: String,
    numberOfSeatsLabel: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(percent = 60.percent),
        ) {
            LabelText(officeLabel)
        }

        Box(
            modifier = Modifier.fillMaxWidth(percent = 20.percent),
        ) {
            LabelText(numberOfSeatsLabel)
        }

        Box(
            modifier = Modifier.fillMaxWidth(percent = 20.percent),
        )
    }
}

@Composable
private fun OfficeItem(
    name: String,
    numberOfSeats: String,
    placeholder: String = "",
    enabled: Boolean = false,
    errorText: String? = null,
    modifier: Modifier = Modifier,
    iconContent: @Composable (BoxScope.() -> Unit)? = null,
    onNameChanged: (String) -> Unit = {},
    onNumberOfSeatsChanged: (String) -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(percent = if (iconContent != null) 60.percent else 80.percent),
            ) {
                OfficeTextInput(
                    text = name,
                    enabled = enabled,
                    placeholder = placeholder,
                    onTextChanged = onNameChanged,
                    modifier = Modifier.fillMaxWidth(80.percent),
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth(percent = 20.percent),
            ) {
                OfficeTextInput(
                    text = numberOfSeats,
                    enabled = enabled,
                    placeholder = "-",
                    onTextChanged = onNumberOfSeatsChanged,
                    modifier = Modifier.fillMaxWidth(70.percent).textAlign(TextAlign.Center),
                )
            }

            iconContent?.let { icon ->
                Box(
                    modifier = Modifier.fillMaxWidth(percent = 20.percent),
                    contentAlignment = Alignment.Center,
                ) {
                    icon.invoke(this)
                }
            }
        }

        errorText?.let { error ->
            ErrorText(
                value = error,
                modifier = Modifier.fillMaxWidth().margin(top = 4.px),
            )
        }
    }
}

@Composable
private fun OfficeTextInput(
    text: String,
    placeholder: String = "",
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onTextChanged: (String) -> Unit,
) {
    TextInput(
        modifier = modifier,
        enabled = enabled,
        text = text,
        placeholder = placeholder,
        onTextChanged = onTextChanged,
    )
}

@Composable
private fun OfficePopupItem(
    popupItem: OfficePopupOptionUi,
    officeUI: OfficeUI,
    modifier: Modifier = Modifier,
    onEvent: (SeatReservationManagementEvent) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .showModalOnClick(
                when (popupItem.option) {
                    OfficePopupOption.EDIT -> EDIT_OFFICE_DIALOG_ID
                    OfficePopupOption.DELETE -> DELETE_OFFICE_DIALOG_ID
                },
            )
            .clickable {
                when (popupItem.option) {
                    OfficePopupOption.EDIT -> onEvent(SeatReservationManagementEvent.EditOfficeClick(officeUI))
                    OfficePopupOption.DELETE -> onEvent(SeatReservationManagementEvent.DeleteOfficeClick(officeUI))
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Image(
            src = popupItem.icon,
            modifier = Modifier.size(16.px),
        )

        Text(
            value = popupItem.text,
            modifier = Modifier
                .margin(left = 10.px)
                .color(MR.colors.textBlack.toCssColor())
                .styleModifier {
                    property("--bs-dropdown-link-active-color", MR.colors.textBlack.toCssColor())
                },
        )
    }
}

@Composable
private fun EditDialog(
    detailsData: OfficeDetailsData?,
    onEvent: (SeatReservationManagementEvent) -> Unit,
) {
    Dialog(
        id = EDIT_OFFICE_DIALOG_ID,
        onClose = { onEvent(SeatReservationManagementEvent.EditDialogClosed) },
        content = { contentModifier ->
            detailsData?.let { state ->
                Column(
                    modifier = contentModifier.fillMaxWidth().padding(20.px),
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().margin(bottom = 20.px),
                    ) {
                        Image(
                            modifier = Modifier
                                .hideModalOnClick()
                                .cursor(Cursor.Pointer)
                                .align(Alignment.CenterStart),
                            src = ImageRes.backIcon,
                        )

                        Text(
                            value = state.detailsTitle,
                            modifier = Modifier
                                .fontSize(20.px)
                                .fontWeight(FontWeight.Bold)
                                .color(MR.colors.textBlack.toCssColor())
                                .align(Alignment.Center),
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth().margin(bottom = 20.px),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(percent = 80.percent).margin(right = 40.px),
                            ) {
                                LabelText(state.nameLabel, modifier = Modifier.margin(bottom = 10.px))

                                OfficeTextInput(
                                    text = state.name,
                                    placeholder = state.namePlaceholder,
                                    onTextChanged = { onEvent(SeatReservationManagementEvent.EditOfficeNameChange(it)) },
                                    modifier = Modifier.fillMaxWidth(80.percent),
                                )
                            }

                            Column(
                                modifier = Modifier.fillMaxWidth(20.percent),
                            ) {
                                LabelText(state.numberOfSeatsLabel, modifier = Modifier.margin(bottom = 10.px))

                                OfficeTextInput(
                                    text = state.numberOfSeats,
                                    placeholder = state.numberOfSeatsPlaceholder,
                                    onTextChanged = { onEvent(SeatReservationManagementEvent.EditOfficeNumberOfSeatsChange(it)) },
                                    modifier = Modifier.fillMaxWidth().textAlign(TextAlign.Center),
                                )
                            }
                        }

                        state.nameErrorText?.let { error ->
                            ErrorText(
                                value = error,
                                modifier = Modifier.fillMaxWidth().margin(top = 4.px),
                            )
                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        LoadingButton(
                            colorScheme = provideCustomColorScheme(MR.colors.secondary.toCssColor()),
                            modifier = Modifier.margin(top = 20.px).padding(leftRight = 80.px),
                            isLoading = state.submitActive,
                            enabled = state.submitEnabled && !state.submitActive,
                            onClick = { onEvent(SeatReservationManagementEvent.EditOfficeConfirmClick(state)) },
                            content = {
                                Text(
                                    value = state.buttonText,
                                    modifier = Modifier.color(Colors.White),
                                )
                            },
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun LabelText(
    text: String,
    textSize: CSSLengthOrPercentageNumericValue = 16.px,
    textColor: Color.Rgb = MR.colors.secondary.toCssColor(),
    modifier: Modifier = Modifier,
) {
    Text(
        value = text,
        modifier = modifier.color(textColor).fontWeight(FontWeight.SemiBold),
        size = textSize,
    )
}

private const val DELETE_OFFICE_DIALOG_ID = "deleteOfficeDialog"
private const val EDIT_OFFICE_DIALOG_ID = "editOfficeDialog"
