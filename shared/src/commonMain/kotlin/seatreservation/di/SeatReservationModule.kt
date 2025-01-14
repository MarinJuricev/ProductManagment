package seatreservation.di

import org.koin.dsl.bind
import org.koin.dsl.module
import seatreservation.CancelSeatReservation
import seatreservation.CreateOffice
import seatreservation.DeleteOffice
import seatreservation.GetOffices
import seatreservation.ObserveOffice
import seatreservation.ObserveOffices
import seatreservation.ObserveReservableDates
import seatreservation.ObserveSeatReservations
import seatreservation.OfficeContainsEmptySeats
import seatreservation.ReserveSeat
import seatreservation.UpdateOffice
import seatreservation.dashboard.GetSeatReservationDashboardType
import seatreservation.repository.OfficeRepository
import seatreservation.repository.OfficeRepositoryImpl
import seatreservation.repository.SeatReservationsRepository
import seatreservation.repository.SeatReservationsRepositoryImpl
import seatreservation.validator.OfficeValidator
import seatreservation.validator.OfficeValidatorImpl

fun seatReservationModule() = module {
    factory { SeatReservationsRepositoryImpl(get()) } bind SeatReservationsRepository::class
    factory { OfficeRepositoryImpl(get()) } bind OfficeRepository::class
    factory { ObserveReservableDates(get(), get(), get()) }
    factory { ObserveSeatReservations(get()) }
    factory { OfficeContainsEmptySeats(get(), get()) }
    factory { ReserveSeat(get(), get(), get(), get()) }
    factory { CancelSeatReservation(get(), get()) }
    factory { GetOffices(get()) }
    factory { GetSeatReservationDashboardType(get()) }
    factory { ObserveOffice(get()) }
    factory { CreateOffice(get(), get()) }
    factory { ObserveOffices(get()) }
    factory { DeleteOffice(get()) }
    factory { UpdateOffice(get()) }
    factory { OfficeValidatorImpl() } bind OfficeValidator::class
}
