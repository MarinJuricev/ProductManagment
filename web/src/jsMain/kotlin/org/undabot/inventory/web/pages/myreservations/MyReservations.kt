package org.product.inventory.web.pages.myreservations

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.TextOverflow
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.css.overflow
import com.varabyte.kobweb.compose.css.textOverflow
import com.varabyte.kobweb.compose.css.whiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.product.inventory.shared.MR
import org.product.inventory.web.components.DateInput
import org.product.inventory.web.components.DateInputValue
import org.product.inventory.web.components.Dialog
import org.product.inventory.web.components.ItemColumnContainer
import org.product.inventory.web.components.ModalSize
import org.product.inventory.web.components.ParkingOptionsDropdown
import org.product.inventory.web.components.ProfileInfo
import org.product.inventory.web.components.ReservationsAndRequestsLabelText
import org.product.inventory.web.components.ReservationsAndRequestsLabelTextWithOverflow
import org.product.inventory.web.components.ReservationsAndRequestsLayout
import org.product.inventory.web.components.StatusButton
import org.product.inventory.web.components.Text
import org.product.inventory.web.components.TextArea
import org.product.inventory.web.components.TextWithOverflow
import org.product.inventory.web.components.clickable
import org.product.inventory.web.components.dialogParkingOptionsStyle
import org.product.inventory.web.components.hideModalOnClick
import org.product.inventory.web.components.pointerCursor
import org.product.inventory.web.components.reservationItemLargeScreenStyle
import org.product.inventory.web.components.reservationItemSmallScreenStyle
import org.product.inventory.web.components.reservationItemTextStyle
import org.product.inventory.web.components.roundedCornersShape
import org.product.inventory.web.components.showModalOnClick
import org.product.inventory.web.components.spaceBetween
import org.product.inventory.web.components.statusButtonStyle
import org.product.inventory.web.components.toColorScheme
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.core.fillWithSpaceIfEmpty
import org.product.inventory.web.toCssColor

val myReservationsInfoStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.display(DisplayStyle.InlineBlock)
    }

    Breakpoint.SM {
        Modifier.display(DisplayStyle.InlineBlock)
    }

    Breakpoint.MD {
        Modifier.display(DisplayStyle.InlineBlock)
    }

    Breakpoint.LG {
        Modifier.display(DisplayStyle.Flex)
    }
}

val myReservationsDateInputStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.width(50.percent)
    }

    Breakpoint.LG {
        Modifier.width(100.percent)
    }
}

val myReservationsDateLabelStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.margin(top = 20.px)
    }

    Breakpoint.LG {
        Modifier.margin(top = 0.px)
    }
}

val myReservationsDateBoxInputStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.width(30.percent)
    }

    Breakpoint.MD {
        Modifier.width(20.percent)
    }
}

val myReservationsDateBoxStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier
            .styleModifier { property("border-radius", 12.px) }
            .padding(15.px)
    }

    Breakpoint.SM {
        Modifier
            .styleModifier { property("border-radius", 16.px) }
            .padding(30.px)
    }

    Breakpoint.MD {
        Modifier
            .styleModifier { property("border-radius", 18.px) }
            .padding(30.px)
    }

    Breakpoint.LG {
        Modifier
            .styleModifier { property("border-radius", 23.px) }
            .padding(30.px)
    }
}

