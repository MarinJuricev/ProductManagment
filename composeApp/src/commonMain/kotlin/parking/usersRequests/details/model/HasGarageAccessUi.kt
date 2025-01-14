package parking.usersRequests.details.model

sealed interface HasGarageAccessUi {
    data object Loading : HasGarageAccessUi
    data class Retry(
        val errorMessage: String,
        val buttonTitle: String,
    ) : HasGarageAccessUi
    data class UiData(
        val formTitle: String,
        val switchActive: Boolean,
    ) : HasGarageAccessUi
}
