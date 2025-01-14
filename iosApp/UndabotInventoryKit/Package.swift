// swift-tools-version: 5.10
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription
import Foundation

var remoteConfig = true

var dependencies: [Package.Dependency] = [
    .package(url: "https://github.com/pointfreeco/swift-composable-architecture", exact: "1.13.0"),
    .package(url: "https://github.com/google/GoogleSignIn-iOS", from: "7.1.0"),
    .package(url: "https://github.com/onevcat/Kingfisher.git", from: "7.0.0"),
    .package(url: "https://github.com/pointfreeco/swift-dependencies", from: "1.3.0"),
    .package(url: "https://github.com/firebase/firebase-ios-sdk.git", from: "10.24.0"),
    .package(url: "https://github.com/pointfreeco/swift-navigation", from: "2.0.0")
]

let sharedSPMVersion: Version = "1.0.0"

if ProcessInfo.processInfo.environment["CI"] != nil {
    dependencies.append(.package(url: "git@kmp_inventory:MarinJuricev/product-inventory-kmp.git", exact: sharedSPMVersion))
} else if remoteConfig {
    dependencies.append(.package(url: "git@github.com:MarinJuricev/product-inventory-kmp.git", exact: sharedSPMVersion))
} else {
    dependencies.append(.package(path: "../../"))
}

let package = Package(
    name: "MarinJuricevInventoryKit",
    platforms: [.iOS(.v17)],
    products: [
        // Products define the executables and libraries a package produces, making them visible to other packages.
        .library(
            name: "Core",
            targets: ["Core"]),
        .library(
            name: "Utilities",
            targets: ["Utilities"]),
        .library(
            name: "CommonUI",
            targets: ["CommonUI"]),
        .library(
            name: "AuthenticationClient",
            targets: ["AuthenticationClient"]),
        .library(
            name: "LoginFeature",
            targets: ["LoginFeature"]),
        .library(
            name: "MainFeature",
            targets: ["MainFeature"]),
        .library(
            name: "ParkingReservationFeature",
            targets: ["ParkingReservationFeature"]),
        .library(
            name: "SeatReservationFeature",
            targets: ["SeatReservationFeature"]),
        .library(
            name: "CrewManagementFeature",
            targets: ["CrewManagementFeature"]),
        .library(
            name: "AppFeature",
            targets: ["AppFeature"])
    ],
    dependencies: dependencies,
    targets: [
        // Targets are the basic building blocks of a package, defining a module or a test suite.
        // Targets can depend on other targets in this package and products from dependencies.
        .target(
            name: "Core",
            dependencies: [
                .product(name: "Shared", package: "product-inventory-kmp"),
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture")
            ]
        ),
        .target(
            name: "CommonUI",
            dependencies: [
                "Core",
                "Utilities",
                .product(name: "Kingfisher", package: "Kingfisher"),
                .product(name: "Shared", package: "product-inventory-kmp")
            ]
        ),
        .target(
            name: "Utilities",
            dependencies: [
                .product(name: "Shared", package: "product-inventory-kmp"),
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
                .product(name: "SwiftNavigation", package: "swift-navigation")
            ]
        ),
        .target(
            name: "AuthenticationClient",
            dependencies: [
                "Utilities",
                "Core",
                .product(name: "GoogleSignIn", package: "GoogleSignIn-iOS"),
                .product(name: "Dependencies", package: "swift-dependencies"),
                .product(name: "DependenciesMacros", package: "swift-dependencies"),
                .product(name: "Shared", package: "product-inventory-kmp")
            ]
        ),
        .target(
            name: "LoginFeature",
            dependencies: [
                "Core",
                "Utilities",
                "AuthenticationClient",
                .product(name: "Shared", package: "product-inventory-kmp"),
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
                .product(name: "GoogleSignInSwift", package: "GoogleSignIn-iOS")
            ]
        ),
        .target(
            name: "MainFeature",
            dependencies: [
                "ParkingReservationFeature",
                "SeatReservationFeature",
                "CommonUI",
                "AuthenticationClient",
                "CrewManagementFeature",
                .product(name: "GoogleSignIn", package: "GoogleSignIn-iOS"),
                .product(name: "Shared", package: "product-inventory-kmp"),
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture")
            ]
        ),
        .target(
            name: "ParkingReservationFeature",
            dependencies: [
                "Core",
                "Utilities",
                "CommonUI",
                .product(name: "Shared", package: "product-inventory-kmp"),
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture")
            ]
        ),
        .target(
            name: "SeatReservationFeature",
            dependencies: [
                "Core",
                "Utilities",
                "CommonUI",
                .product(name: "Shared", package: "product-inventory-kmp"),
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture")
            ]
        ),
        .target(
            name: "CrewManagementFeature",
            dependencies: [
                "Core",
                "Utilities",
                "CommonUI",
                .product(name: "Shared", package: "product-inventory-kmp"),
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture")
            ]
        ),
        .target(
            name: "AppFeature",
            dependencies: [
                "Utilities",
                "ParkingReservationFeature",
                "LoginFeature",
                "MainFeature",
                "AuthenticationClient",
                .product(name: "Shared", package: "product-inventory-kmp"),
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture")
            ]
        ),
        .testTarget(
            name: "LoginFeatureTests",
            dependencies: [
                "LoginFeature",
                "AuthenticationClient",
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
                .product(name: "FirebaseAuth", package: "firebase-ios-sdk"),
                .product(name: "FirebaseFirestore", package: "firebase-ios-sdk"),
                .product(name: "FirebaseRemoteConfig", package: "firebase-ios-sdk")
            ]
        ),
        .testTarget(
            name: "AppFeatureTests",
            dependencies: [
                "LoginFeature",
                "MainFeature",
                "AppFeature",
                "AuthenticationClient",
                "ParkingReservationFeature",
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
                .product(name: "FirebaseAuth", package: "firebase-ios-sdk"),
                .product(name: "FirebaseFirestore", package: "firebase-ios-sdk"),
                .product(name: "FirebaseRemoteConfig", package: "firebase-ios-sdk")
            ]
        ),
        .testTarget(
            name: "MainFeatureTests",
            dependencies: [
                "MainFeature",
                "AuthenticationClient",
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
                .product(name: "FirebaseAuth", package: "firebase-ios-sdk"),
                .product(name: "FirebaseFirestore", package: "firebase-ios-sdk"),
                .product(name: "FirebaseRemoteConfig", package: "firebase-ios-sdk")
            ]
        ),
        .testTarget(
            name: "ParkingReservationFeatureTests",
            dependencies: [
                "ParkingReservationFeature",
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
                .product(name: "FirebaseAuth", package: "firebase-ios-sdk"),
                .product(name: "FirebaseFirestore", package: "firebase-ios-sdk"),
                .product(name: "FirebaseRemoteConfig", package: "firebase-ios-sdk")
            ]
        )
    ]
)
