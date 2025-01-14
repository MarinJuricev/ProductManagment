import Dependencies
import Utilities
import Shared
import Core

struct EmailTemplatesClient {
    var get: () async throws -> [IAEmailTemplate]
    var update: (IAEmailTemplate) async throws -> Void
}

extension EmailTemplatesClient: DependencyKey {
    public enum EmailTemplatesClientError: Error {
        case castingError
    }

    static let liveValue: EmailTemplatesClient = EmailTemplatesClient {
        let getTemplates: GetTemplates = Di.shared.get()
        let either = try await getTemplates.invoke()

        guard let items = try SwiftEither(either: either).get() as? [Template] else {
            throw EmailTemplatesClientError.castingError
        }
        return items.map { IAEmailTemplate($0) }
    } update: { updatedTemplate in
        let updateTemplate: SaveTemplates = Di.shared.get()
        let either = try await updateTemplate.invoke(templates: [Template(updatedTemplate)])
        _ = try SwiftEither(either: either).get()
    }
}

extension DependencyValues {
    var emailTemplatesClient: EmailTemplatesClient {
        get { self[EmailTemplatesClient.self] }
        set { self[EmailTemplatesClient.self] = newValue }
    }
}
