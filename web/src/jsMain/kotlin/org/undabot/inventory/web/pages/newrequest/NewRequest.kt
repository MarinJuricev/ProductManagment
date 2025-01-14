package org.product.inventory.web.pages.newrequest

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.ButtonSize
import com.varabyte.kobweb.silk.components.forms.TextInput
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.product.inventory.shared.MR
import org.product.inventory.web.components.CircularProgressBar
import org.product.inventory.web.components.ContentWithBadge
import org.product.inventory.web.components.DateInput
import org.product.inventory.web.components.DateInputValue
import org.product.inventory.web.components.Dialog
import org.product.inventory.web.components.InfoMessage
import org.product.inventory.web.components.LoadingButton
import org.product.inventory.web.components.ModalSize
import org.product.inventory.web.components.OneLineText
import org.product.inventory.web.components.ParkingSelection
import org.product.inventory.web.components.PermanentGarageAccessSelection
import org.product.inventory.web.components.ProfileInfo
import org.product.inventory.web.components.Text
import org.product.inventory.web.components.TextArea
import org.product.inventory.web.components.TitleWithPath
import org.product.inventory.web.components.clickable
import org.product.inventory.web.components.closeDialog
import org.product.inventory.web.components.hideModalOnClick
import org.product.inventory.web.components.provideCustomColorScheme
import org.product.inventory.web.components.roundedCornersShape
import org.product.inventory.web.components.showModalOnClick
import org.product.inventory.web.components.spaceBetween
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.models.GarageLevelAndParkingSpotsStatus
import org.product.inventory.web.models.ParkingSelectionData
import org.product.inventory.web.toCssColor
import parking.reservation.model.MultipleParkingRequestState

val newRequestDateStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.width(60.percent)
    }

    Breakpoint.SM {
        Modifier.width(45.percent)
    }

    Breakpoint.MD {
        Modifier.width(30.percent)
    }

    Breakpoint.LG {
        Modifier.width(25.percent)
    }
}

val newRequestProfileInfoStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.display(DisplayStyle.InlineBlock)
    }

    Breakpoint.MD {
        Modifier.display(DisplayStyle.Flex)
    }
}

val newRequestProfileInfoMarginStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.margin(0.px)
    }

    Breakpoint.SM {
        Modifier.margin(10.px)
    }
}

