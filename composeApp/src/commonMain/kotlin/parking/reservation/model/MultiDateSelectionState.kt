package parking.reservation.model

data class MultiDateSelectionState(
    val staticData: MultiDateSelectionStaticData = MultiDateSelectionStaticData(),
    val selectedDates: List<DateWithTimestamp> = emptyList(),
    val forbiddenDates: List<Long> = emptyList(),
    val lowerDateLimit: Long = 0,
    val upperDateLimit: Long = 0,
    val preselectedDate: Long = 0,
    val addingNewDateEnabled: Boolean = true,
    val submitStatusSheetVisible: Boolean = false,
    val requestsForSubmission: Map<String, MultipleParkingRequestState> = emptyMap(),
    val closeSubmitStatusSheetButtonEnabled: Boolean = false,
)

data class DateWithTimestamp(
    val timestamp: Long,
    val date: String,
)

data class MultiDateSelectionStaticData(
    val formTitle: String = "",
    val addDateButtonText: String = "",
    val datePickerConfirmSelectionText: String = "",
    val dateFormTitle: String = "",
    val statusFormTitle: String = "",
    val failedRequestError: String = "",
    val closeButtonText: String = "",
)
