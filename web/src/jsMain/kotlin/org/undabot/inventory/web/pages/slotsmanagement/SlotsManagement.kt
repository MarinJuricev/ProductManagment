package org.product.inventory.web.pages.slotsmanagement

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.WordBreak
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.alignItems
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flex
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.modifiers.wordBreak
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.forms.TextInput
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.product.inventory.shared.MR
import org.product.inventory.web.components.CircleButton
import org.product.inventory.web.components.CircularProgressBar
import org.product.inventory.web.components.ConfirmationDialog
import org.product.inventory.web.components.ContentWithBadge
import org.product.inventory.web.components.Dialog
import org.product.inventory.web.components.LoadingButton
import org.product.inventory.web.components.OneLineText
import org.product.inventory.web.components.PopUpMenu
import org.product.inventory.web.components.Text
import org.product.inventory.web.components.TextWithOverflow
import org.product.inventory.web.components.TitleWithPath
import org.product.inventory.web.components.clickable
import org.product.inventory.web.components.closeDialog
import org.product.inventory.web.components.hideModalOnClick
import org.product.inventory.web.components.roundedCornersShape
import org.product.inventory.web.components.showModalOnClick
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.toCssColor

@Composable
fun SlotsManagement(
    state: SlotsManagementUiState,
    detailsState: GarageLevelDetailsState?,
    onEvent: (SlotsManagementEvent) -> Unit,
) {
    if (state.closeAddOrEditDialog) {
        closeDialog(ADD_OR_EDIT_GARAGE_LEVEL_DIALOG_ID)
    }

    if (state.closeDeleteDialog) {
        closeDialog(DELETE_GARAGE_LEVEL_DIALOG_ID)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TitleWithPath(
                items = state.breadcrumbItems,
                title = state.title,
                onPathClick = { onEvent(SlotsManagementEvent.PathClick(it)) },
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressBar()
                }
            } else {
                SlotList(
                    items = state.items,
                    createGarageLevelLabel = state.createNewGarageLevelLabel,
                    popUpOptions = state.popupOptions,
                    onEvent = onEvent,
                )
            }
        }

        ConfirmationDialog(
            id = DELETE_GARAGE_LEVEL_DIALOG_ID,
            dialogState = state.deleteGarageLevelDialogState,
            isLoading = state.isDeleteActionInProgress,
            closableOutside = false,
            onPositiveClick = { onEvent(SlotsManagementEvent.OnPositiveClickDeleteDialog) },
            onNegativeClick = { onEvent(SlotsManagementEvent.OnNegativeClickDeleteDialog) },
            onClose = { onEvent(SlotsManagementEvent.DeleteDialogClosed) },
        )

        DetailsDialog(
            detailsState = detailsState,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun ColumnScope.SlotList(
    items: List<GarageLevelDataUi>,
    createGarageLevelLabel: String,
    popUpOptions: List<SpotPopupOptionUi>,
    onEvent: (SlotsManagementEvent) -> Unit,
) {
    SimpleGrid(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally)
            .gap(24.px)
            .padding(topBottom = 20.px, leftRight = 10.px),
        numColumns = numColumns(
            base = 1,
            sm = 1,
            md = 2,
            lg = 2,
            xl = 3,
        ),
    ) {
        CreateGarageLevelItem(
            onEvent = onEvent,
            text = createGarageLevelLabel,
        )

        items.forEach {
            GarageLevelItem(
                item = it,
                popupOptions = popUpOptions,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun GarageLevelItem(
    item: GarageLevelDataUi,
    popupOptions: List<SpotPopupOptionUi>,
    modifier: Modifier = Modifier,
    onEvent: (SlotsManagementEvent) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(90.percent)
                .minHeight(300.px)
                .align(alignment = Alignment.Center)
                .roundedCornersShape(20.px)
                .background(color = Colors.White),
            verticalArrangement = Arrangement.Top,
        ) {
            Div(
                attrs = Modifier.fillMaxWidth()
                    .display(DisplayStyle.Flex)
                    .position(Position.Relative)
                    .alignItems(AlignItems.Center)
                    .toAttrs(),
            ) {
                Column(
                    modifier = Modifier
                        .background(color = MR.colors.secondary.toCssColor())
                        .size(60.px)
                        .flex("0 1 auto")
                        .roundedCornersShape(topLeft = 20.px, bottomRight = 20.px),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        value = item.levelLabel,
                        size = 16.px,
                        modifier = Modifier
                            .color(Colors.White)
                            .textAlign(TextAlign.Center)
                            .fontWeight(400),
                    )
                    TextWithOverflow(
                        value = item.title,
                        size = 16.px,
                        modifier = Modifier
                            .color(Colors.White)
                            .fontWeight(FontWeight.SemiBold)
                            .textAlign(TextAlign.Center),
                    )
                }

                Box(modifier = Modifier.flex(1))

                Span(
                    attrs = Modifier
                        .margin(leftRight = 20.px)
                        .textAlign(TextAlign.Center)
                        .toAttrs(),
                ) {
                    OneLineText(
                        modifier = Modifier.fontWeight(FontWeight.SemiBold),
                        value = item.registeredSpotsLabel,
                        size = 20.px,
                    )

                    OneLineText(
                        modifier = Modifier
                            .fontWeight(FontWeight.SemiBold)
                            .color(MR.colors.secondary.toCssColor()),
                        value = item.spots.size.toString(),
                        size = 20.px,
                    )
                }

                Box(modifier = Modifier.flex(1))

                PopUpMenu(
                    items = popupOptions,
                    modifier = Modifier.flex("0 1 auto").margin(right = 10.px),
                    itemContent = { _, popupItem, modifier ->
                        Row(
                            modifier = modifier
                                .fillMaxSize()
                                .showModalOnClick(
                                    when (popupItem.option) {
                                        SpotPopupOption.EDIT -> ADD_OR_EDIT_GARAGE_LEVEL_DIALOG_ID
                                        SpotPopupOption.DELETE -> DELETE_GARAGE_LEVEL_DIALOG_ID
                                    },
                                )
                                .clickable {
                                    when (popupItem.option) {
                                        SpotPopupOption.EDIT -> onEvent(SlotsManagementEvent.OnAddOrEditClick(item.id))
                                        SpotPopupOption.DELETE -> onEvent(SlotsManagementEvent.OnDeleteClick(item))
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
                    },
                )
            }

            SimpleGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .gap(16.px)
                    .padding(leftRight = 14.px, topBottom = 20.px),
                numColumns = numColumns(3),
            ) {
                item.spots.forEach {
                    SpotItem(text = it.displayedTitle)
                }
            }
        }
    }
}

@Composable
private fun SpotItem(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .roundedCornersShape(9.px)
            .padding(8.px)
            .background(color = MR.colors.secondary.toCssColor()),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            value = text,
            size = 12.px,
            modifier = Modifier
                .color(Colors.White)
                .fontWeight(FontWeight.SemiBold)
                .wordBreak(WordBreak.BreakAll)
                .maxWidth(100.px),
        )
    }
}

@Composable
private fun CreateGarageLevelItem(
    text: String,
    modifier: Modifier = Modifier,
    onEvent: (SlotsManagementEvent) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(90.percent)
                .minHeight(300.px)
                .roundedCornersShape(20.px)
                .background(color = Colors.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircleButton(
                    radius = 40,
                    modifier = Modifier.showModalOnClick(ADD_OR_EDIT_GARAGE_LEVEL_DIALOG_ID),
                    onClick = { onEvent(SlotsManagementEvent.OnAddOrEditClick()) },
                    content = {
                        Image(
                            src = ImageRes.addIcon,
                            modifier = Modifier.size(30.px),
                        )
                    },
                )

                Text(
                    value = text,
                    size = 20.px,
                    modifier = Modifier
                        .margin(top = 10.px)
                        .color(MR.colors.textBlack.toCssColor())
                        .fontWeight(FontWeight.SemiBold),
                )
            }
        }
    }
}