@Composable
fun NewRequest(
    state: NewRequestState,
    userListState: UserListDialogState?,
    multiDateSelectionState: MultiDateSelectionState,
    onEvent: (NewRequestEvent) -> Unit,
) {
    if (state.closeDialog) {
        closeDialog(USER_LIST_DIALOG_ID)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            TitleWithPath(
                items = state.staticData.breadcrumbItems,
                title = state.staticData.title,
                onPathClick = { onEvent(NewRequestEvent.PathClick(it)) },
            )

            InputBox(
                state = state,
                multiDateSelectionState = multiDateSelectionState,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onEvent = onEvent,
            )
        }

        UserListDialog(
            filterText = state.userListFilterText,
            listState = userListState,
            emptyListMessage = state.staticData.emptyUserListMessage,
            filterPlaceholder = state.staticData.userListFilterPlaceholder,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun UserListDialog(
    filterText: String,
    filterPlaceholder: String,
    emptyListMessage: String,
    listState: UserListDialogState?,
    modifier: Modifier = Modifier,
    onEvent: (NewRequestEvent) -> Unit,
) {
    Dialog(
        id = USER_LIST_DIALOG_ID,
        modifier = modifier,
        onClose = { onEvent(NewRequestEvent.UserListClosed) },
        size = ModalSize.Large,
        content = { contentModifier ->
            Column(
                modifier = contentModifier.fillMaxSize().padding(30.px),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().spaceBetween().margin(bottom = 20.px),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        modifier = Modifier
                            .hideModalOnClick()
                            .cursor(Cursor.Pointer)
                            .padding(bottom = 20.px),
                        src = ImageRes.backIcon,
                    )

                    (listState as? UserListDialogState.Success)?.let {
                        TextInput(
                            text = filterText,
                            placeholder = filterPlaceholder,
                            onTextChanged = { onEvent(NewRequestEvent.UserListFilterChanged(it)) },
                        )
                    }
                }

                listState?.let { state ->
                    when (state) {
                        is UserListDialogState.Error -> Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(state.message)
                        }
                        UserListDialogState.Loading -> Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressBar()
                        }
                        is UserListDialogState.Success -> UserListSuccessContent(
                            users = state.users,
                            emptyListMessage = emptyListMessage,
                            onEvent = onEvent,
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun UserListSuccessContent(
    users: List<UserListItem>,
    emptyListMessage: String,
    onEvent: (NewRequestEvent) -> Unit,
) {
    if (users.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(emptyListMessage)
        }
    } else {
        SimpleGrid(
            modifier = Modifier.fillMaxSize().gap(columnGap = 24.px, rowGap = 0.px),
            numColumns = numColumns(
                base = 1,
                sm = 1,
                md = 1,
                lg = 2,
                xl = 2,
            ),
        ) {
            users.forEach { userItem ->
                ProfileInfo(
                    profileInfo = userItem.profileInfo,
                    imageSize = 45.px,
                    showRole = false,
                    hoverColor = MR.colors.textLight.toCssColor(),
                    modifier = Modifier.fillMaxWidth()
                        .padding(10.px)
                        .roundedCornersShape(20.px),
                    onClick = { onEvent(NewRequestEvent.UserListItemClick(userItem)) },
                )
            }
        }
    }
}

@Composable
private fun InputBox(
    state: NewRequestState,
    multiDateSelectionState: MultiDateSelectionState,
    modifier: Modifier = Modifier,
    onEvent: (NewRequestEvent) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(percent = 70.percent)
            .padding(10.px)
            .margin(bottom = 20.px)
            .background(Colors.White)
            .roundedCornersShape(23.px),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                FormText(
                    value = state.staticData.personLabel,
                    color = MR.colors.textBlack.toCssColor(),
                )

                Row(
                    modifier = newRequestProfileInfoMarginStyle
                        .toModifier()
                        .thenIf(
                            condition = state.requestMode is RequestMode.Automatic,
                            other = Modifier
                                .showModalOnClick(USER_LIST_DIALOG_ID)
                                .clickable { onEvent(NewRequestEvent.ProfileInfoClick) },
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ProfileInfo(
                        profileInfo = state.profileInfo,
                        modifier = newRequestProfileInfoStyle.toModifier(),
                    )

                    if (state.requestMode is RequestMode.Automatic) {
                        Image(
                            modifier = newRequestProfileInfoMarginStyle
                                .toModifier()
                                .size(16.px),
                            src = ImageRes.newRequestEdit,
                        )
                    }
                }
            }

            FormText(
                value = state.staticData.dateLabel,
                modifier = Modifier.margin(top = 40.px),
            )
            if (multiDateSelectionState.selectedRequestDates.isNotEmpty()) {
                SelectedRequestDates(
                    dates = multiDateSelectionState.selectedRequestDates,
                    onEvent = onEvent,
                )
            }
            if (multiDateSelectionState.shouldShowDateInput) {
                DateInput(
                    value = state.date,
                    minDate = state.minDate,
                    enabled = !state.isLoading,
                    modifier = newRequestDateStyle.toModifier().margin(top = 20.px),
                    onValueChange = { onEvent(NewRequestEvent.DateChanged(it)) },
                )
            }
            if (multiDateSelectionState.shouldShowAddDateButton) {
                AddDateButton(
                    text = multiDateSelectionState.staticData.addDateButtonText,
                    modifier = newRequestDateStyle.toModifier().margin(top = 20.px),
                    isLoading = state.isLoading,
                ) {
                    onEvent(NewRequestEvent.AddRequestDate(state.date))
                }
            }

            FormText(
                value = state.notesLabel,
                modifier = Modifier.margin(top = 40.px),
            )
            TextArea(
                value = state.notes,
                placeholder = state.notesPlaceholder,
                enabled = !state.isLoading,
                modifier = Modifier.margin(top = 20.px),
                onValueChange = { onEvent(NewRequestEvent.NotesChanged(it)) },
            )

            (state.requestMode as? RequestMode.Automatic)?.let {
                state.garageLevelAndParkingSpotsStatus?.let {
                    GarageLevelAndParkingSpotsContent(
                        parkingSelectionData = state.parkingSelectionData,
                        garageLevelAndParkingSpotsStatus = it,
                        noParkingSpacesMessage = state.staticData.noParkingSpacesMessage,
                        onEvent = onEvent,
                    )
                }

                PermanentGarageAccessSelection(
                    modifier = Modifier.fillMaxWidth().margin(top = 20.px),
                    checked = state.garageAccess,
                    label = state.parkingSelectionData.garageLevelLabel,
                    enabled = !state.isLoading,
                    onCheckedChange = { onEvent(NewRequestEvent.ToggleGarageAccess) },
                )
            }

            state.lateReservationMessage?.let {
                Box(
                    modifier = Modifier.margin(top = 40.px, leftRight = 20.px),
                    contentAlignment = Alignment.Center,
                ) {
                    InfoMessage(
                        message = it,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .margin(
                        top = when {
                            state.lateReservationMessage != null -> 40.px
                            state.requestMode is RequestMode.Automatic -> 60.px
                            else -> 100.px
                        },
                    ),
                horizontalArrangement = Arrangement.End,
            ) {
                LoadingButton(
                    modifier = Modifier.padding(leftRight = 80.px),
                    type = ButtonType.Submit,
                    size = ButtonSize.LG,
                    isLoading = state.isLoading,
                    enabled = state.submitEnabled && !state.isLoading,
                    content = {
                        Text(
                            value = state.staticData.submitText,
                            modifier = Modifier.color(Colors.White),
                            size = 16.px,
                        )
                    },
                    onClick = { onEvent(NewRequestEvent.SubmitClick) },
                )
            }
        }
    }
}

@Composable
private fun FormText(
    value: String,
    modifier: Modifier = Modifier,
    color: Color.Rgb = MR.colors.secondary.toCssColor(),
    fontSize: CSSLengthOrPercentageNumericValue = 16.px,
) {
    OneLineText(
        value = value,
        size = fontSize,
        modifier = modifier
            .color(color = color)
            .fontWeight(FontWeight.SemiBold),
    )
}

@Composable
private fun SelectedRequestDates(
    dates: Map<DateInputValue, MultipleParkingRequestState>,
    modifier: Modifier = Modifier,
    onEvent: (NewRequestEvent) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.px)
            .display(DisplayStyle.Flex)
            .flexWrap(FlexWrap.Wrap)
            .gap(20.px),
    ) {
        dates.forEach { date ->
            ContentWithBadge(
                content = {
                    DateItem(
                        text = date.key.formattedAsDayMonthYear,
                        background = date.value.getBackgroundColor(),
                    )
                },
                badgeContent = { modifier ->
                    Image(
                        modifier = modifier.clickable {
                            onEvent(
                                NewRequestEvent.RemoveRequestDate(
                                    date.key,
                                ),
                            )
                        },
                        src = ImageRes.closeIcon,
                    )
                },
            )
        }
    }
}

@Composable
private fun DateItem(
    text: String,
    background: Color.Rgb,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .roundedCornersShape(9.px)
            .padding(8.px)
            .background(background)
            .minWidth(95.px),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            value = text,
            size = 14.px,
            modifier = Modifier
                .color(Colors.White)
                .fontWeight(FontWeight.SemiBold)
                .textAlign(TextAlign.Center),
        )
    }
}

