package org.product.inventory.web.pages.seatreservationtimeline

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.overflowY
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.alignItems
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.product.inventory.shared.MR
import org.product.inventory.web.components.CircularImage
import org.product.inventory.web.components.CircularProgressBar
import org.product.inventory.web.components.Dropdown
import org.product.inventory.web.components.ProfileInfo
import org.product.inventory.web.components.Text
import org.product.inventory.web.components.TextWithOverflow
import org.product.inventory.web.components.TitleWithPath
import org.product.inventory.web.components.clickable
import org.product.inventory.web.components.fullSizeVisibleModifier
import org.product.inventory.web.components.invisibleModifier
import org.product.inventory.web.components.roundedCornersShape
import org.product.inventory.web.components.toDateInputValue
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.pages.seatreservationtimeline.SeatReservationTimelineEvent.OnActionOptionClick
import org.product.inventory.web.toCssColor
import seatreservation.model.Office

val seatReservationTimelineSmallScreenStyle by ComponentStyle {
    Breakpoint.ZERO {
        fullSizeVisibleModifier
    }

    Breakpoint.LG {
        invisibleModifier
    }
}

val seatReservationTimelineLargeScreenStyle by ComponentStyle {
    Breakpoint.ZERO {
        invisibleModifier
    }

    Breakpoint.LG {
        fullSizeVisibleModifier
    }
}

val reservableDateContentItemWidthStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.width(300.px)
    }

    Breakpoint.MD {
        Modifier.width(400.px)
    }
}

@Composable
fun SeatReservationTimeline(
    state: SeatReservationTimelineUiState,
    datesCtaStatus: Map<Long, Boolean>,
    modifier: Modifier = Modifier,
    onEvent: (SeatReservationTimelineEvent) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        TitleWithPath(
            items = state.breadcrumbItems,
            title = state.title,
            onPathClick = { }, // This click event will never be triggered due to current design
        )

        OfficePicker(
            offices = state.availableOffices,
            selectedOffice = state.selectedOffice,
            onItemSelectedClick = { onEvent(SeatReservationTimelineEvent.OnOfficeChanged(it)) },
        )

        ReservableDateUiList(
            items = state.reservableDateUiItems,
            isLoading = state.isLoading,
            selectedOffice = state.selectedOffice,
            datesCtaStatus = datesCtaStatus,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun OfficePicker(
    offices: List<Office>,
    selectedOffice: Office?,
    modifier: Modifier = Modifier,
    onItemSelectedClick: (Office) -> Unit,
) {
    Dropdown(
        modifier = modifier.padding(30.px),
        items = offices,
        enabled = selectedOffice != null,
        defaultContent = {
            Row(
                modifier = Modifier
                    .border {
                        color(MR.colors.textLight.toCssColor())
                        style(LineStyle.Solid)
                        width(1.px)
                    }
                    .borderRadius(12.px)
                    .padding(10.px)
                    .thenIf(
                        condition = selectedOffice == null,
                        other = Modifier.padding(topBottom = 10.px, leftRight = 60.px),
                    )
                    .background(Colors.White),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (selectedOffice != null) {
                    OfficePickerItemContent(text = selectedOffice.title)
                } else {
                    CircularProgressBar(modifier = Modifier.size(25.px))
                }
            }
        },
        itemContent = { item, modifier ->
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    value = item.title,
                    modifier = Modifier.color(MR.colors.secondary.toCssColor()),
                )
            }
        },
        onItemSelect = { _, item -> onItemSelectedClick(item) },
    )
}

@Composable
private fun OfficePickerItemContent(text: String) {
    Text(
        modifier = Modifier
            .padding(topBottom = 3.px, leftRight = 30.px)
            .fontWeight(FontWeight.SemiBold)
            .color(MR.colors.secondary.toCssColor()),
        value = text,
    )
    Image(
        src = ImageRes.userRequestsStatusDropdown,
        modifier = Modifier.margin(left = 16.px),
    )
}

