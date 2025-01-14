package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextOverflow
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.product.inventory.shared.MR
import org.product.inventory.web.toCssColor

val reservationItemLargeScreenStyle by ComponentStyle {
    Breakpoint.ZERO {
        invisibleModifier
    }

    Breakpoint.LG {
        fullSizeVisibleModifier
    }
}

val reservationItemSmallScreenStyle by ComponentStyle {
    Breakpoint.ZERO {
        fullSizeVisibleModifier
    }

    Breakpoint.LG {
        invisibleModifier
    }
}

val reservationItemTextStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.fontSize(8.px)
    }

    Breakpoint.SM {
        Modifier.fontSize(12.px)
    }

    Breakpoint.MD {
        Modifier.fontSize(16.px)
    }
}

val statusButtonStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier
            .width(80.px)
            .height(20.px)
    }

    Breakpoint.SM {
        Modifier
            .height(25.px)
            .width(100.px)
    }

    Breakpoint.MD {
        Modifier
            .height(35.px)
            .width(140.px)
    }

    Breakpoint.LG {
        Modifier
            .height(30.px)
            .width(120.px)
    }
}

val dialogParkingOptionsStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.display(DisplayStyle.InlineBlock)
    }

    Breakpoint.SM {
        Modifier.display(DisplayStyle.Flex)
    }
}

@Composable
fun <T> ReservationsAndRequestsLayout(
    breadcrumbItems: List<BreadcrumbItem>,
    title: String,
    emptyListMessage: String,
    data: List<T>,
    isLoading: Boolean,
    itemContent: @Composable (Int, T) -> Unit,
    header: @Composable ColumnScope.() -> Unit,
    onPathClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        TitleWithPath(
            items = breadcrumbItems,
            title = title,
            onPathClick = onPathClick,
        )

        header()

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
            if (data.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        value = emptyListMessage,
                        modifier = Modifier
                            .fontWeight(FontWeight.SemiBold)
                            .padding(30.px),
                        size = 16.px,
                    )
                }
            } else {
                ItemsList(
                    data = data,
                    itemContent = itemContent,
                    modifier = Modifier.padding(30.px).fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun <T> ItemsList(
    data: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (Int, T) -> Unit,
) {
    Column(modifier = modifier) {
        data.forEachIndexed { index, item ->
            itemContent(index, item)
        }
    }
}

@Composable
fun ReservationsAndRequestsLabelText(
    value: String,
    fontSize: CSSLengthOrPercentageNumericValue? = 16.px,
    modifier: Modifier = Modifier,
) {
    Text(
        value = value,
        modifier = modifier
            .color(MR.colors.secondary.toCssColor())
            .fontWeight(FontWeight.SemiBold),
        size = fontSize,
    )
}

@Composable
fun ReservationsAndRequestsLabelTextWithOverflow(
    value: String,
    modifier: Modifier = Modifier,
    textOverflow: TextOverflow = TextOverflow.Clip,
) {
    TextWithOverflow(
        value = value,
        modifier = modifier
            .color(MR.colors.secondary.toCssColor())
            .fontWeight(FontWeight.SemiBold),
        size = 16.px,
        textOverflow = textOverflow,
    )
}

@Composable
fun ItemColumnContainer(
    modifier: Modifier = Modifier,
    width: CSSLengthOrPercentageNumericValue = 23.percent,
    maxWidth: CSSLengthOrPercentageNumericValue = 100.percent,
    marginLeft: CSSLengthOrPercentageNumericValue = 2.px,
    marginRight: CSSLengthOrPercentageNumericValue = 2.px,
    alignment: Alignment = Alignment.CenterStart,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .width(width)
            .margin(left = marginLeft, right = marginRight),
        contentAlignment = alignment,
    ) {
        Column(
            modifier = Modifier.maxWidth(maxWidth),
        ) {
            content()
        }
    }
}