@Composable
private fun AddDateButton(
    text: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    LoadingButton(
        modifier = modifier,
        colorScheme = provideCustomColorScheme(MR.colors.secondary.toCssColor()),
        type = ButtonType.Submit,
        isLoading = isLoading,
        content = {
            Text(
                value = text,
                modifier = Modifier.color(Colors.White),
            )
        },
        onClick = onClick,
    )
}

@Composable
private fun ColumnScope.GarageLevelAndParkingSpotsContent(
    noParkingSpacesMessage: String,
    parkingSelectionData: ParkingSelectionData,
    garageLevelAndParkingSpotsStatus: GarageLevelAndParkingSpotsStatus,
    onEvent: (NewRequestEvent) -> Unit,
) {
    when (garageLevelAndParkingSpotsStatus) {
        GarageLevelAndParkingSpotsStatus.Error -> GarageLevelAndParkingSpotsStatusError(
            errorMessage = parkingSelectionData.parkingErrorMessage,
            retryButtonText = parkingSelectionData.parkingErrorRetryMessage,
            onRetryClick = { onEvent(NewRequestEvent.ReFetchGarageLevelDataClick) },
        )
        GarageLevelAndParkingSpotsStatus.Loading -> GarageLevelAndParkingSpotsStatusLoading()
        is GarageLevelAndParkingSpotsStatus.Success -> GarageLevelAndParkingSpotsStatusSuccess(
            noParkingSpacesMessage = noParkingSpacesMessage,
            parkingSelectionData = parkingSelectionData,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun GarageLevelAndParkingSpotsStatusLoading() {
    Box(
        modifier = Modifier.margin(top = 20.px).fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressBar()
    }
}

@Composable
private fun ColumnScope.GarageLevelAndParkingSpotsStatusSuccess(
    noParkingSpacesMessage: String,
    parkingSelectionData: ParkingSelectionData,
    onEvent: (NewRequestEvent) -> Unit,
) {
    if (parkingSelectionData.garageLevels.isEmpty()) {
        InfoMessage(
            message = noParkingSpacesMessage,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    } else {
        ParkingSelection(
            modifier = Modifier.fillMaxWidth().margin(top = 20.px),
            garageLevelLabel = parkingSelectionData.garageLevelLabel,
            garageLevelsEnabled = true,
            garageLevelsEntries = parkingSelectionData.garageLevels,
            garageLevelDefault = parkingSelectionData.selectedGarageLevel,
            parkingSpotLabel = parkingSelectionData.parkingSpotLabel,
            parkingSpotsEnabled = parkingSelectionData.parkingSpots.isNotEmpty(),
            parkingSpotsEntries = parkingSelectionData.parkingSpots,
            parkingSpotDefault = parkingSelectionData.selectedParkingSpot,
            garageLevelChanged = { onEvent(NewRequestEvent.GarageLevelChanged(it)) },
            parkingSpotChanged = { onEvent(NewRequestEvent.ParkingSpotChanged(it)) },
        )
    }
}

@Composable
private fun GarageLevelAndParkingSpotsStatusError(
    errorMessage: String,
    retryButtonText: String,
    onRetryClick: () -> Unit,
) {
    Column(
        modifier = Modifier.margin(top = 20.px),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(value = errorMessage)

        Button(
            modifier = Modifier.margin(top = 10.px),
            colorScheme = provideCustomColorScheme(MR.colors.secondary.toCssColor()),
            enabled = true,
            onClick = { onRetryClick() },
            content = {
                Text(
                    value = retryButtonText,
                    modifier = Modifier.color(Colors.White),
                )
            },
        )
    }
}

private const val USER_LIST_DIALOG_ID = "userListDialog"
