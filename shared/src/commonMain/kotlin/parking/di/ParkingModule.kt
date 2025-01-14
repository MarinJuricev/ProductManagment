package parking.di

import org.koin.dsl.bind
import org.koin.dsl.module
import parking.dashboard.GetParkingDashboard
import parking.reservation.CancelParkingPlaceRequest
import parking.reservation.CancelParkingPlaceRequestByUser
import parking.reservation.CheckForDuplicatedLevelTitle
import parking.reservation.CheckForDuplicatedSpots
import parking.reservation.CheckInputFieldLength
import parking.reservation.CreateGarageLevel
import parking.reservation.DeleteGarageLevel
import parking.reservation.GetEmptyParkingSpots
import parking.reservation.GetGarageLevels
import parking.reservation.IsUserAbleToMakeReservation
import parking.reservation.MakeReservation
import parking.reservation.ManageParkingRequest
import parking.reservation.MyParkingRequests
import parking.reservation.ObserveGarageLevelsData
import parking.reservation.ObserveMyParkingRequests
import parking.reservation.RequestMultipleParkingPlaces
import parking.reservation.RequestParkingPlace
import parking.reservation.UpdateGarageLevel
import parking.reservation.UpdateInvalidUserRequests
import parking.reservation.repository.GarageLevelRepository
import parking.reservation.repository.GarageLevelRepositoryImpl
import parking.reservation.repository.ParkingReservationRepository
import parking.reservation.repository.ParkingReservationRepositoryImpl
import parking.reservation.validator.GarageLevelValidator
import parking.reservation.validator.GarageLevelValidatorImpl
import parking.templates.GetTemplates
import parking.templates.SaveTemplates
import parking.templates.UpdateTemplate
import parking.templates.repository.TemplatesRepository
import parking.templates.repository.TemplatesRepositoryImpl
import parking.usersRequests.GetUsersRequests
import parking.usersRequests.ObserveUserRequests
import parking.usersRequests.UpdateUserRequest
import parking.usersRequests.repository.UsersRequestsRepository
import parking.usersRequests.repository.UsersRequestsRepositoryImpl

fun parkingModule() = module {
    factory { RequestParkingPlace(get()) }
    factory { MakeReservation(get(), get(), get()) }
    factory { ManageParkingRequest(get(), get(), get(), get(), get(), get()) }
    factory { ParkingReservationRepositoryImpl(get()) } bind ParkingReservationRepository::class
    factory { MyParkingRequests(get(), get()) }
    factory { GarageLevelRepositoryImpl(get()) } bind GarageLevelRepository::class
    factory { GetEmptyParkingSpots(get(), get(), get()) }
    factory { CreateGarageLevel(get(), get(), get(), get(), get()) }
    factory { UpdateGarageLevel(get(), get(), get(), get(), get(), get()) }
    factory { DeleteGarageLevel(get(), get(), get()) }
    factory { CancelParkingPlaceRequestByUser(get(), get()) }
    factory { GetParkingDashboard(get()) }
    factory { GetUsersRequests(get(), get()) }
    factory { UsersRequestsRepositoryImpl(get()) } bind UsersRequestsRepository::class
    factory { UpdateUserRequest(get(), get(), get()) }
    factory { GetGarageLevels(get(), get()) }
    factory { ObserveMyParkingRequests(get(), get()) }
    factory { ObserveUserRequests(get(), get()) }
    factory { ObserveGarageLevelsData(get(), get()) }
    factory { TemplatesRepositoryImpl(get()) } bind TemplatesRepository::class
    factory { GetTemplates(get(), get()) }
    factory { SaveTemplates(get(), get()) }
    factory { CheckForDuplicatedSpots() }
    factory { CheckForDuplicatedLevelTitle(get()) }
    factory { UpdateInvalidUserRequests(get(), get(), get(), get(), get()) }
    factory { CancelParkingPlaceRequest(get(), get()) }
    factory { UpdateTemplate(get(), get()) }
    factory { IsUserAbleToMakeReservation() }
    factory { GarageLevelValidatorImpl() } bind GarageLevelValidator::class
    factory { CheckInputFieldLength() }
    factory { RequestMultipleParkingPlaces(get()) }
}
