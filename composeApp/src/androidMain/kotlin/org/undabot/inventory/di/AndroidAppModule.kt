package org.product.inventory.di

import MyReservationsScreenMapper
import UsersRequestsScreenMapper
import auth.AuthScreen
import auth.AuthScreenViewModel
import auth.mapper.AuthScreenMapper
import home.HomeScreenViewModel
import home.mapper.HomeScreenMapper
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.product.inventory.utils.GetGoogleCredential
import org.product.inventory.utils.dictionary.Dictionary
import org.product.inventory.utils.dictionary.DictionaryImpl
import parking.crewManagement.CrewManagementViewModel
import parking.crewManagement.edit.EditUserBottomSheetViewModel
import parking.crewManagement.edit.mapper.EditUserSheetUiMapper
import parking.crewManagement.edit.mapper.EditUserUiTextsMapper
import parking.crewManagement.mapper.CrewManagementScreenMapper
import parking.dashboard.ParkingDashboardViewModel
import parking.dashboard.mapper.ParkingDashboardScreenMapper
import parking.emailTemplates.EmailTemplatesViewModel
import parking.emailTemplates.details.EmailTemplateDetailsViewModel
import parking.emailTemplates.details.mapper.DiscardChangesDialogMapper
import parking.emailTemplates.details.mapper.EmailTemplateDetailsUiMapper
import parking.myReservations.MyReservationsScreenViewModel
import parking.myReservations.details.MyReservationDetailsViewModel
import parking.myReservations.details.mapper.CancelParkingReservationQuestionDialogMapper
import parking.myReservations.details.mapper.MyReservationDetailsUiMapper
import parking.myReservations.details.mapper.ParkingReservationErrorMapper
import parking.myReservations.details.mapper.ParkingReservationMapper
import parking.reservation.CreateParkingReservationViewModel
import parking.reservation.components.userpicker.UserPickerScreenViewModel
import parking.reservation.components.userpicker.mapper.UserPickerUiMapper
import parking.reservation.mapper.CreateParkingReservationUiMapper
import parking.reservation.mapper.MultiDateSelectionUiMapper
import parking.reservation.mapper.ParkingReservationStatusUiMapper
import parking.reservation.mapper.ParkingReservationUiModelMapper
import parking.reservation.mapper.ReservationDetailsScreenTextsMapper
import parking.slotsManagement.SlotsManagementViewModel
import parking.slotsManagement.levelCreator.LevelCreatorViewModel
import parking.slotsManagement.levelCreator.mapper.LevelCreatorTextsMapper
import parking.slotsManagement.levelCreator.mapper.LevelCreatorUiMapper
import parking.slotsManagement.levelCreator.mapper.LevelNameStepUiMapper
import parking.slotsManagement.levelCreator.mapper.LevelSpotsStepUiMapper
import parking.slotsManagement.mapper.SlotsManagementUiMapper
import parking.templates.GetTemplateById
import parking.usersRequests.UsersRequestsScreenViewModel
import parking.usersRequests.details.ParkingReservationDetailsViewModel
import parking.usersRequests.details.interaction.ParkingReservationDetailsFieldsValidator
import parking.usersRequests.details.mapper.ParkingReservationDetailsScreenMapper
import parking.usersRequests.mapper.DateRangePickerUiMapper
import parking.usersRequests.screenComponent.header.datepicker.DateRangePickerViewModel
import parking.usersRequests.screenComponent.header.filter.FilterViewModel
import parking.usersRequests.screenComponent.header.filter.mapper.FilterStateUiMapper
import seat.dashboard.SeatReservationDashboardViewModel
import seat.management.SeatManagementViewModel
import seat.management.mapper.DeleteOfficeDialogMapper
import seat.management.mapper.EditOfficeDialogMapper
import seat.management.mapper.SeatManagementScreenUiMapper
import seat.management.mapper.SeatManagementTextMapper
import seat.timeline.SeatReservationTimelineViewModel
import seat.timeline.mapper.ReservableDateUiItemMapper
import seat.timeline.mapper.SeatReservationTimelineUiMapper
import splash.di.splashModule
import utils.ValidateDateRange

