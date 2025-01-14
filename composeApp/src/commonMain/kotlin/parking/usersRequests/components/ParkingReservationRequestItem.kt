package parking.usersRequests.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import components.SingleLineFormValue
import org.product.inventory.shared.MR
import parking.reservation.components.ParkingReservationStatusIndicator
import parking.reservation.model.ParkingReservationUiModel

@Composable
fun ParkingReservationRequestItem(
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.70f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SingleLineFormValue(
                valueName = reservation.requestedDateFieldName,
                value = reservation.date,
            )
            SingleLineFormValue(
                valueName = reservation.emailFieldName,
                value = reservation.email,
            )
        }
        ParkingReservationStatusIndicator(
            parkingReservation = reservation.reservationStatus,
        )
    }
}
