package parking.myReservations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import components.SingleRowFormValue
import org.product.inventory.shared.MR
import parking.reservation.components.ParkingReservationStatusIndicator
import parking.reservation.model.ParkingReservationUiModel

@Composable
fun MyReservationRequestItem(
    modifier: Modifier = Modifier,
    reservation: ParkingReservationUiModel,
    onItemClick: (ParkingReservationUiModel) -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(23.dp))
            .clickable { onItemClick(reservation) }
            .background(colorResource(MR.colors.surface.resourceId))
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.7f),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            SingleRowFormValue(
                valueName = reservation.submittedDateFieldName,
                value = reservation.createdAt,
            )
            SingleRowFormValue(
                valueName = reservation.requestedDateFieldName,
                value = reservation.date,
            )
        }
        ParkingReservationStatusIndicator(
            parkingReservation = reservation.reservationStatus,
        )
    }
}
