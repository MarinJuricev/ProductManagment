// swift-tools-version:5.9
import PackageDescription

let packageName = "Shared"

let package = Package(
    name: packageName,
    platforms: [
        .iOS(.v16)
    ],
    products: [
        .library(
            name: packageName,
            targets: [packageName]
        ),
    ],
    targets: [
        .binaryTarget(
            name: packageName,
            path: "./shared/build/XCFrameworks/debug/\(packageName).xcframework"
        )
        ,
    ]
)