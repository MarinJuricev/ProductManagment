package parking.crewManagement.edit

import arrow.core.Either.Left
import arrow.core.Either.Right
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import core.utils.UUIDProvider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.product.inventory.shared.MR
import org.product.inventory.utils.dictionary.Dictionary
import parking.crewManagement.edit.interaction.EditUserSheetEvent
import parking.crewManagement.edit.interaction.EditUserSheetEvent.EmailChanged
import parking.crewManagement.edit.interaction.EditUserSheetEvent.GarageAccessChanged
import parking.crewManagement.edit.interaction.EditUserSheetEvent.RoleChanged
import parking.crewManagement.edit.interaction.EditUserSheetEvent.SaveClick
import parking.crewManagement.edit.interaction.EditUserSheetEvent.SheetShown
import parking.crewManagement.edit.interaction.EditUserSheetState
import parking.crewManagement.edit.interaction.EditUserSheetViewEffect
import parking.crewManagement.edit.interaction.EditUserSheetViewEffect.CloseDetails
import parking.crewManagement.edit.interaction.EditUserSheetViewEffect.ShowMessage
import parking.crewManagement.edit.mapper.EditUserEditableFields
import parking.crewManagement.edit.mapper.EditUserSheetUiMapper
import parking.crewManagement.edit.mapper.EditUserUiTextsMapper
import user.model.InventoryAppRole
import user.model.InventoryAppUser
import user.usecase.CreateUser
import user.usecase.UpdateUser

class EditUserBottomSheetViewModel(
    uiTextsMapper: EditUserUiTextsMapper,
    private val uiMapper: EditUserSheetUiMapper,
    private val updateUser: UpdateUser,
    private val createUser: CreateUser,
    private val uuidProvider: UUIDProvider,
    val dictionary: Dictionary,
) : ScreenModel {

    private val _selectedUser = MutableStateFlow<InventoryAppUser?>(null)
    private val _editableFields = MutableStateFlow(EditUserEditableFields())
    private val _uiTexts = MutableStateFlow(uiTextsMapper.map())
    private val _saveButtonLoading = MutableStateFlow<Boolean?>(null)
    private val _viewEffect = Channel<EditUserSheetViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<EditUserSheetViewEffect> = _viewEffect.receiveAsFlow()

    val uiState = combine(
        _selectedUser,
        _editableFields,
        _saveButtonLoading,
        _uiTexts,
        uiMapper::map,
    ).stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = EditUserSheetState.Loading,
    )

    fun onEvent(event: EditUserSheetEvent) {
        when (event) {
            is SheetShown -> _selectedUser.update { event.user }
            is GarageAccessChanged -> _editableFields.update { it.copy(permanentGarageAccess = event.hasPermanentAccess) }
            is RoleChanged -> _editableFields.update { it.copy(role = event.newRole) }
            is SaveClick -> handleSave(
                email = event.email,
                newRole = event.role,
                newAccessGrant = event.hasPermanentAccess,
            )

            is EmailChanged -> _editableFields.update { it.copy(email = event.email) }
        }
    }

    private fun handleSave(
        email: String,
        newRole: InventoryAppRole,
        newAccessGrant: Boolean,
    ) = screenModelScope.launch {
        _saveButtonLoading.update { true }
        _selectedUser.value?.let {
            updateExistingUser(
                selectedUser = it,
                newRole = newRole,
                newAccessGrant = newAccessGrant,
            )
        }
            ?: createNewUser(
                email = email,
                newRole = newRole,
                newAccessGrant = newAccessGrant,
            )
        _saveButtonLoading.update { false }
        _viewEffect.send(CloseDetails)
    }

    private suspend fun updateExistingUser(
        selectedUser: InventoryAppUser,
        newRole: InventoryAppRole,
        newAccessGrant: Boolean,
    ) {
        when (
            val result = updateUser(
                selectedUser.copy(role = newRole, hasPermanentGarageAccess = newAccessGrant),
            )
        ) {
            is Left -> _viewEffect.send(ShowMessage(result.value.toString()))
            is Right -> _viewEffect.send(ShowMessage(dictionary.getString(MR.strings.crew_management_form_add_edit)))
        }
    }

    private suspend fun createNewUser(
        email: String,
        newRole: InventoryAppRole,
        newAccessGrant: Boolean,
    ) {
        when (
            val result = createUser(
                InventoryAppUser(
                    id = uuidProvider.generateUUID(),
                    email = email,
                    role = newRole,
                    hasPermanentGarageAccess = newAccessGrant,
                    profileImageUrl = "",
                ),
            )
        ) {
            is Left -> _viewEffect.send(ShowMessage(result.value.toString()))
            is Right -> _viewEffect.send(ShowMessage(dictionary.getString(MR.strings.crew_management_form_add_success)))
        }
    }
}
