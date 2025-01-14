package components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.product.inventory.shared.MR
import parking.reservation.model.MultipleParkingRequestState

@Composable
fun SubmittableDate(
    modifier: Modifier = Modifier,
    state: Pair<String, MultipleParkingRequestState>,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        InventoryTextField(
            value = state.first,
            enabled = false,
            modifier = Modifier.fillMaxWidth(0.3f),
            onValueChanged = {},
            shape = RoundedCornerShape(13.dp),
            isError = state.second == MultipleParkingRequestState.FAILURE,
        )
        when (state.second) {
            MultipleParkingRequestState.LOADING -> LoadingIndicator(modifier = Modifier.size(24.dp))
            MultipleParkingRequestState.FAILURE -> Image(imageType = ImageType.Resource(MR.images.ic_error))
            MultipleParkingRequestState.SUCCESS -> Image(imageType = ImageType.Resource(MR.images.ic_green_check))
        }
    }
}
