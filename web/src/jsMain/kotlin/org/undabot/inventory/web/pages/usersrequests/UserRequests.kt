package org.product.inventory.web.pages.usersrequests

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
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
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.TextInput
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.minWidth
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.product.inventory.shared.MR
import org.product.inventory.web.components.CircularProgressBar
import org.product.inventory.web.components.DateInput
import org.product.inventory.web.components.DateInputValue
import org.product.inventory.web.components.Dialog
import org.product.inventory.web.components.Dropdown
import org.product.inventory.web.components.InfoMessage
import org.product.inventory.web.components.ItemColumnContainer
import org.product.inventory.web.components.LoadingButton
import org.product.inventory.web.components.ModalSize
import org.product.inventory.web.components.ParkingSelection
import org.product.inventory.web.components.PermanentGarageAccessSelection
import org.product.inventory.web.components.ReservationsAndRequestsLabelText
import org.product.inventory.web.components.ReservationsAndRequestsLabelTextWithOverflow
import org.product.inventory.web.components.ReservationsAndRequestsLayout
import org.product.inventory.web.components.StatusButton
import org.product.inventory.web.components.Text
import org.product.inventory.web.components.TextArea
import org.product.inventory.web.components.TextWithOverflow
import org.product.inventory.web.components.clickable
import org.product.inventory.web.components.closeDialog
import org.product.inventory.web.components.defaultCursor
import org.product.inventory.web.components.hideModalOnClick
import org.product.inventory.web.components.provideCustomColorScheme
import org.product.inventory.web.components.reservationItemLargeScreenStyle
import org.product.inventory.web.components.reservationItemSmallScreenStyle
import org.product.inventory.web.components.reservationItemTextStyle
import org.product.inventory.web.components.roundedCornersShape
import org.product.inventory.web.components.showModalOnClick
import org.product.inventory.web.components.statusButtonStyle
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.core.fillWithSpaceIfEmpty
import org.product.inventory.web.models.GarageLevelAndParkingSpotsStatus
import org.product.inventory.web.pages.myreservations.ParkingReservationStatusUi
import org.product.inventory.web.toCssColor

val requestedDateLabelStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.styleModifier {
            minWidth(80.px)
        }
    }

    Breakpoint.SM {
        Modifier.styleModifier {
            minWidth(110.px)
        }
    }

    Breakpoint.MD {
        Modifier.styleModifier {
            minWidth(140.px)
        }
    }
}

val emailLabelStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.minWidth(30.px)
    }

    Breakpoint.SM {
        Modifier.minWidth(50.px)
    }

    Breakpoint.MD {
        Modifier.minWidth(60.px)
    }
}

val emailStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.width(100.px)
    }

    Breakpoint.SM {
        Modifier.width(200.px)
    }

    Breakpoint.MD {
        Modifier.width(360.px)
    }
}

val userRequestsDateBoxInputStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.width(30.percent)
    }

    Breakpoint.MD {
        Modifier.width(40.percent)
    }
}

val userRequestsDateBoxStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier
            .styleModifier { property("border-radius", 12.px) }
            .padding(topBottom = 10.px, leftRight = 25.px)
    }

    Breakpoint.SM {
        Modifier
            .styleModifier { property("border-radius", 16.px) }
            .padding(topBottom = 15.px, leftRight = 30.px)
    }

    Breakpoint.MD {
        Modifier
            .styleModifier { property("border-radius", 18.px) }
            .padding(topBottom = 15.px, leftRight = 30.px)
    }

    Breakpoint.LG {
        Modifier
            .styleModifier { property("border-radius", 23.px) }
            .padding(topBottom = 15.px, leftRight = 30.px)
    }
}