@Composable
fun MyReservations(
    state: MyReservationsState,
    selectedReservation: ParkingReservationItemUi?,
    onEvent: (MyReservationsEvent) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ReservationsAndRequestsLayout(
            breadcrumbItems = state.breadcrumbItems,
            title = state.title,
            emptyListMessage = state.emptyReservationsMessage,
            data = state.reservations,
            itemContent = { index, item ->
                ReservationItem(
                    reservationItem = item,
                    onEvent = onEvent,
                )

                if (index != state.reservations.lastIndex) {
                    Box(modifier = Modifier.fillMaxWidth().height(25.px))
                }
            },
            header = {
                Header(
                    modifier = Modifier.padding(30.px).fillMaxWidth(),
                    fromDate = state.fromDate,
                    minToDate = state.fromDate,
                    toDate = state.toDate,
                    fromLabel = state.fromDateLabel,
                    toLabel = state.toDateLabel,
                    onFromDateChange = { onEvent(MyReservationsEvent.DateRangeChanged(fromDate = it)) },
                    onToDateChange = { onEvent(MyReservationsEvent.DateRangeChanged(toDate = it)) },
                )
            },
            isLoading = state.isLoading,
            onPathClick = { onEvent(MyReservationsEvent.PathClick(it)) },
        )

        RequestDetailsDialog(
            selectedReservation = selectedReservation,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun Header(
    fromDate: DateInputValue,
    minToDate: DateInputValue,
    toDate: DateInputValue,
    fromLabel: String,
    toLabel: String,
    modifier: Modifier = Modifier,
    onFromDateChange: (DateInputValue) -> Unit,
    onToDateChange: (DateInputValue) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Colors.White)
                .then(myReservationsDateBoxStyle.toModifier()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ReservationsAndRequestsLabelText(
                value = fromLabel,
                fontSize = null,
                modifier = reservationItemTextStyle
                    .toModifier()
                    .margin(right = 10.px),
            )
            DateInput(
                modifier = reservationItemTextStyle
                    .toModifier()
                    .then(myReservationsDateBoxInputStyle.toModifier())
                    .margin(right = 20.px)
                    .padding(5.px),
                value = fromDate,
                onValueChange = onFromDateChange,
            )

            ReservationsAndRequestsLabelText(
                value = toLabel,
                fontSize = null,
                modifier = reservationItemTextStyle
                    .toModifier()
                    .margin(right = 10.px),
            )
            DateInput(
                modifier = reservationItemTextStyle
                    .toModifier()
                    .then(myReservationsDateBoxInputStyle.toModifier())
                    .padding(5.px),
                value = toDate,
                minDate = minToDate,
                onValueChange = onToDateChange,
            )
        }
    }
}

@Composable
private fun ReservationItem(
    reservationItem: ParkingReservationItemUi,
    onEvent: (MyReservationsEvent) -> Unit,
) {
    ReservationItemLargeScreen(
        reservationItem = reservationItem,
        onEvent = onEvent,
    )

    ReservationItemSmallScreen(
        reservationItem = reservationItem,
        onEvent = onEvent,
    )
}

