package org.product.inventory.web.pages.crewmanagement

import core.utils.UUIDProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import menu.model.MenuOption
import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.clearAfterDelay
import org.product.inventory.web.core.AlertMessageMapper
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.models.InventoryAppRoleUi
import org.product.inventory.web.models.toInventoryAppRole
import org.product.inventory.web.models.toInventoryAppRoleUi
import org.product.inventory.web.pages.Routes
import parking.role.ObserveScreenUnavailability
import user.model.CreateUserError.CreateUserFailed
import user.model.CreateUserError.DuplicatedUser
import user.model.CreateUserError.InvalidEmail
import user.model.InventoryAppRole
import user.model.InventoryAppUser
import user.model.UserError
import user.model.UserError.StoreUserError
import user.model.UserError.Unauthorized
import user.model.UserError.UnknownError
import user.model.UserError.UserNotFound
import user.usecase.CreateUser
import user.usecase.ObserveUsers
import user.usecase.UpdateUser

class CrewManagementViewModel(
    private val scope: CoroutineScope,
    private val uiMapper: CrewManagementUiMapper,
    private val updateUser: UpdateUser,
    private val createUser: CreateUser,
    private val alertMessageMapper: AlertMessageMapper,
    private val dictionary: Dictionary,
    private val uuidProvider: UUIDProvider,
    observeScreenUnavailability: ObserveScreenUnavailability,
    observeUsers: ObserveUsers,
) {

    private val _routeToNavigate = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(true)
    private val _alertMessage = MutableStateFlow<AlertMessage?>(null)
    private val _closeDetailsDialog = MutableStateFlow(false)

    val state = combine(
        _isLoading,
        observeUsers().onEach { _isLoading.update { false } },
        _routeToNavigate,
        _alertMessage,
        _closeDetailsDialog,
        uiMapper::toUiState,
    ).stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = CrewManagementUiState(),
    )

    private val _userUpsertMode = MutableStateFlow<UserUpsertMode?>(null)
    private val _emailTemp = MutableStateFlow("")
    private val _roleTemp = MutableStateFlow<InventoryAppRoleUi?>(null)
    private val _hasPermanentGarageAccessTemp = MutableStateFlow(false)
    private val _detailsUpdating = MutableStateFlow(false)

    val detailsState = _userUpsertMode
        .onEach(::setInitialUserDetailsState)
        .flatMapLatest { upsertMode ->
            when (upsertMode) {
                null -> flowOf(null)
                else -> core.utils.combine(
                    flowOf((upsertMode as? UserUpsertMode.Edit)?.userId),
                    flowOf(provideUserIcon(upsertMode)),
                    _emailTemp,
                    _roleTemp.filterNotNull(),
                    _hasPermanentGarageAccessTemp,
                    _detailsUpdating,
                    uiMapper::toDetailsUiState,
                )
            }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = null,
        )

    init {
        observeScreenUnavailability(MenuOption.CrewManagement.requiredRole)
            .onEach {
                _closeDetailsDialog.update { true }
                _routeToNavigate.update { Routes.parkingReservation }
            }
            .launchIn(scope)
    }

    fun onEvent(event: CrewManagementEvent) {
        when (event) {
            is CrewManagementEvent.PathClick -> _routeToNavigate.update { event.path }
            is CrewManagementEvent.UserClick -> _userUpsertMode.update { UserUpsertMode.Edit(event.userId) }
            is CrewManagementEvent.EmailChanged -> _emailTemp.update { event.email }
            is CrewManagementEvent.HasPermanentGarageAccessChanged -> _hasPermanentGarageAccessTemp.update { event.hasPermanentGarageAccess }
            is CrewManagementEvent.RoleChanged -> _roleTemp.update { event.role }
            CrewManagementEvent.SaveClick -> scope.launch { upsertUser() }
            CrewManagementEvent.AddUserClick -> _userUpsertMode.update { UserUpsertMode.New }
            CrewManagementEvent.DetailsDialogClosed -> clearDetailsData()
        }
    }

    private fun clearDetailsData() {
        _userUpsertMode.update { null }
        _emailTemp.update { "" }
        _roleTemp.update { null }
        _hasPermanentGarageAccessTemp.update { false }
        _detailsUpdating.update { false }
        _closeDetailsDialog.update { false }
    }

    private suspend fun upsertUser() {
        val upsertMode = _userUpsertMode.value ?: return
        val detailsState = detailsState.value ?: return

        _detailsUpdating.update { true }

        val result = when (upsertMode) {
            is UserUpsertMode.New -> createUser(createInventoryAppUserFromState(detailsState)).mapLeft { error ->
                when (error) {
                    DuplicatedUser -> dictionary.get(StringRes.crewManagementDuplicatedEmailErrorMessage)
                    CreateUserFailed, InvalidEmail -> dictionary.get(StringRes.slotsManagementRequestFailure)
                }
            }

            is UserUpsertMode.Edit -> updateUser(createInventoryAppUserFromState(detailsState)).mapLeft { error ->
                when (error) {
                    is StoreUserError -> error.message
                    UserError.InvalidEmail, Unauthorized, UnknownError, UserNotFound ->
                        dictionary.get(StringRes.slotsManagementRequestFailure)
                }
            }
        }

        _detailsUpdating.update { false }
        _closeDetailsDialog.update { true }

        _alertMessage.update {
            alertMessageMapper.map(
                isSuccess = result.isRight(),
                successMessage = provideSuccessMessage(upsertMode),
                failureMessage = result.leftOrNull(),
            )
        }

        _alertMessage.clearAfterDelay()
    }

    private fun createInventoryAppUserFromState(state: CrewManagementUserDetailsState) =
        InventoryAppUser(
            id = state.userId ?: uuidProvider.generateUUID(),
            email = state.email,
            role = state.role.toInventoryAppRole(),
            hasPermanentGarageAccess = state.hasPermanentGarageAccess,
            profileImageUrl = state.profileIcon.orEmpty(),
        )

    private fun setInitialUserDetailsState(mode: UserUpsertMode?) {
        when (mode) {
            null -> return
            is UserUpsertMode.New -> {
                _emailTemp.update { "" }
                _roleTemp.update { InventoryAppRole.User.toInventoryAppRoleUi(dictionary) }
                _hasPermanentGarageAccessTemp.update { false }
            }

            is UserUpsertMode.Edit -> {
                val userId = mode.userId
                val user = state.value.users
                    .firstOrNull { it.id == userId }
                    ?: return
                _emailTemp.update { user.email }
                _roleTemp.update { user.roleUi }
                _hasPermanentGarageAccessTemp.update { user.hasPermanentGarageAccess }
            }
        }
    }

    private fun provideUserIcon(upsertMode: UserUpsertMode) =
        (upsertMode as? UserUpsertMode.Edit)
            ?.let { mode -> state.value.users.firstOrNull { user -> user.id == mode.userId } }
            ?.profileInfo?.icon

    private fun provideSuccessMessage(upsertMode: UserUpsertMode) = when (upsertMode) {
        is UserUpsertMode.Edit -> dictionary.get(StringRes.crewManagementRequestEditUserSuccess)
        UserUpsertMode.New -> dictionary.get(StringRes.crewManagementRequestAddUserSuccess)
    }
}