@Composable
fun UserRequests(
    state: UserRequestsState,
    detailsState: UserRequestDetailsState?,
    onEvent: (UserRequestsEvent) -> Unit,
) {
    if (state.closeDialog) {
        closeDialog(RESERVATION_ITEM_DIALOG_ID)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ReservationsAndRequestsLayout(
            breadcrumbItems = state.breadcrumbItems,
            title = state.title,
            emptyListMessage = state.emptyRequestsMessage,
            data = state.requests,
            itemContent = { index, item ->
                ReservationItem(
                    reservationItem = item,
                    onEvent = onEvent,
                )

                if (index != state.requests.lastIndex) {
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
                    userEmailFilter = state.userEmailFilter,
                    userEmailFilterLabel = state.userEmailFilterLabel,
                    userEmailFilterPlaceholder = state.userEmailFilterPlaceholder,
                    statusFilterEntries = state.statusFilterEntries,
                    statusFilter = state.statusFilter,
                    allOptionText = state.statusFilterAllOptionText,
                    statusFilterChange = { onEvent(UserRequestsEvent.StatusFilterChanged(it)) },
                    emailFilterChange = { onEvent(UserRequestsEvent.EmailFilterChanged(it)) },
                    onFromDateChange = { onEvent(UserRequestsEvent.DateRangeChanged(fromDate = it)) },
                    onToDateChange = { onEvent(UserRequestsEvent.DateRangeChanged(toDate = it)) },
                )
            },
            isLoading = state.isLoading,
            onPathClick = { onEvent(UserRequestsEvent.PathClick(it)) },
        )

        RequestDetailsDialog(
            detailsState = detailsState,
            allOptionText = state.statusFilterAllOptionText,
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
    userEmailFilter: String,
    userEmailFilterLabel: String,
    userEmailFilterPlaceholder: String,
    statusFilterEntries: List<ParkingReservationStatusUi?>,
    statusFilter: ParkingReservationStatusUi?,
    allOptionText: String,
    modifier: Modifier = Modifier,
    statusFilterChange: (ParkingReservationStatusUi?) -> Unit,
    emailFilterChange: (String) -> Unit,
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
                .then(userRequestsDateBoxStyle.toModifier())
                .flexWrap(FlexWrap.Wrap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1).margin(topBottom = 10.px),
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
                        .then(userRequestsDateBoxInputStyle.toModifier())
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
                        .then(userRequestsDateBoxInputStyle.toModifier())
                        .padding(5.px)
                        .margin(right = 30.px),
                    value = toDate,
                    minDate = minToDate,
                    onValueChange = onToDateChange,
                )
            }

            Filter(
                modifier = Modifier.weight(1).margin(topBottom = 10.px),
                userEmailFilter = userEmailFilter,
                userEmailFilterLabel = userEmailFilterLabel,
                userEmailFilterPlaceholder = userEmailFilterPlaceholder,
                statusFilterEntries = statusFilterEntries,
                statusFilter = statusFilter,
                allOptionText = allOptionText,
                statusFilterChange = statusFilterChange,
                emailFilterChange = emailFilterChange,
            )
        }
    }
}

@Composable
private fun Filter(
    userEmailFilter: String,
    userEmailFilterLabel: String,
    userEmailFilterPlaceholder: String,
    statusFilterEntries: List<ParkingReservationStatusUi?>,
    statusFilter: ParkingReservationStatusUi?,
    allOptionText: String,
    modifier: Modifier = Modifier,
    statusFilterChange: (ParkingReservationStatusUi?) -> Unit,
    emailFilterChange: (String) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ReservationsAndRequestsLabelText(
            value = userEmailFilterLabel,
            fontSize = null,
            modifier = reservationItemTextStyle
                .toModifier()
                .margin(right = 10.px),
        )

        TextInput(
            modifier = reservationItemTextStyle
                .toModifier()
                .then(userRequestsDateBoxInputStyle.toModifier()),
            text = userEmailFilter,
            placeholder = userEmailFilterPlaceholder,
            onTextChanged = emailFilterChange,
        )

        StatusDropdown(
            modifier = Modifier.margin(leftRight = 20.px),
            entries = statusFilterEntries,
            selected = statusFilter,
            onItemSelect = statusFilterChange,
            enabled = true,
            allOptionText = allOptionText,
        )
    }
}

@Composable
private fun ReservationItem(
    reservationItem: UserRequestItemUi,
    modifier: Modifier = Modifier,
    onEvent: (UserRequestsEvent) -> Unit,
) {
    ReservationItemSmallScreen(
        reservationItem = reservationItem,
        onEvent = onEvent,
        modifier = modifier,
    )
    ReservationItemLargeScreen(
        reservationItem = reservationItem,
        onEvent = onEvent,
        modifier = modifier,
    )
}