@Composable
private fun DetailsDialog(
    detailsState: GarageLevelDetailsState?,
    onEvent: (SlotsManagementEvent) -> Unit,
) {
    Dialog(
        id = ADD_OR_EDIT_GARAGE_LEVEL_DIALOG_ID,
        onClose = { onEvent(SlotsManagementEvent.DetailsClosed) },
        content = { contentModifier ->
            detailsState?.let { state ->
                Column(
                    modifier = contentModifier.fillMaxSize().padding(30.px),
                ) {
                    Image(
                        modifier = Modifier
                            .hideModalOnClick()
                            .cursor(Cursor.Pointer)
                            .padding(bottom = 20.px),
                        src = ImageRes.backIcon,
                    )

                    DetailsDialogLabelText(
                        value = state.titleLabel,
                        modifier = Modifier.margin(bottom = 10.px),
                    )
                    TextInput(
                        text = state.title,
                        modifier = Modifier.fillMaxWidth().margin(bottom = 20.px),
                        onTextChanged = { onEvent(SlotsManagementEvent.OnGarageTitleChanged(it)) },
                        placeholder = state.titlePlaceholder,
                        enabled = !state.isLoading,
                    )

                    DetailsDialogLabelText(
                        value = state.spotsLabel,
                        modifier = Modifier.margin(bottom = 10.px),
                    )
                    SimpleGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                            .gap(24.px)
                            .padding(20.px)
                            .margin(bottom = 20.px),
                        numColumns = numColumns(3),
                    ) {
                        state.spots.forEach { parkingSpot ->
                            ContentWithBadge(
                                content = {
                                    SpotItem(text = parkingSpot.displayedTitle)
                                },
                                badgeContent = { modifier ->
                                    Image(
                                        modifier = modifier
                                            .clickable { onEvent(SlotsManagementEvent.OnDeleteSpotClick(parkingSpot)) },
                                        src = ImageRes.closeIcon,
                                    )
                                },
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(topBottom = 10.px)
                            .margin(bottom = 20.px),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextInput(
                            modifier = Modifier.weight(1).margin(right = 20.px),
                            text = state.newSpotName,
                            onTextChanged = { onEvent(SlotsManagementEvent.OnNewSpotNameChanged(it)) },
                            placeholder = state.newSpotNamePlaceholder,
                            enabled = !state.isLoading,
                        )

                        CircleButton(
                            radius = 30,
                            onClick = { onEvent(SlotsManagementEvent.OnAddNewSpotClick) },
                            enabled = state.newSpotNameValid && !state.isLoading,
                            content = {
                                Image(
                                    src = ImageRes.addIcon,
                                    modifier = Modifier.size(10.px),
                                )
                            },
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        LoadingButton(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(leftRight = 80.px),
                            onClick = {
                                onEvent(
                                    SlotsManagementEvent.OnSaveClick(
                                        title = state.title,
                                        spots = state.spots,
                                    ),
                                )
                            },
                            enabled = state.saveEnabled && !state.isLoading,
                            isLoading = state.isLoading,
                            content = {
                                Text(
                                    value = state.saveButtonText,
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
private fun DetailsDialogLabelText(
    value: String,
    modifier: Modifier = Modifier,
) {
    Text(
        value = value,
        size = 16.px,
        modifier = modifier
            .color(MR.colors.secondary.toCssColor())
            .fontWeight(FontWeight.SemiBold),
    )
}

private const val DELETE_GARAGE_LEVEL_DIALOG_ID = "deleteGarageLevelDialog"
private const val ADD_OR_EDIT_GARAGE_LEVEL_DIALOG_ID = "addOrEditGarageLevelDialog"
