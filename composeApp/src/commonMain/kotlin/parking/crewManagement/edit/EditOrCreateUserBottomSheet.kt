package parking.crewManagement.edit

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import components.BodyLargeText
import components.BodyMediumText
import components.BodySmallText
import components.DropdownPickerForm
import components.GeneralUnknownError
import components.Image
import components.ImageType
import components.InventoryBigButton
import components.LoadingIndicator
import components.SwitchForm
import components.TextFieldForm
import org.product.inventory.SecondaryColor
import org.product.inventory.shared.MR
import parking.crewManagement.edit.interaction.EditUserSheetEvent
import parking.crewManagement.edit.interaction.EditUserSheetEvent.EmailChanged
import parking.crewManagement.edit.interaction.EditUserSheetEvent.GarageAccessChanged
import parking.crewManagement.edit.interaction.EditUserSheetEvent.RoleChanged
import parking.crewManagement.edit.interaction.EditUserSheetEvent.SaveClick
import parking.crewManagement.edit.interaction.EditUserSheetEvent.SheetShown
import parking.crewManagement.edit.interaction.EditUserSheetState.Content
import parking.crewManagement.edit.interaction.EditUserSheetState.Error
import parking.crewManagement.edit.interaction.EditUserSheetState.Loading
import parking.crewManagement.edit.interaction.EditUserSheetViewEffect.CloseDetails
import parking.crewManagement.edit.interaction.EditUserSheetViewEffect.ShowMessage
import user.model.InventoryAppUser

class EditOrCreateUserBottomSheet(
    private val selectedUser: InventoryAppUser? = null,
) : Screen {
    @Composable
    override fun Content() {
        val viewModel: EditUserBottomSheetViewModel = koinScreenModel()
        val context = LocalContext.current
        val navigator = LocalBottomSheetNavigator.current

        LaunchedEffect(Unit) {
            viewModel.onEvent(SheetShown(selectedUser))
            viewModel.viewEffect.collect {
                when (it) {
                    is CloseDetails -> navigator.hide()
                    is ShowMessage -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        when (val uiState = viewModel.uiState.collectAsState().value) {
            is Content -> EditUserContent(uiState = uiState, onEvent = viewModel::onEvent)
            is Error -> GeneralUnknownError()
            is Loading -> LoadingIndicator()
        }
    }
}

@Composable
private fun EditUserContent(
    uiState: Content,
    onEvent: (EditUserSheetEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        BodyLargeText(text = uiState.sheetTitle, fontWeight = SemiBold)
        Image(
            imageType = ImageType.Remote(uiState.userProfileImageUrl),
            modifier = Modifier.clip(CircleShape).size(64.dp)
                .border(width = 2.dp, shape = CircleShape, color = SecondaryColor),
        )
        TextFieldForm(
            formTitle = uiState.emailFieldIdentifier,
            value = uiState.userEmail,
            onValueChanged = { onEvent(EmailChanged(it)) },
            enabled = uiState.emailFieldEnabled,
            errorMessage = uiState.emailFieldError,
        )
        DropdownPickerForm(
            formTitle = uiState.rolePickerIdentifier,
            menuItems = uiState.availableRoles,
            selectedItem = uiState.selectedRole,
            onItemClick = {
                it?.let { role -> onEvent(RoleChanged(newRole = role)) }
            },
        ) {
            BodySmallText(
                modifier = Modifier.widthIn(min = 100.dp).padding(vertical = 8.dp),
                text = it.toString(),
                color = MR.colors.secondary,
                fontWeight = SemiBold,
            )
        }
        SwitchForm(
            formTitle = uiState.garageAccessIdentifier,
            switchActive = uiState.permanentGarageAccess,
            onCheckedChange = { onEvent(GarageAccessChanged(it)) },
        )

        AnimatedVisibility(uiState.saveButtonVisible) {
            InventoryBigButton(
                isLoading = uiState.saveButtonLoading,
                onClick = {
                    onEvent(
                        SaveClick(
                            email = uiState.userEmail,
                            role = uiState.selectedRole,
                            hasPermanentAccess = uiState.permanentGarageAccess,
                        ),
                    )
                },
            ) {
                BodyMediumText(
                    modifier = Modifier.padding(8.dp),
                    text = uiState.saveButtonText,
                    fontWeight = SemiBold,
                    color = MR.colors.surface,
                )
            }
        }
    }
}
