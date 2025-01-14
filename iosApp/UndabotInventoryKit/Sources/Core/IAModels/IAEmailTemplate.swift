import Shared

public struct IAEmailTemplate: Identifiable, Equatable, Sendable {
    public var id: String
    public var status: IAStatus
    public var text: String
    public var title: String

    public init(
        id: String,
        status: IAStatus,
        text: String,
        title: String) {
            self.id = id
            self.status = status
            self.text = text
            self.title = title
        }

    public init(_ template: Template) {
        self.id = template.id
        self.status = IAStatus(template.status)
        self.text = template.text
        self.title = template.title
    }
}

extension Template {
    public convenience init(_ template: IAEmailTemplate) {
        self.init(
            id: template.id,
            text: template.text, 
            title: template.title,
            status: template.status.toTemplateStatus()
        )
    }
}
