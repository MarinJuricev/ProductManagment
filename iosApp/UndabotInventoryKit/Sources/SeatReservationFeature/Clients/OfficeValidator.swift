import DependenciesMacros
import Dependencies
import Core
import Shared
import Utilities

@DependencyClient
public struct OfficeFormValidator: Sendable {
    var isAddingValid: @Sendable (_ unavailableTitles: [String], _ title: String, _ seats: String) -> Bool = { _, _, _ in true }
    var isEditingValid: @Sendable (_ unavailableOffices: [IAOffice], _ initialOffice: IAOffice, _ title: String, _ seats: String) -> Bool = { _, _, _, _ in true }
    var validateAddTitle: @Sendable (_ unavailableTitles: [String], _ title: String) -> String? = { _, _ in nil }
    var validateEditTitle: @Sendable (_ unavailableOffices: [IAOffice], _ initialOffice: IAOffice, _ title: String) -> String? = { _, _, _ in nil }
}

extension OfficeFormValidator: DependencyKey {
    public static var liveValue: OfficeFormValidator {
        let officeValidator: OfficeValidator = Di.shared.get()

        return OfficeFormValidator { unavailableOfficeTitles, title, seats in
            officeValidator.isAddingValid(unavailableOfficeTitles: unavailableOfficeTitles, title: title, seats: seats)
        } isEditingValid: { unavailableOffices, initialOffice, title, seats in
            officeValidator.isEditingValid(initialOffice: Office(initialOffice), unavailableOffices: unavailableOffices.map { Office($0) }, title: title, seats: seats)
        } validateAddTitle: { unavailableOfficeTitles, title in
            officeValidator.validateTitle(unavailableOfficeTitles: unavailableOfficeTitles, title: title)
        } validateEditTitle: { unavailableOffices, initialOffice, title in
            officeValidator.validateTitle(initialOffice: Office(initialOffice), unavailableOffices: unavailableOffices.map { Office($0) }, title: title)
        }
    }
}

extension DependencyValues {
    public var officeValidator: OfficeFormValidator {
        get { self[OfficeFormValidator.self] }
        set { self[OfficeFormValidator.self] = newValue }
    }
}