@Composable
private fun ColumnScope.ReservableDateUiList(
    items: List<ReservableDateUiItem>,
    isLoading: Boolean,
    selectedOffice: Office?,
    datesCtaStatus: Map<Long, Boolean>,
    onEvent: (SeatReservationTimelineEvent) -> Unit,
) {
    ReservableDateUiListSmallScreen(
        items = items,
        isLoading = isLoading,
        selectedOffice = selectedOffice,
        datesCtaStatus = datesCtaStatus,
        onEvent = onEvent,
        modifier = seatReservationTimelineSmallScreenStyle.toModifier(),
    )
    ReservableDateUiListLargeScreen(
        items = items,
        isLoading = isLoading,
        selectedOffice = selectedOffice,
        datesCtaStatus = datesCtaStatus,
        onEvent = onEvent,
        modifier = seatReservationTimelineLargeScreenStyle.toModifier(),
    )
}

@Composable
private fun ColumnScope.ReservableDateUiListSmallScreen(
    items: List<ReservableDateUiItem>,
    isLoading: Boolean,
    selectedOffice: Office?,
    datesCtaStatus: Map<Long, Boolean>,
    onEvent: (SeatReservationTimelineEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally)
            .alignItems(AlignItems.Center)
            .gap(40.px)
            .padding(topBottom = 20.px, leftRight = 30.px),
    ) {
        ReservableDateUiListContent(
            items = items,
            isLoading = isLoading,
            selectedOffice = selectedOffice,
            datesCtaStatus = datesCtaStatus,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun ColumnScope.ReservableDateUiListLargeScreen(
    items: List<ReservableDateUiItem>,
    isLoading: Boolean,
    selectedOffice: Office?,
    datesCtaStatus: Map<Long, Boolean>,
    onEvent: (SeatReservationTimelineEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally)
            .gap(24.px)
            .padding(leftRight = 30.px),
    ) {
        ReservableDateUiListContent(
            items = items,
            isLoading = isLoading,
            selectedOffice = selectedOffice,
            datesCtaStatus = datesCtaStatus,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun ReservableDateUiListContent(
    items: List<ReservableDateUiItem>,
    isLoading: Boolean,
    selectedOffice: Office?,
    datesCtaStatus: Map<Long, Boolean>,
    onEvent: (SeatReservationTimelineEvent) -> Unit,
) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.px),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressBar()
        }
    } else {
        items.forEach { reservableDate ->
            Column {
                DateUi(reservableDate.dateUi)
                ReservableItemContent(
                    item = reservableDate,
                    selectedOffice = selectedOffice,
                    datesCtaStatus = datesCtaStatus,
                    onEvent = onEvent,
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.DateUi(
    dateUi: DateUi,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .align(Alignment.CenterHorizontally)
            .padding(topBottom = 6.px)
            .width(60.percent)
            .background(dateUi.background)
            .roundedCornersShape(topLeft = 18.px, topRight = 18.px),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        listOf(
            dateUi.date.toDateInputValue().formattedAsDayMonthYear,
            dateUi.day,
        ).forEach { item ->
            Text(
                modifier = Modifier
                    .color(dateUi.textColor)
                    .fontWeight(FontWeight.SemiBold)
                    .textAlign(TextAlign.Center),
                size = 20.px,
                value = item,
            )
        }
    }
}

@Composable
private fun ReservableItemContent(
    item: ReservableDateUiItem,
    selectedOffice: Office?,
    datesCtaStatus: Map<Long, Boolean>,
    onEvent: (SeatReservationTimelineEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = reservableDateContentItemWidthStyle
            .toModifier()
            .then(modifier)
            .height(400.px)
            .background(Colors.White)
            .borderRadius(24.px)
            .border {
                color(MR.colors.textLight.toCssColor())
                style(LineStyle.Solid)
                width(1.px)
            },
    ) {
        when (item) {
            is ReservableDateUiItem.Weekday -> WeekdayReservableContentItem(
                item = item,
                isLoading = datesCtaStatus[item.dateUi.date],
                selectedOffice = selectedOffice,
                onEvent = onEvent,
            )
            is ReservableDateUiItem.Weekend -> WeekendReservableContentItem(item.buttonUi)
        }
    }
}

@Composable
private fun ColumnScope.WeekdayReservableContentItem(
    item: ReservableDateUiItem.Weekday,
    isLoading: Boolean?,
    selectedOffice: Office?,
    onEvent: (SeatReservationTimelineEvent) -> Unit,
) {
    Row(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            value = "${item.remainingSeats} ",
            size = 16.px,
            modifier = Modifier
                .padding(top = 16.px)
                .fontWeight(FontWeight.Bold)
                .color(MR.colors.secondary.toCssColor()),
        )
        Text(
            value = item.remainingSeatsText,
            modifier = Modifier
                .padding(top = 15.px)
                .color(MR.colors.textBlack.toCssColor()),
        )
    }

    SeatHolderList(
        seatHolders = item.seatHolders,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )

    ReservableItemButton(
        reservableDateButtonUi = item.buttonUi,
        isLoading = isLoading,
        modifier = Modifier.align(Alignment.CenterHorizontally),
        onClick = {
            selectedOffice?.let {
                onEvent(OnActionOptionClick(item.dateUi.date, item.option, selectedOffice.id))
            }
        },
    )
}

@Composable
private fun SeatHolderList(
    seatHolders: List<ProfileInfo>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(300.px)
            .fillMaxWidth()
            .margin(top = 5.px)
            .styleModifier { overflowY(Overflow.Auto) }
            .padding(top = 5.px, bottom = 10.px, leftRight = 10.px),
    ) {
        seatHolders.forEach { profileInfo ->
            SeatHolderItem(
                seatHolder = profileInfo,
                modifier = Modifier.margin(4.px),
            )
        }
    }
}

@Composable
private fun SeatHolderItem(
    seatHolder: ProfileInfo,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(right = 10.px)
            .borderRadius(24.px)
            .border {
                color(MR.colors.textLight.toCssColor())
                style(LineStyle.Solid)
                width(1.px)
            },
    ) {
        CircularImage(
            path = seatHolder.icon,
            modifier = Modifier
                .borderRadius(r = 28.px)
                .border {
                    color(MR.colors.secondary.toCssColor())
                    style(LineStyle.Solid)
                    width(1.px)
                }
                .margin(top = 5.px, left = 5.px, right = 12.px, bottom = 5.px),
            size = 28.px,
        )
        TextWithOverflow(
            modifier = Modifier
                .color(MR.colors.textBlack.toCssColor())
                .padding(right = 4.px)
                .align(Alignment.CenterVertically),
            value = seatHolder.username,
        )
    }
}

@Composable
private fun ColumnScope.WeekendReservableContentItem(reservableDateButtonUi: ReservableDateButtonUi) {
    Image(
        src = ImageRes.seatReservationTimelineWeekend,
        modifier = Modifier
            .weight(1)
            .size(180.px)
            .align(Alignment.CenterHorizontally),
    )

    ReservableItemButton(
        reservableDateButtonUi = reservableDateButtonUi,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
}

@Composable
private fun ReservableItemButton(
    reservableDateButtonUi: ReservableDateButtonUi,
    isLoading: Boolean? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .thenIf(
                condition = reservableDateButtonUi.isEnabled,
                other = Modifier.clickable(onClick),
            )
            .padding(top = 16.px, bottom = 18.px)
            .background(reservableDateButtonUi.background)
            .borderRadius(bottomLeft = 24.px, bottomRight = 24.px),
    ) {
        if (isLoading == true) {
            CircularProgressBar(
                modifier = Modifier.align(Alignment.Center).size(25.px),
                color = MR.colors.textBlack.toCssColor(),
            )
        } else {
            Text(
                modifier = Modifier
                    .color(reservableDateButtonUi.textButtonColor)
                    .fontWeight(FontWeight.Bold)
                    .align(Alignment.Center),
                size = 16.px,
                value = reservableDateButtonUi.buttonText,
            )
        }
    }
}
