package org.product.inventory.web.pages

object Routes {

    const val home = "/"
    const val auth = "/auth"

    const val parkingReservation = "/parking-reservation"
    const val newRequest = "$parkingReservation/new-request"
    const val myReservations = "$parkingReservation/my-reservations"
    const val userRequests = "$parkingReservation/user-requests"
    const val slotsManagement = "$parkingReservation/slots-management"
    const val emailTemplates = "$parkingReservation/email-templates"

    const val crewManagement = "/crew-management"
    const val seatReservation = "/seat-reservation"
    const val seatReservationTimeline = "$seatReservation/timeline"
    const val seatReservationManagement = "$seatReservation/management"
    const val testDevices = "/test-devices"
}
