package org.product.inventory.web.pages.seatreservation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.position
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.product.inventory.shared.MR
import org.product.inventory.web.components.Text
import org.product.inventory.web.components.TitleWithPath
import org.product.inventory.web.components.clickable
import org.product.inventory.web.components.roundedCornersShape
import org.product.inventory.web.toCssColor

@Composable
fun SeatReservation(
    state: SeatReservationUiState,
    onEvent: (SeatReservationEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        TitleWithPath(
            items = state.breadcrumbItems,
            title = state.title,
            onPathClick = { onEvent(SeatReservationEvent.PathClick(it)) },
        )

        SimpleGrid(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .gap(24.px)
                .padding(topBottom = 20.px),
            numColumns = numColumns(
                base = 1,
                sm = 1,
                md = 2,
                lg = 2,
                xl = 3,
            ),
        ) {
            state.seatReservationItems.forEach {
                ReservationItem(
                    item = it,
                    modifier = Modifier.clickable {
                        onEvent(SeatReservationEvent.ItemClick(it.type))
                    },
                )
            }
        }
    }
}

@Composable
private fun ReservationItem(
    item: SeatReservationItemUi,
    modifier: Modifier = Modifier,
) {
    var width by remember { mutableStateOf(0f) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(80.percent)
                .align(alignment = Alignment.Center)
                .roundedCornersShape(20.px)
                .background(color = Colors.White),
        ) {
            Div(
                attrs = Modifier.fillMaxWidth().toAttrs {
                    style {
                        display(DisplayStyle.Flex)
                        position(Position.Relative)
                        alignItems(AlignItems.Center)
                    }
                },
            ) {
                Box(
                    modifier = Modifier
                        .background(color = MR.colors.secondary.toCssColor())
                        .size(60.px)
                        .attrsModifier {
                            ref { element ->
                                width = element.getBoundingClientRect().width.toFloat()
                                onDispose { }
                            }
                            style { flex("0 1 auto") }
                        }
                        .roundedCornersShape(topLeft = 20.px, bottomRight = 20.px),
                ) {
                    Image(src = item.icon, modifier = Modifier.size(32.px).align(Alignment.Center))
                }

                Box(modifier = Modifier.attrsModifier { style { flex(1) } })

                Text(
                    modifier = Modifier
                        .fontWeight(FontWeight.SemiBold)
                        .margin(right = width.px)
                        .attrsModifier {
                            style {
                                flex("0 1 auto")
                                textAlign(TextAlign.Center)
                            }
                        },
                    value = item.title,
                    size = 20.px,
                )

                Box(modifier = Modifier.attrsModifier { style { flex(1) } })
            }

            Text(
                modifier = Modifier
                    .padding(26.px)
                    .color(MR.colors.textBlack.toCssColor())
                    .align(Alignment.CenterHorizontally)
                    .textAlign(TextAlign.Center),
                value = item.text,
                size = 14.px,
            )
        }
    }
}