fun androidAppModule() = module {
    includes(authModule)
    includes(dashboardModule)
    includes(splashModule)
    includes(parkingModule)
    includes(seatReservationModule)
    factoryOf(::DictionaryImpl) bind Dictionary::class
}

private val authModule = module {
    factoryOf(::GetGoogleCredential)
    factoryOf(::AuthScreen)
    factoryOf(::AuthScreenMapper)
    factoryOf(::AuthScreenViewModel)
}

private val dashboardModule = module {
    singleOf(::HomeScreenViewModel)
    factoryOf(::HomeScreenMapper)
}

private val parkingModule = module {
    factoryOf(::ParkingDashboardViewModel)
    factoryOf(::ParkingDashboardScreenMapper)
    factoryOf(::CreateParkingReservationViewModel)
    factoryOf(::UsersRequestsScreenViewModel)
    factoryOf(::UsersRequestsScreenMapper)
    factoryOf(::ParkingReservationUiModelMapper)
    factoryOf(::ValidateDateRange)
    factoryOf(::ParkingReservationDetailsViewModel)
    factoryOf(::ParkingReservationDetailsScreenMapper)
    factoryOf(::ReservationDetailsScreenTextsMapper)
    factoryOf(::ParkingReservationStatusUiMapper)
    factoryOf(::DateRangePickerViewModel)
    factoryOf(::DateRangePickerUiMapper)
    factoryOf(::ParkingReservationDetailsFieldsValidator)
    factoryOf(::MyReservationsScreenViewModel)
    factoryOf(::MyReservationsScreenMapper)
    factoryOf(::MyReservationDetailsUiMapper)
    factoryOf(::MyReservationDetailsViewModel)
    factoryOf(::ParkingReservationErrorMapper)
    factoryOf(::ParkingReservationMapper)
    factoryOf(::LevelCreatorViewModel)
    factoryOf(::LevelCreatorUiMapper)
    factoryOf(::LevelNameStepUiMapper)
    factoryOf(::LevelSpotsStepUiMapper)
    factoryOf(::CrewManagementViewModel)
    factoryOf(::CrewManagementScreenMapper)
    factoryOf(::SlotsManagementViewModel)
    factoryOf(::SlotsManagementUiMapper)
    factoryOf(::LevelCreatorTextsMapper)
    factoryOf(::EmailTemplatesViewModel)
    factoryOf(::EmailTemplateDetailsViewModel)
    factoryOf(::GetTemplateById)
    factoryOf(::EmailTemplateDetailsUiMapper)
    factoryOf(::DiscardChangesDialogMapper)
    factoryOf(::EmailTemplatesViewModel)
    factoryOf(::EditUserBottomSheetViewModel)
    factoryOf(::EditUserSheetUiMapper)
    factoryOf(::EditUserUiTextsMapper)
    factoryOf(::CancelParkingReservationQuestionDialogMapper)
    factoryOf(::CreateParkingReservationUiMapper)
    factoryOf(::UserPickerScreenViewModel)
    factoryOf(::UserPickerUiMapper)
    factoryOf(::MultiDateSelectionUiMapper)
}

private val seatReservationModule = module {
    factoryOf(::SeatReservationDashboardViewModel)
    factoryOf(::SeatManagementViewModel)
    factoryOf(::SeatManagementTextMapper)
    factoryOf(::SeatManagementScreenUiMapper)
    factoryOf(::DeleteOfficeDialogMapper)
    factoryOf(::FilterViewModel)
    factoryOf(::FilterStateUiMapper)
    factoryOf(::SeatReservationTimelineViewModel)
    factoryOf(::EditOfficeDialogMapper)
    factoryOf(::SeatReservationTimelineUiMapper)
    factoryOf(::ReservableDateUiItemMapper)
}
