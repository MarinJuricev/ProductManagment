import Dependencies
import DependenciesMacros
import Shared

@DependencyClient
public struct GarageLevelFormValidator {
    var isGarageLevelEditingValid: @Sendable (
        _ initialGarageLevelData: IAGarageLevelData,
        _ unavailableGarageLevels: [IAGarageLevel],
        _ title: String,
        _ spots: [IAParkingSpot]) -> Bool = { _, _, _, _ in true }

    var isGarageLevelCreationValid: @Sendable (
        _ unavailableGarageLevelTitles: [String],
        _ title: String,
        _ spots: [IAParkingSpot]) -> Bool = { _, _, _ in true }

    var isParkingSpotCreationValid: @Sendable (
        _ unavailableTitles: [String],
        _ title: String) -> Bool = { _, _ in true }

    var validateAddGarageLevelTitle: @Sendable (
        _ unavailableGarageLevelTitles: [String],
        _ title: String
    ) -> String?

    var validateEditGarageLevelTitle: @Sendable (
        _ initialGarageLevel: IAGarageLevel,
        _ unavailableGarageLevels: [IAGarageLevel],
        _ title: String
    ) -> String?

    var validateParkingSpotTitle: @Sendable (
        _ unavailableGarageLevelTitles: [String],
        _ title: String
    ) -> String?
}

extension GarageLevelFormValidator: DependencyKey {
    public static let liveValue: GarageLevelFormValidator = {
        let garageLevelValidator: GarageLevelValidator = Di.shared.get()
        return GarageLevelFormValidator { initialGarageLevelData, unavailableGarageLevels, title, spots in
            garageLevelValidator.isGarageLevelEditingValid(
                initialGarageLevelData: GarageLevelData(initialGarageLevelData),
                unavailableGarageLevels: unavailableGarageLevels.map { GarageLevel($0) },
                title: title,
                spots: spots.map { ParkingSpot($0) }
            )
        } isGarageLevelCreationValid: { unavailableTitles, title, spots in
            garageLevelValidator.isGarageLevelCreationValid(
                unavailableTitles: unavailableTitles,
                title: title,
                spots: spots.map { ParkingSpot($0) }
            )
        } isParkingSpotCreationValid: { unavailableTitles, title in
            garageLevelValidator.isParkingSpotCreationValid(
                unavailableTitles: unavailableTitles,
                title: title
            )
        } validateAddGarageLevelTitle: { unavailableTitles, title in
            garageLevelValidator.validateGarageLevelTitleCreation(
                unavailableTitles: unavailableTitles,
                title: title
            )
        } validateEditGarageLevelTitle: { initialGarageLevel, unavailableGarageLevels, title in
            garageLevelValidator.validateGarageLevelTitleEdit(
                initialGarageLevel: GarageLevel(initialGarageLevel),
                unavailableGarageLevels: unavailableGarageLevels.map { GarageLevel($0) },
                title: title
            )
        } validateParkingSpotTitle: { unavailableTitles, title in
            garageLevelValidator.validateParkingSpotTitle(
                unavailableTitles: unavailableTitles,
                title: title
            )
        }
    }()
}

extension DependencyValues {
    public var garageLevelValidator: GarageLevelFormValidator {
        get { self[GarageLevelFormValidator.self] }
        set { self[GarageLevelFormValidator.self] = newValue }
    }
}
