package seatreservation.validator

import seatreservation.model.Office

interface OfficeValidator {
    fun isAddingValid(
        unavailableOfficeTitles: List<String>,
        title: String,
        seats: String,
    ): Boolean
    fun isEditingValid(
        initialOffice: Office,
        unavailableOffices: List<Office>,
        title: String,
        seats: String,
    ): Boolean
    fun validateTitle(
        unavailableOfficeTitles: List<String>,
        title: String,
    ): String?
    fun validateTitle(
        initialOffice: Office,
        unavailableOffices: List<Office>,
        title: String,
    ): String?
}
