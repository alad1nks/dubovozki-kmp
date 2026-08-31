import XCTest

final class AppEntryPointUITests: XCTestCase {
    func testSwiftUIShellLaunchesComposeAndNavigates() {
        let app = XCUIApplication()
        app.launchArguments += ["--e2e"]
        app.launch()

        XCTAssertTrue(app.buttons["nav.schedule"].waitForExistence(timeout: 15))
        app.buttons["nav.services"].tap()
        XCTAssertTrue(app.buttons["services.linen"].waitForExistence(timeout: 10))
        app.buttons["nav.settings"].tap()
        XCTAssertTrue(app.buttons["settings.language"].waitForExistence(timeout: 10))
    }
}
