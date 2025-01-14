package org.product.inventory.web.pages.crewmanagement

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.Switch
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.product.inventory.shared.MR
import org.product.inventory.web.components.Dialog
import org.product.inventory.web.components.Dropdown
import org.product.inventory.web.components.LoadingButton
import org.product.inventory.web.components.ProfileImage
import org.product.inventory.web.components.ProfileInfo
import org.product.inventory.web.components.Text
import org.product.inventory.web.components.TextInputWithErrorMessage
import org.product.inventory.web.components.TextWithOverflow
import org.product.inventory.web.components.TitleWithPath
import org.product.inventory.web.components.closeDialog
import org.product.inventory.web.components.hideModalOnClick
import org.product.inventory.web.components.provideCustomColorScheme
import org.product.inventory.web.components.roundedCornersShape
import org.product.inventory.web.components.showModalOnClick
import org.product.inventory.web.components.spaceBetween
import org.product.inventory.web.core.ImageRes
import org.product.inventory.web.models.InventoryAppRoleUi
import org.product.inventory.web.toCssColor

val crewManagementTitleStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.fontSize(18.px)
    }

    Breakpoint.SM {
        Modifier.fontSize(25.px)
    }
}

val crewManagementButtonTextStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.fontSize(12.px)
    }

    Breakpoint.SM {
        Modifier.fontSize(16.px)
    }

    Breakpoint.MD {
        Modifier.fontSize(20.px)
    }
}

@Composable
fun CrewManagement(
    state: CrewManagementUiState,
    detailsState: CrewManagementUserDetailsState?,
    onEvent: (CrewManagementEvent) -> Unit,
) {
    if (state.closeDetailsDialog) {
        closeDialog(USER_DETAILS_DIALOG_ID)
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
                onPathClick = { onEvent(CrewManagementEvent.PathClick(it)) },
            )

            UserList(
                state = state,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .margin(bottom = 40.px)
                    .fillMaxWidth(percent = 70.percent)
                    .padding(top = 20.px, bottom = 50.px, leftRight = 20.px)
                    .background(Colors.White)
                    .roundedCornersShape(23.px),
                onEvent = onEvent,
            )
        }

        UserDetailsDialog(detailsState, onEvent)
    }
}

@Composable
private fun UserList(
    state: CrewManagementUiState,
    modifier: Modifier = Modifier,
    onEvent: (CrewManagementEvent) -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().spaceBetween().margin(bottom = 40.px),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = crewManagementTitleStyle
                    .toModifier()
                    .fontWeight(FontWeight.SemiBold)
                    .color(MR.colors.textBlack.toCssColor()),
                value = state.registeredUsersLabel,
            )

            Button(
                onClick = { onEvent(CrewManagementEvent.AddUserClick) },
                colorScheme = provideCustomColorScheme(MR.colors.secondary.toCssColor()),
                modifier = Modifier.showModalOnClick(USER_DETAILS_DIALOG_ID),
                content = {
                    Text(
                        value = state.addButtonText,
                        modifier = crewManagementButtonTextStyle
                            .toModifier()
                            .color(Colors.White),
                    )
                },
            )
        }

        SimpleGrid(
            modifier = Modifier.fillMaxSize().gap(columnGap = 24.px, rowGap = 0.px),
            numColumns = numColumns(
                base = 1,
                sm = 1,
                md = 2,
                lg = 2,
                xl = 2,
            ),
        ) {
            state.users.forEach { user ->
                ProfileInfo(
                    modifier = Modifier.fillMaxWidth()
                        .padding(10.px)
                        .roundedCornersShape(20.px)
                        .showModalOnClick(USER_DETAILS_DIALOG_ID),
                    profileInfo = user.profileInfo,
                    showRole = false,
                    imageSize = 45.px,
                    hoverColor = MR.colors.textLight.toCssColor(),
                    onClick = { onEvent(CrewManagementEvent.UserClick(user.id)) },
                )
            }
        }
    }
}