@Composable
private fun ReservationItemLargeScreen(
    reservationItem: UserRequestItemUi,
    onEvent: (UserRequestsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.then(reservationItemLargeScreenStyle.toModifier())
            .showModalOnClick(RESERVATION_ITEM_DIALOG_ID)
            .background(color = Colors.White)
            .roundedCornersShape(23.px)
            .padding(leftRight = 30.px, topBottom = 10.px)
            .clickable { onEvent(UserRequestsEvent.ItemClick(reservationItem.id)) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ItemColumnContainer {
            ReservationsAndRequestsLabelTextWithOverflow(reservationItem.emailLabel)
            TextWithOverflow(value = reservationItem.email.fillWithSpaceIfEmpty())
        }

        ItemColumnContainer {
            ReservationsAndRequestsLabelTextWithOverflow(reservationItem.requestDateLabel)
            TextWithOverflow(value = reservationItem.requestedDate.fillWithSpaceIfEmpty())
        }

        ItemColumnContainer {
            ReservationsAndRequestsLabelTextWithOverflow(reservationItem.submittedDateLabel)
            TextWithOverflow(value = reservationItem.submittedDate.fillWithSpaceIfEmpty())
        }

        ItemColumnContainer(alignment = Alignment.Center) {
            StatusButton(
                itemStatus = reservationItem.status,
                modifier = statusButtonStyle.toModifier().styleModifier {
                    whiteSpace(WhiteSpace.NoWrap)
                    overflow(Overflow.Visible)
                    textOverflow(TextOverflow.Ellipsis)
                },
                onClick = { onEvent(UserRequestsEvent.ItemClick(reservationItem.id)) },
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
    reservationItem: UserRequestItemUi,
    onEvent: (UserRequestsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.then(reservationItemSmallScreenStyle.toModifier())
            .showModalOnClick(RESERVATION_ITEM_DIALOG_ID)
            .background(color = Colors.White)
            .roundedCornersShape(16.px)
            .padding(leftRight = 16.px, topBottom = 12.px)
            .clickable { onEvent(UserRequestsEvent.ItemClick(reservationItem.id)) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Row(modifier = Modifier.padding(bottom = 4.px)) {
                Text(
                    value = reservationItem.requestDateLabel,
                    modifier = reservationItemTextStyle.toModifier()
                        .then(requestedDateLabelStyle.toModifier())
                        .color(MR.colors.secondary.toCssColor())
                        .fontWeight(FontWeight.SemiBold)
                        .styleModifier {
                            whiteSpace(WhiteSpace.NoWrap)
                        },
                )
                Text(
                    modifier = reservationItemTextStyle.toModifier()
                        .styleModifier {
                            whiteSpace(WhiteSpace.NoWrap)
                            overflow(Overflow.Hidden)
                            textOverflow(TextOverflow.Ellipsis)
                            width(50.percent)
                        },
                    value = reservationItem.requestedDate.fillWithSpaceIfEmpty(),
                )
            }

            Row(modifier = Modifier.padding(bottom = 4.px)) {
                Text(
                    value = reservationItem.submittedDateLabel,
                    modifier = reservationItemTextStyle.toModifier()
                        .then(requestedDateLabelStyle.toModifier())
                        .color(MR.colors.secondary.toCssColor())
                        .fontWeight(FontWeight.SemiBold)
                        .styleModifier {
                            whiteSpace(WhiteSpace.NoWrap)
                        },
                )
                Text(
                    modifier = reservationItemTextStyle.toModifier()
                        .styleModifier {
                            whiteSpace(WhiteSpace.NoWrap)
                            overflow(Overflow.Hidden)
                            textOverflow(TextOverflow.Ellipsis)
                            width(50.percent)
                        },
                    value = reservationItem.submittedDate.fillWithSpaceIfEmpty(),
                )
            }

            Row {
                Text(
                    value = reservationItem.emailLabel,
                    modifier = reservationItemTextStyle.toModifier()
                        .then(emailLabelStyle.toModifier())
                        .color(MR.colors.secondary.toCssColor())
                        .fontWeight(FontWeight.SemiBold),
                )
                Text(
                    modifier = emailStyle.toModifier()
                        .then(reservationItemTextStyle.toModifier())
                        .styleModifier {
                            whiteSpace(WhiteSpace.NoWrap)
                            overflow(Overflow.Hidden)
                            textOverflow(TextOverflow.Ellipsis)
                        },
                    value = reservationItem.email.fillWithSpaceIfEmpty(),
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
            onClick = { onEvent(UserRequestsEvent.ItemClick(reservationItem.id)) },
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
    detailsState: UserRequestDetailsState?,
    allOptionText: String,
    size: ModalSize = ModalSize.Large,
    onEvent: (UserRequestsEvent) -> Unit,
) {
    Dialog(
        id = RESERVATION_ITEM_DIALOG_ID,
        size = size,
        clearContentOnClose = true,
        onClose = { onEvent(UserRequestsEvent.DetailsClosed) },
        content = { contentModifier ->
            detailsState?.let { state ->
                when (state) {
                    UserRequestDetailsState.Loading -> RequestDetailsLoadingScreen(modifier = contentModifier)
                    is UserRequestDetailsState.UserRequestDetailsItemUi -> RequestDetailsSuccessScreen(
                        reservationItem = state,
                        onEvent = onEvent,
                        allOptionText = allOptionText,
                        modifier = contentModifier,
                    )
                }
            }
        },
    )
}

@Composable
private fun RequestDetailsLoadingScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize().padding(topBottom = 50.px),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressBar()
    }
}

@Composable
private fun RequestDetailsSuccessScreen(
    reservationItem: UserRequestDetailsState.UserRequestDetailsItemUi,
    allOptionText: String,
    modifier: Modifier = Modifier,
    onEvent: (UserRequestsEvent) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(30.px),
    ) {
        Image(
            modifier = Modifier
                .hideModalOnClick()
                .cursor(Cursor.Pointer)
                .padding(bottom = 20.px),
            src = ImageRes.backIcon,
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                ReservationsAndRequestsLabelText(
                    value = reservationItem.submittedDateLabel,
                    modifier = Modifier.margin(bottom = 10.px),
                )
                TextArea(
                    value = reservationItem.submittedDate,
                    modifier = Modifier.width(180.px),
                    rowsCount = 1,
                    enabled = false,
                )

                ReservationsAndRequestsLabelText(
                    value = reservationItem.requestedDateLabel,
                    modifier = Modifier.margin(top = 20.px),
                )

                DateInput(
                    value = reservationItem.requestedDateDateInput,
                    enabled = false,
                    onValueChange = {},
                    modifier = Modifier.width(180.px).margin(top = 10.px),
                )
            }

            StatusDropdown(
                entries = reservationItem.statusDropdownEntries,
                selected = reservationItem.status,
                enabled = !reservationItem.savingActive,
                modifier = Modifier.align(Alignment.CenterEnd),
                allOptionText = allOptionText,
                onItemSelect = { status ->
                    onEvent(UserRequestsEvent.StatusChanged(status, reservationItem.id))
                },
            )
        }

        ReservationsAndRequestsLabelText(
            value = reservationItem.adminNotesLabel,
            modifier = Modifier.margin(top = 20.px),
        )

        TextArea(
            value = reservationItem.note,
            enabled = false,
            modifier = Modifier.fillMaxWidth().margin(top = 10.px),
        )

        if (reservationItem.status is ParkingReservationStatusUi.Approved) {
            ReservationsAndRequestsLabelText(
                value = reservationItem.approveNoteLabel,
                modifier = Modifier.margin(top = 20.px),
            )

            TextArea(
                value = reservationItem.approveNote,
                enabled = !reservationItem.savingActive,
                onValueChange = { onEvent(UserRequestsEvent.ApproveNoteChanged(it)) },
                modifier = Modifier.fillMaxWidth().margin(top = 10.px),
            )

            when (reservationItem.garageLevelAndParkingSpotsStatus) {
                is GarageLevelAndParkingSpotsStatus.Error -> Column(
                    modifier = Modifier.margin(top = 20.px),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(value = reservationItem.parkingSelectionData.parkingErrorMessage)

                    Button(
                        modifier = Modifier.margin(top = 10.px),
                        colorScheme = provideCustomColorScheme(MR.colors.secondary.toCssColor()),
                        enabled = !reservationItem.savingActive,
                        onClick = { onEvent(UserRequestsEvent.ReFetchGarageLevelsClick) },
                        content = {
                            Text(
                                value = reservationItem.parkingSelectionData.parkingErrorRetryMessage,
                                modifier = Modifier.color(Colors.White),
                            )
                        },
                    )
                }

                GarageLevelAndParkingSpotsStatus.Loading -> Box(
                    modifier = Modifier.margin(top = 20.px).fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressBar()
                }

                is GarageLevelAndParkingSpotsStatus.Success -> Box(
                    modifier = Modifier.margin(top = 20.px).fillMaxWidth(),
                ) {
                    if (reservationItem.parkingSelectionData.garageLevels.isNotEmpty()) {
                        with(reservationItem) {
                            ParkingSelection(
                                modifier = Modifier.align(Alignment.CenterStart),
                                garageLevelLabel = parkingSelectionData.garageLevelLabel,
                                garageLevelsEnabled = !savingActive,
                                garageLevelsEntries = parkingSelectionData.garageLevels,
                                garageLevelDefault = parkingSelectionData.selectedGarageLevel,
                                parkingSpotLabel = parkingSelectionData.parkingSpotLabel,
                                parkingSpotsEnabled = !savingActive && parkingSelectionData.parkingSpots.isNotEmpty(),
                                parkingSpotsEntries = parkingSelectionData.parkingSpots,
                                parkingSpotDefault = parkingSelectionData.selectedParkingSpot,
                                garageLevelChanged = { onEvent(UserRequestsEvent.GarageLevelChanged(it)) },
                                parkingSpotChanged = { onEvent(UserRequestsEvent.ParkingSpotChanged(it)) },
                            )
                        }
                    } else {
                        InfoMessage(
                            message = reservationItem.parkingSelectionData.noParkingSpacesMessage,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }
            }
        }

        if (reservationItem.garageAccessSwitchVisible) {
            PermanentGarageAccessSelection(
                modifier = Modifier.fillMaxWidth().margin(top = 20.px),
                checked = reservationItem.hasPermanentGarageAccess,
                label = reservationItem.hasPermanentGarageAccessLabel,
                enabled = !reservationItem.savingActive,
                onCheckedChange = { onEvent(UserRequestsEvent.TogglePermanentGarageAccess) },
            )
        }

        if (reservationItem.status is ParkingReservationStatusUi.Rejected) {
            ReservationsAndRequestsLabelText(
                modifier = Modifier.margin(top = 20.px),
                value = reservationItem.rejectReasonLabel,
            )

            TextArea(
                value = reservationItem.rejectReason,
                enabled = !reservationItem.savingActive,
                onValueChange = { onEvent(UserRequestsEvent.RejectReasonChanged(it)) },
                modifier = Modifier.fillMaxWidth().margin(top = 10.px),
            )
        }

        if (reservationItem.saveVisible) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd,
            ) {
                LoadingButton(
                    colorScheme = provideCustomColorScheme(MR.colors.secondary.toCssColor()),
                    modifier = Modifier.margin(top = 20.px).padding(leftRight = 80.px),
                    isLoading = reservationItem.savingActive,
                    enabled = !reservationItem.savingActive && reservationItem.parkingSelectionData.validParkingOption,
                    onClick = { onEvent(UserRequestsEvent.SaveClick) },
                    content = {
                        Text(
                            value = reservationItem.saveButtonText,
                            modifier = Modifier.color(Colors.White),
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun StatusDropdown(
    entries: List<ParkingReservationStatusUi?>,
    selected: ParkingReservationStatusUi?,
    enabled: Boolean,
    allOptionText: String,
    modifier: Modifier = Modifier,
    onItemSelect: (ParkingReservationStatusUi?) -> Unit,
) {
    Dropdown(
        modifier = modifier,
        items = entries,
        enabled = enabled,
        defaultContent = {
            Row(
                modifier = Modifier
                    .border {
                        color(MR.colors.textLight.toCssColor())
                        style(LineStyle.Solid)
                        width(1.px)
                    }
                    .borderRadius(12.px)
                    .padding(4.px),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (selected != null) {
                    StatusButton(
                        itemStatus = selected,
                        modifier = statusButtonStyle.toModifier().thenIf(
                            condition = !enabled,
                            other = Modifier.defaultCursor(),
                        ),
                    ) {
                        TextWithOverflow(
                            value = selected.text,
                            modifier = reservationItemTextStyle.toModifier().color(Colors.White),
                        )
                    }
                } else {
                    ParkingStatusAllOption(
                        text = allOptionText,
                        modifier = statusButtonStyle.toModifier().thenIf(
                            condition = !enabled,
                            other = Modifier.defaultCursor(),
                        ),
                    )
                }

                Image(
                    src = ImageRes.userRequestsStatusDropdown,
                    modifier = Modifier.margin(left = 20.px),
                )
            }
        },
        itemContent = { currentItem, modifier ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (currentItem != null) {
                    StatusButton(
                        itemStatus = currentItem,
                        modifier = modifier.then(statusButtonStyle.toModifier()),
                    ) {
                        TextWithOverflow(
                            value = currentItem.text,
                            modifier = reservationItemTextStyle.toModifier().color(Colors.White),
                        )
                    }
                } else {
                    ParkingStatusAllOption(
                        text = allOptionText,
                        modifier = modifier.then(statusButtonStyle.toModifier()),
                    )
                }

                if (currentItem?.text == selected?.text) {
                    Image(
                        src = ImageRes.checkMark,
                        modifier = Modifier.margin(left = 10.px),
                    )
                }
            }
        },
        onItemSelect = { _, item -> onItemSelect(item) },
    )
}

@Composable
private fun ParkingStatusAllOption(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color.Rgb = Colors.White,
    backgroundColor: Color.Rgb = MR.colors.textLight.toCssColor(),
    onClick: () -> Unit = {},
) {
    Button(
        modifier = modifier,
        colorScheme = provideCustomColorScheme(backgroundColor),
        content = {
            TextWithOverflow(
                value = text,
                modifier = reservationItemTextStyle.toModifier().color(textColor),
            )
        },
        onClick = { onClick() },
    )
}

private const val RESERVATION_ITEM_DIALOG_ID = "reservationItemDialog"
