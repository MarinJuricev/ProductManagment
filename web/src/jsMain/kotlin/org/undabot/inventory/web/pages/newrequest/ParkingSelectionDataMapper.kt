package org.product.inventory.web.pages.newrequest

import org.product.inventory.web.components.ParkingOption
import org.product.inventory.web.components.isValid
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.core.StringRes
import org.product.inventory.web.models.GarageLevelAndParkingSpotsStatus
import org.product.inventory.web.models.ParkingSelectionData

class ParkingSelectionDataMapper(
    private val dictionary: Dictionary,
) {

    operator fun invoke(
        garageLevelsAndParkingSpotsStatus: GarageLevelAndParkingSpotsStatus,
        editableFields: NewRequestEditableFields,
    ): ParkingSelectionData = ParkingSelectionData(
        garageLevelLabel = dictionary.get(StringRes.userRequestsGarageLevelLabel),
        parkingSpotLabel = dictionary.get(StringRes.userRequestsParkingSpotLabel),
        parkingErrorMessage = dictionary.get(StringRes.userRequestsParkingErrorMessage),
        parkingErrorRetryMessage = dictionary.get(StringRes.userRequestsParkingErrorMessageRetry),
        noParkingSpacesMessage = dictionary.get(StringRes.userRequestsNoParkingSpacesMessage),
        garageLevels = buildGarageLevels(garageLevelsAndParkingSpotsStatus),
        parkingSpots = buildParkingSpots(garageLevelsAndParkingSpotsStatus, editableFields.selectedGarageLevel.id),
        selectedGarageLevel = editableFields.selectedGarageLevel,
        selectedParkingSpot = editableFields.selectedParkingSpot,
        validParkingOption = editableFields.selectedGarageLevel.isValid() &&
            editableFields.selectedParkingSpot.isValid() &&
            editableFields.garageAccess,
    )

    private fun buildGarageLevels(
        garageLevelsAndParkingSpotsStatus: GarageLevelAndParkingSpotsStatus,
    ) = when (garageLevelsAndParkingSpotsStatus) {
        is GarageLevelAndParkingSpotsStatus.Success -> garageLevelsAndParkingSpotsStatus.garageLevelData.map {
            ParkingOption(value = it.level.title, id = it.id)
        }
        else -> emptyList()
    }

    private fun buildParkingSpots(
        garageLevelsAndParkingSpotsStatus: GarageLevelAndParkingSpotsStatus,
        selectedGarageLevelId: String,
    ): List<ParkingOption> {
        if (
            garageLevelsAndParkingSpotsStatus !is GarageLevelAndParkingSpotsStatus.Success ||
            selectedGarageLevelId == ParkingOption.INVALID_VALUE
        ) {
            return emptyList()
        }

        return garageLevelsAndParkingSpotsStatus.garageLevelData
            .firstOrNull { it.id == selectedGarageLevelId }
            ?.spots
            ?.map { ParkingOption(value = it.title, id = it.id) }
            ?: emptyList()
    }
}
