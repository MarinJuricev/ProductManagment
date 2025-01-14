package parking.reservation.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement

class ParkingReservationStatusSerializer : KSerializer<ParkingReservationStatus> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ParkingReservationStatus") {
        element<String>(TYPE_KEY)
        element<String>(ADMIN_NOTE_KEY, isOptional = true)
        element<JsonElement>(PARKING_COORDINATE_KEY, isOptional = true)
    }
    override fun serialize(
        encoder: Encoder,
        value: ParkingReservationStatus,
    ) {
        val compositeOutput = encoder.beginStructure(descriptor)
        when (value) {
            is ParkingReservationStatus.Submitted -> {
                compositeOutput.encodeStringElement(descriptor, 0, SUBMITTED_STATUS)
            }
            is ParkingReservationStatus.Approved -> {
                compositeOutput.encodeStringElement(descriptor, 0, APPROVED_STATUS)
                compositeOutput.encodeStringElement(descriptor, 1, value.adminNote)
                compositeOutput.encodeSerializableElement(
                    descriptor,
                    2,
                    ParkingCoordinate.serializer(),
                    value.parkingCoordinate,
                )
            }
            is ParkingReservationStatus.Declined -> {
                compositeOutput.encodeStringElement(descriptor, 0, DECLINED_STATUS)
                compositeOutput.encodeStringElement(descriptor, 1, value.adminNote)
            }
            is ParkingReservationStatus.Canceled -> {
                compositeOutput.encodeStringElement(descriptor, 0, CANCELED_STATUS)
            }
        }
        compositeOutput.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): ParkingReservationStatus {
        val dec = decoder.beginStructure(descriptor)
        var type: String? = null
        var adminNote: String? = null
        var parkingCoordinate: ParkingCoordinate? = null
        loop@ while (true) {
            when (val index = dec.decodeElementIndex(descriptor)) {
                CompositeDecoder.DECODE_DONE -> break@loop
                0 -> type = dec.decodeStringElement(descriptor, index)
                1 -> adminNote = dec.decodeStringElement(descriptor, index)
                2 -> parkingCoordinate = dec.decodeSerializableElement(
                    descriptor,
                    index,
                    ParkingCoordinate.serializer(),
                )
                else -> throw SerializationException("Unexpected index: $index")
            }
        }
        dec.endStructure(descriptor)
        return when (type) {
            SUBMITTED_STATUS, SUBMITTED_STATUS_OLD -> ParkingReservationStatus.Submitted
            APPROVED_STATUS, APPROVED_STATUS_OLD -> {
                ParkingReservationStatus.Approved(
                    adminNote ?: throw SerializationException("$ADMIN_NOTE_KEY missing"),
                    parkingCoordinate ?: throw SerializationException("$PARKING_COORDINATE_KEY missing"),
                )
            }
            DECLINED_STATUS, DECLINED_STATUS_OLD -> ParkingReservationStatus.Declined(
                adminNote ?: throw SerializationException("$ADMIN_NOTE_KEY missing"),
            )
            CANCELED_STATUS, CANCELED_STATUS_OLD -> ParkingReservationStatus.Canceled
            else -> throw SerializationException("Unknown type: $type")
        }
    }
}

private const val TYPE_KEY = "type"
private const val ADMIN_NOTE_KEY = "adminNote"
private const val PARKING_COORDINATE_KEY = "parkingCoordinate"
private const val SUBMITTED_STATUS = "Submitted"
private const val SUBMITTED_STATUS_OLD = "parking.reservation.model.ParkingReservationStatus.Submitted"
private const val APPROVED_STATUS = "Approved"
private const val APPROVED_STATUS_OLD = "parking.reservation.model.ParkingReservationStatus.Approved"
private const val DECLINED_STATUS = "Declined"
private const val DECLINED_STATUS_OLD = "parking.reservation.model.ParkingReservationStatus.Declined"
private const val CANCELED_STATUS = "Canceled"
private const val CANCELED_STATUS_OLD = "parking.reservation.model.ParkingReservationStatus.Canceled"
