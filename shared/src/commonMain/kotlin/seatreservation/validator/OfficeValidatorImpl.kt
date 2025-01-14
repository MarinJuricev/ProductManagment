package seatreservation.validator

import seatreservation.model.Office

class OfficeValidatorImpl : OfficeValidator {
    override fun isAddingValid(
        unavailableOfficeTitles: List<String>,
        title: String,
        seats: String,
    ): Boolean {
        val isTitleValid = validateTitle(
            unavailableOfficeTitles = unavailableOfficeTitles,
            title = title,
        ).isNullOrBlank()
        return isTitleValid && isSeatNumberValid(seatNumber = seats)
    }

    override fun isEditingValid(
        initialOffice: Office,
        unavailableOffices: List<Office>,
        title: String,
        seats: String,
    ): Boolean {
        val trimmedTitle = title.trim()
        val seatNumber = seats.toIntOrNull()
        if (initialOffice.title == trimmedTitle && initialOffice.numberOfSeats == seatNumber) {
            return false
        }
        val isTitleValid = validateTitle(
            unavailableOffices = unavailableOffices,
            initialOffice = initialOffice,
            title = title,
        ).isNullOrBlank()

        return isTitleValid && isSeatNumberValid(seatNumber = seats)
    }

    override fun validateTitle(
        unavailableOfficeTitles: List<String>,
        title: String,
    ): String? {
        val trimmedTitle = title.trim()
        if (unavailableOfficeTitles.contains(trimmedTitle)) {
            return "Office already exists"
        }
        return validateTitleLength(title = trimmedTitle)
    }

    override fun validateTitle(
        initialOffice: Office,
        unavailableOffices: List<Office>,
        title: String,
    ): String? {
        val trimmedTitle = title.trim()
        if (unavailableOffices.any { initialOffice.id != it.id && it.title == trimmedTitle }) {
            return "Office already exists"
        }
        return validateTitleLength(title = trimmedTitle)
    }

    private fun isSeatNumberValid(seatNumber: String): Boolean = seatNumber.toIntOrNull()?.let { it > 0 } ?: false

    private fun validateTitleLength(title: String): String? = if (title.length < MIN_TITLE_LENGTH || title.length > MAX_TITLE_LENGTH) {
        "Title must be between $MIN_TITLE_LENGTH and $MAX_TITLE_LENGTH characters"
    } else {
        null
    }
}

private const val MIN_TITLE_LENGTH = 3
private const val MAX_TITLE_LENGTH = 15
