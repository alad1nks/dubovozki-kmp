import XCTest

final class AppEntryPointUITests: XCTestCase {
    func testSwiftUIShellLaunchesComposeAndNavigates() {
        let app = XCUIApplication()
        app.launchArguments += ["--e2e"]
        app.launch()

        XCTAssertTrue(app.buttons["nav.schedule"].waitForExistence(timeout: 15))
        replaceBusScheduleInEmulator()
        XCTAssertTrue(app.descendants(matching: .any)["bus.item.503"].waitForExistence(timeout: 10))
        app.buttons["nav.services"].tap()
        XCTAssertTrue(app.buttons["services.linen"].waitForExistence(timeout: 10))
        app.buttons["services.linen"].tap()
        XCTAssertTrue(app.buttons["service_schedule.back"].waitForExistence(timeout: 10))
        XCTAssertFalse(app.buttons["nav.services"].exists)
        app.buttons["service_schedule.back"].tap()
        XCTAssertTrue(app.buttons["nav.services"].waitForExistence(timeout: 10))
        app.buttons["services.linen"].tap()
        XCTAssertTrue(app.buttons["service_schedule.back"].waitForExistence(timeout: 10))
        app.swipeRight()
        XCTAssertTrue(app.buttons["nav.services"].waitForExistence(timeout: 10))

        app.buttons["nav.settings"].tap()
        XCTAssertTrue(app.buttons["settings.language"].waitForExistence(timeout: 10))
        app.buttons["settings.theme"].tap()
        app.buttons["settings.theme.dark"].tap()
        app.buttons["settings.language"].tap()
        app.buttons["settings.language.english"].tap()

        app.terminate()
        app.launch()
        XCTAssertTrue(app.buttons["nav.schedule"].waitForExistence(timeout: 15))
        app.buttons["nav.settings"].tap()
        XCTAssertTrue(app.descendants(matching: .any)["settings.theme.current.dark"].waitForExistence(timeout: 10))
        XCTAssertTrue(app.descendants(matching: .any)["settings.language.current.english"].waitForExistence(timeout: 10))
    }

    private func replaceBusScheduleInEmulator() {
        guard let projectId = ProcessInfo.processInfo.environment["E2E_FIREBASE_PROJECT_ID"] else {
            XCTFail("E2E_FIREBASE_PROJECT_ID is required")
            return
        }
        let buses = [1, 2, 3, 7].map { dayOfWeek in
            [
                "id": 503,
                "dayOfWeek": dayOfWeek,
                "dayTime": 43_200_000,
                "dayTimeString": "12:00",
                "direction": "msk",
                "station": "odn",
            ] as [String: Any]
        }
        let payload: [String: Any] = ["revision": "ios-realtime-v2", "busList": buses]
        let url = URL(string: "http://127.0.0.1:9000/busSchedule.json?ns=\(projectId)")!
        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = try! JSONSerialization.data(withJSONObject: payload)

        let updated = expectation(description: "Firebase Emulator updated")
        URLSession.shared.dataTask(with: request) { _, response, error in
            XCTAssertNil(error)
            XCTAssertEqual((response as? HTTPURLResponse)?.statusCode, 200)
            updated.fulfill()
        }.resume()
        wait(for: [updated], timeout: 10)
    }
}