@Composable
private fun RoleDropdown(
    selectedRole: InventoryAppRoleUi,
    allRoles: List<InventoryAppRoleUi>,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onItemSelect: (InventoryAppRoleUi) -> Unit,
) {
    Dropdown(
        modifier = modifier,
        items = allRoles,
        enabled = enabled,
        defaultContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border {
                        color(MR.colors.textLight.toCssColor())
                        style(LineStyle.Solid)
                        width(1.px)
                    }
                    .borderRadius(12.px)
                    .padding(10.px),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.weight(1),
                    contentAlignment = Alignment.Center,
                ) {
                    TextWithOverflow(
                        modifier = Modifier
                            .fontSize(14.px)
                            .color(MR.colors.secondary.toCssColor())
                            .textAlign(TextAlign.Center),
                        value = selectedRole.text,
                    )
                }

                Image(
                    src = ImageRes.userRequestsStatusDropdown,
                    modifier = Modifier.size(20.px).margin(left = 20.px),
                )
            }
        },
        itemContent = { selectedItem, modifier ->
            Box(modifier = modifier.fillMaxWidth()) {
                TextWithOverflow(
                    modifier = Modifier
                        .fontSize(14.px)
                        .color(MR.colors.secondary.toCssColor())
                        .align(Alignment.Center)
                        .textAlign(TextAlign.Center),
                    value = selectedItem.text,
                )
            }
        },
        onItemSelect = { _, item -> onItemSelect(item) },
    )
}

@Composable
private fun UserDetailsDialog(
    detailsState: CrewManagementUserDetailsState?,
    onEvent: (CrewManagementEvent) -> Unit,
) {
    Dialog(
        id = USER_DETAILS_DIALOG_ID,
        onClose = { onEvent(CrewManagementEvent.DetailsDialogClosed) },
        content = { contentModifier ->
            detailsState?.let { state ->
                Column(modifier = contentModifier.fillMaxSize().padding(30.px)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .spaceBetween()
                            .margin(top = 10.px, bottom = 20.px),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            modifier = Modifier
                                .hideModalOnClick()
                                .cursor(Cursor.Pointer),
                            src = ImageRes.backIcon,
                        )

                        state.profileIcon?.let { icon ->
                            ProfileImage(
                                image = icon,
                                imageSize = 40.px,
                            )
                        }
                    }

                    LabelText(value = state.emailLabel, modifier = Modifier.margin(bottom = 10.px))
                    TextInputWithErrorMessage(
                        value = state.email,
                        errorText = state.emailErrorMessage,
                        modifier = Modifier.fillMaxWidth().margin(bottom = 20.px),
                        enabled = state.emailEditable && !state.isUpdating,
                        onValueChange = {
                            onEvent(CrewManagementEvent.EmailChanged(it))
                        },
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().spaceBetween().margin(bottom = 20.px),
                    ) {
                        LabelText(value = state.roleLabel)
                        RoleDropdown(
                            selectedRole = state.role,
                            allRoles = state.availableRoles,
                            enabled = !state.isUpdating,
                            onItemSelect = {
                                onEvent(CrewManagementEvent.RoleChanged(it))
                            },
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().spaceBetween().margin(bottom = 20.px),
                    ) {
                        LabelText(value = state.hasPermanentGarageAccessLabel)
                        Switch(
                            checked = state.hasPermanentGarageAccess,
                            enabled = !state.isUpdating,
                            onCheckedChange = {
                                onEvent(CrewManagementEvent.HasPermanentGarageAccessChanged(it))
                            },
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        LoadingButton(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            isLoading = state.isUpdating,
                            enabled = state.saveEnabled && !state.isUpdating,
                            content = {
                                Text(
                                    value = state.saveButtonText,
                                    modifier = Modifier.color(Colors.White),
                                )
                            },
                            onClick = {
                                onEvent(CrewManagementEvent.SaveClick)
                            },
                        )
                    }
                }
            }
        },
    )
}

@Composable
fun LabelText(
    value: String,
    modifier: Modifier = Modifier,
    fontSize: CSSLengthOrPercentageNumericValue? = 16.px,
) {
    Text(
        value = value,
        modifier = modifier
            .color(MR.colors.secondary.toCssColor())
            .fontWeight(FontWeight.SemiBold),
        size = fontSize,
    )
}

private const val USER_DETAILS_DIALOG_ID = "userDetailsDialog"
