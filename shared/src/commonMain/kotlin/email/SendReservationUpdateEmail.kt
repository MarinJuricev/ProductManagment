package email

import arrow.core.Either
import arrow.core.raise.either
import auth.Authentication
import core.model.EmailRequestData
import email.model.SendEmailError
import email.model.SendEmailError.FetchingTemplatesError
import email.model.SendEmailError.TemplateNotFound
import email.model.SendEmailError.Unauthorized
import parking.reservation.model.ParkingReservation
import parking.reservation.model.ParkingReservationStatus
import parking.templates.GetTemplates
import parking.templates.model.Template
import parking.templates.model.TemplateStatus
import user.model.InventoryAppUser

class SendReservationUpdateEmail(
    private val emailService: EmailService,
    private val authentication: Authentication,
    private val getTemplates: GetTemplates,
    private val updateTemplate: UpdateTemplateEmailWithData,
) {
    suspend operator fun invoke(
        reservation: ParkingReservation,
        templateStatus: TemplateStatus,
    ): Either<SendEmailError, Unit> =
        either {
            val currentUser = authentication.getCurrentUser().mapLeft { Unauthorized }.bind()
            val templates = getTemplates().mapLeft { FetchingTemplatesError }.bind()
            val matchingTemplate = templates.getByStatus(templateStatus).bind()
            sendUpdatedTemplateEmail(
                template = matchingTemplate,
                parkingReservation = reservation,
                currentUser = currentUser,
                status = reservation.status,
            )
        }

    private fun List<Template>.getByStatus(status: TemplateStatus): Either<TemplateNotFound, Template> =
        either {
            this@getByStatus.find { it.status == status } ?: raise(TemplateNotFound)
        }

    private suspend fun sendUpdatedTemplateEmail(
        template: Template,
        parkingReservation: ParkingReservation,
        currentUser: InventoryAppUser,
        status: ParkingReservationStatus,
    ) {
        emailService.sendEmail(
            emailRequest = EmailRequestData(
                recipient = parkingReservation.email,
                htmlText = updateTemplate(
                    template = template.text,
                    status = status,
                    parkingReservation = parkingReservation,
                ),
                subject = template.title,
            ),
        )
    }
}
