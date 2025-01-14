package parking.reservation.components.userpicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import components.BodySmallText
import components.InventoryTextField
import components.LoadingIndicator
import kotlinx.coroutines.launch
import org.product.inventory.shared.MR
import parking.reservation.components.userpicker.components.UserItem
import parking.reservation.components.userpicker.interaction.UserPickerEffect.ClosePicker
import parking.reservation.components.userpicker.interaction.UserPickerEffect.SelectUser
import parking.reservation.components.userpicker.interaction.UserPickerEvent
import parking.reservation.components.userpicker.interaction.UserPickerEvent.CancelClick
import parking.reservation.components.userpicker.interaction.UserPickerEvent.SaveClick
import parking.reservation.components.userpicker.interaction.UserPickerEvent.SearchValueChange
import parking.reservation.components.userpicker.interaction.UserPickerEvent.UserClick
import parking.reservation.components.userpicker.interaction.UserPickerEvent.UserPreselected
import parking.reservation.components.userpicker.interaction.UserPickerState.Content
import parking.reservation.components.userpicker.interaction.UserPickerState.Loading
import user.model.InventoryAppUser
import user.model.getUsername
import utils.clickable

class UserPickerScreen(
    private val preselectedUser: InventoryAppUser,
    val onUserSelected: (InventoryAppUser) -> Unit,
    val onDismiss: () -> Unit,
) : Screen {
    @Composable
    override fun Content() {
        val viewModel: UserPickerScreenViewModel = koinScreenModel()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.onEvent(UserPreselected(preselectedUser))
            viewModel.viewEffect.collect {
                when (it) {
                    is ClosePicker -> onDismiss()
                    is SelectUser -> onUserSelected(it.user)
                }
            }
        }

        when (val state = uiState) {
            is Content -> UserPickerContent(state = state, onEvent = viewModel::onEvent)
            is Loading -> LoadingIndicator(modifier = Modifier.size(100.dp))
        }
    }
}

@Composable
private fun UserPickerContent(
    state: Content,
    modifier: Modifier = Modifier,
    onEvent: (UserPickerEvent) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxHeight(0.8f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SaveAndCancel(onEvent = onEvent, preselectedUser = state.preselectedUser)

        InventoryTextField(
            modifier = Modifier.focusRequester(focusRequester),
            value = state.searchValue,
            onValueChanged = {
                onEvent(SearchValueChange(it))
                scope.launch { listState.animateScrollToItem(0) }
            },
            placeholder = { BodySmallText(state.searchPlaceholderText) },
            shape = RoundedCornerShape(16.dp),
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = listState,
        ) {
            items(
                items = state.users,
                key = { it.id },
            ) {
                UserItem(
                    modifier = Modifier.fillMaxWidth(),
                    imageUrl = it.profileImageUrl,
                    username = it.getUsername(),
                    isSelected = it.id == state.preselectedUser.id,
                    onUserClick = { onEvent(UserClick(it)) },
                )
            }
        }
    }
}

@Composable
private fun SaveAndCancel(
    modifier: Modifier = Modifier,
    preselectedUser: InventoryAppUser,
    onEvent: (UserPickerEvent) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        BodySmallText(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable { onEvent(CancelClick) }.padding(8.dp),
            text = stringResource(MR.strings.slots_management_cancel_button.resourceId),
            color = MR.colors.secondary,
            fontWeight = SemiBold,
        )
        BodySmallText(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable { onEvent(SaveClick(preselectedUser)) }.padding(8.dp),
            text = stringResource(MR.strings.slots_management_save_button.resourceId),
            color = MR.colors.secondary,
            fontWeight = SemiBold,
        )
    }
}