@Composable
private fun ReservationItemLargeScreen(
    reservationItem: ParkingReservationItemUi,
    modifier: Modifier = Modifier,
    onEvent: (MyReservationsEvent) -> Unit,
) {
    Row(
        modifier = modifier.then(reservationItemLargeScreenStyle.toModifier())
            .showModalOnClick(RESERVATION_ITEM_DIALOG_ID)
            .fillMaxWidth()
            .background(color = Colors.White)
            .roundedCornersShape(23.px)
            .padding(leftRight = 30.px, topBottom = 10.px)
            .pointerCursor()
            .clickable { onEvent(MyReservationsEvent.ItemClick(reservationItem.itemId)) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ItemColumnContainer {
            ReservationsAndRequestsLabelTextWithOverflow(reservationItem.submittedDateLabel)
            TextWithOverflow(reservationItem.submittedDate.fillWithSpaceIfEmpty())
        }

        ItemColumnContainer {
            ReservationsAndRequestsLabelTextWithOverflow(reservationItem.requestDateLabel)
            TextWithOverflow(reservationItem.requestedDate.fillWithSpaceIfEmpty())
        }

        ItemColumnContainer {
            ReservationsAndRequestsLabelTextWithOverflow(reservationItem.noteLabel)
            TextWithOverflow(reservationItem.note.fillWithSpaceIfEmpty())
        }

        ItemColumnContainer(alignment = Alignment.Center) {
            StatusButton(
                itemStatus = reservationItem.status,
                modifier = statusButtonStyle.toModifier().styleModifier {
                    whiteSpace(WhiteSpace.NoWrap)
                    overflow(Overflow.Visible)
                    textOverflow(TextOverflow.Ellipsis)
                },
                onClick = { onEvent(MyReservationsEvent.ItemClick(reservationItem.itemId)) },
                content = {
                    TextWithOverflow(
                        value = reservationItem.status.text,
                        modifier = Modifier.color(Colors.White),
                        textOverflow = TextOverflow.Clip,
                    )
                },
            )
        }

        ItemColumnContainer(
            width = 8.percent,
            alignment = Alignment.CenterEnd,
        ) {
            Image(src = ImageRes.arrowRight)
        }
    }
}

@Composable
private fun ReservationItemSmallScreen(
    reservationItem: ParkingReservationItemUi,
    modifier: Modifier = Modifier,
    onEvent: (MyReservationsEvent) -> Unit,
) {
    Row(
        modifier = modifier.then(reservationItemSmallScreenStyle.toModifier())
            .showModalOnClick(RESERVATION_ITEM_DIALOG_ID)
            .fillMaxWidth()
            .background(color = Colors.White)
            .roundedCornersShape(16.px)
            .padding(leftRight = 16.px, topBottom = 12.px)
            .pointerCursor()
            .clickable { onEvent(MyReservationsEvent.ItemClick(reservationItem.itemId)) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Row(modifier = Modifier.padding(bottom = 4.px)) {
                Text(
                    value = reservationItem.submittedDateLabel,
                    modifier = reservationItemTextStyle.toModifier()
                        .color(MR.colors.secondary.toCssColor())
                        .fontWeight(FontWeight.SemiBold)
                        .styleModifier {
                            whiteSpace(WhiteSpace.NoWrap)
                        },
                )
                Text(
                    modifier = reservationItemTextStyle.toModifier()
                        .padding(left = 8.px)
                        .styleModifier {
                            width(50.percent)
                        },
                    value = reservationItem.submittedDate.fillWithSpaceIfEmpty(),
                )
            }

            Row {
                Text(
                    value = reservationItem.requestDateLabel,
                    modifier = reservationItemTextStyle.toModifier()
                        .color(MR.colors.secondary.toCssColor())
                        .fontWeight(FontWeight.SemiBold)
                        .styleModifier {
                            whiteSpace(WhiteSpace.NoWrap)
                        },
                )
                Text(
                    modifier = reservationItemTextStyle.toModifier()
                        .padding(left = 8.px)
                        .styleModifier {
                            width(50.percent)
                        },
                    value = reservationItem.requestedDate.fillWithSpaceIfEmpty(),
                )
            }
        }

        StatusButton(
            itemStatus = reservationItem.status,
            modifier = statusButtonStyle.toModifier().styleModifier {
                whiteSpace(WhiteSpace.NoWrap)
                overflow(Overflow.Visible)
                textOverflow(TextOverflow.Ellipsis)
            },
            onClick = { onEvent(MyReservationsEvent.ItemClick(reservationItem.itemId)) },
            content = {
                TextWithOverflow(
                    value = reservationItem.status.text,
                    modifier = reservationItemTextStyle.toModifier().color(Colors.White),
                )
            },
        )
    }
}

@Composable
private fun RequestDetailsDialog(
    selectedReservation: ParkingReservationItemUi?,
    size: ModalSize = ModalSize.Large,
    onEvent: (MyReservationsEvent) -> Unit,
) {
    Dialog(
        id = RESERVATION_ITEM_DIALOG_ID,
        size = size,
        clearContentOnClose = false, // this dialog contains TextArea but it is disabled so we don't need to clear the content
        onClose = { onEvent(MyReservationsEvent.DetailsClosed) },
        content = { contentModifier ->
            selectedReservation?.let { reservationItem ->
                Column(
                    modifier = contentModifier.fillMaxSize().padding(30.px),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().spaceBetween(),
                    ) {
                        Image(
                            modifier = Modifier
                                .hideModalOnClick()
                                .clickable { onEvent(MyReservationsEvent.CloseDialogClick) },
                            src = ImageRes.backIcon,
                        )
                        StatusButton(
                            itemStatus = reservationItem.status,
                            modifier = Modifier.cursor(Cursor.Default),
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .margin(top = 20.px)
                            .justifyContent(JustifyContent.SpaceBetween)
                            .then(myReservationsInfoStyle.toModifier()),
                    ) {
                        Column {
                            ReservationsAndRequestsLabelText(reservationItem.dateLabel)
                            DateInput(
                                modifier = myReservationsDateInputStyle.toModifier().margin(top = 20.px),
                                value = reservationItem.requestedDateDateInput,
                                enabled = false,
                                onValueChange = {},
                            )
                        }

                        Column {
                            ReservationsAndRequestsLabelText(
                                value = reservationItem.requestByLabel,
                                modifier = myReservationsDateLabelStyle.toModifier(),
                            )
                            ProfileInfo(
                                profileInfo = reservationItem.profileInfo,
                                showEmail = true,
                                showRole = false,
                                modifier = Modifier.margin(top = 20.px),
                                imageSize = 40.px,
                            )
                        }
                    }

                    ReservationsAndRequestsLabelText(
                        value = reservationItem.additionalNotesLabel,
                        modifier = Modifier.margin(top = 30.px),
                    )
                    TextArea(
                        modifier = Modifier.margin(top = 20.px),
                        value = reservationItem.note,
                        enabled = false,
                    )

                    ReservationsAndRequestsLabelText(
                        value = reservationItem.approveOrRejectNoteLabel,
                        modifier = Modifier.margin(top = 30.px),
                    )
                    TextArea(
                        modifier = Modifier.margin(top = 20.px),
                        value = reservationItem.approveOrRejectNote,
                        enabled = false,
                    )

                    if (reservationItem.status is ParkingReservationStatusUi.Approved) {
                        Row(modifier = Modifier.margin(top = 20.px)) {
                            Box(modifier = dialogParkingOptionsStyle.toModifier()) {
                                ReservationsAndRequestsLabelText(
                                    value = reservationItem.garageLevelLabel,
                                    modifier = Modifier
                                        .margin(right = 20.px)
                                        .align(Alignment.CenterStart),
                                )

                                ParkingOptionsDropdown(
                                    entries = emptyList(),
                                    enabled = false,
                                    defaultItem = reservationItem.status.garageLevel,
                                    onItemSelect = { },
                                    modifier = Modifier.margin(right = 20.px),
                                )
                            }

                            Box(modifier = dialogParkingOptionsStyle.toModifier()) {
                                ReservationsAndRequestsLabelText(
                                    value = reservationItem.parkingSpotLabel,
                                    modifier = Modifier
                                        .margin(right = 20.px)
                                        .align(Alignment.CenterStart),
                                )

                                ParkingOptionsDropdown(
                                    entries = emptyList(),
                                    enabled = false,
                                    defaultItem = reservationItem.status.parkingSpot,
                                    onItemSelect = { },
                                )
                            }
                        }
                    }

                    if (
                        reservationItem.status !is ParkingReservationStatusUi.Canceled &&
                        reservationItem.isActiveReservation
                    ) {
                        when (reservationItem.cancellationStatus) {
                            CancellationStatus.Confirmation -> CancelConfirmationContent(
                                confirmMessage = reservationItem.confirmCancellationMessage,
                                positiveText = reservationItem.confirmCancellationPositiveText,
                                negativeText = reservationItem.confirmCancellationNegativeText,
                                onPositiveClick = { onEvent(MyReservationsEvent.CancelConfirmClick(reservationItem.itemId)) },
                                onNegativeClick = { onEvent(MyReservationsEvent.CancelCanceledClick) },
                            )
                            CancellationStatus.Initial -> CancelButton(
                                text = reservationItem.cancelRequestText,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .margin(top = 30.px)
                                    .padding(leftRight = 30.px, topBottom = 10.px),
                                onClick = { onEvent(MyReservationsEvent.CancelReservationClick) },
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun CancelButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        colorScheme = MR.colors.error.toCssColor().toColorScheme(),
        content = {
            Image(
                src = ImageRes.cancelRequestIcon,
                modifier = Modifier.margin(right = 10.px),
            )
            Text(
                value = text,
                modifier = Modifier.color(Colors.White),
            )
        },
        onClick = { onClick() },
    )
}

@Composable
private fun CancelConfirmationContent(
    confirmMessage: String,
    positiveText: String,
    negativeText: String,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 30.px),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            value = confirmMessage,
            modifier = Modifier.margin(bottom = 20.px).textAlign(TextAlign.Center),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                modifier = Modifier.margin(right = 20.px),
                colorScheme = MR.colors.secondary.toCssColor().toColorScheme(),
                content = {
                    Text(
                        value = negativeText,
                        modifier = Modifier.color(Colors.White),
                    )
                },
                onClick = { onNegativeClick() },
            )

            Button(
                modifier = Modifier.hideModalOnClick(),
                colorScheme = MR.colors.error.toCssColor().toColorScheme(),
                content = {
                    Text(
                        value = positiveText,
                        modifier = Modifier.color(Colors.White),
                    )
                },
                onClick = { onPositiveClick() },
            )
        }
    }
}

private const val RESERVATION_ITEM_DIALOG_ID = "reservationItemDialog"
