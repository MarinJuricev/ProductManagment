package org.product.inventory.web.models

import org.product.inventory.web.components.ParkingOption

data class ParkingSelectionData(
    val garageLevelLabel: String,
    val parkingSpotLabel: String,
    val parkingErrorMessage: String,
    val parkingErrorRetryMessage: String,
    val noParkingSpacesMessage: String,
    val garageLevels: List<ParkingOption>,
    val parkingSpots: List<ParkingOption>,
    val selectedGarageLevel: ParkingOption,
    val selectedParkingSpot: ParkingOption,
    val validParkingOption: Boolean,
)
