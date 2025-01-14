package parking.reservation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import components.BodySmallText
import org.product.inventory.shared.MR
import parking.reservation.model.ParkingReservationStatus
import parking.reservation.model.ParkingReservationStatusUiModel

@Composable
fun ParkingReservationStatusIndicator(
    parkingReservation: ParkingReservationStatusUiModel?,
    modifier: Modifier = Modifier,
) {
    BodySmallText(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .background(
                colorResource(
                    when (parkingReservation?.status) {
                        is ParkingReservationStatus.Approved -> MR.colors.success.resourceId
                        is ParkingReservationStatus.Canceled -> MR.colors.textLight.resourceId
                        is ParkingReservationStatus.Declined -> MR.colors.error.resourceId
                        is ParkingReservationStatus.Submitted -> MR.colors.warning.resourceId
                        null -> MR.colors.textLight.resourceId
                    },
                ),
            )
            .widthIn(min = 100.dp)
            .padding(8.dp),
        text = parkingReservation?.text ?: stringResource(MR.strings.general_all.resourceId),
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        color = MR.colors.surface,
    )
}
