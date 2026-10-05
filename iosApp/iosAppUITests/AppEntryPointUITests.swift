import XCTest

final class AppEntryPointUITests: XCTestCase {
    override func setUpWithError() throws {
        continueAfterFailure = false
    }

    @MainActor
    func testSwiftUIShellLaunchesComposeAndNavigates() async throws {
        let namespace = try XCTUnwrap(
            ProcessInfo.processInfo.environment["E2E_FIREBASE_NAMESPACE"],
            "Pass TEST_RUNNER_E2E_FIREBASE_NAMESPACE to xcodebuild"
        )
        // Reset the initial row on every iteration so a cached previous update cannot pass the test.
        try await replaceBusScheduleInEmulator(namespace: namespace, busId: 502)
        let app = XCUIApplication()
        app.launchArguments += ["--e2e"]
        app.launch()

        XCTAssertTrue(app.buttons["nav.schedule"].waitForExistence(timeout: 15))
        XCTAssertTrue(app.descendants(matching: .any)["bus.item.502"].waitForExistence(timeout: 10))
        try await replaceBusScheduleInEmulator(namespace: namespace, busId: 503)
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
        let leftEdge = app.coordinate(withNormalizedOffset: CGVector(dx: 0.01, dy: 0.5))
        let rightSide = app.coordinate(withNormalizedOffset: CGVector(dx: 0.8, dy: 0.5))
        leftEdge.press(forDuration: 0.05, thenDragTo: rightSide)
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

    private func replaceBusScheduleInEmulator(namespace: String, busId: Int) async throws {
        let buses = [1, 2, 3, 7].map { dayOfWeek in
            [
                "id": busId,
                "dayOfWeek": dayOfWeek,
                "dayTime": 43_200_000,
                "dayTimeString": "12:00",
                "direction": "msk",
                "station": "odn",
            ] as [String: Any]
        }
        let payload: [String: Any] = ["revision": "ios-realtime-\(busId)", "busList": buses]
        let url = try XCTUnwrap(URL(string: "http://127.0.0.1:9000/busSchedule.json?ns=\(namespace)"))
        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = try JSONSerialization.data(withJSONObject: payload)

        // Await the request itself: an XCTest expectation deadline used to hide its network error.
        let configuration = URLSessionConfiguration.ephemeral
        configuration.timeoutIntervalForRequest = 15
        configuration.timeoutIntervalForResource = 30
        let session = URLSession(configuration: configuration)
        defer { session.invalidateAndCancel() }
        let (data, response) = try await session.data(for: request)
        XCTAssertEqual(
            (response as? HTTPURLResponse)?.statusCode,
            200,
            "Firebase PUT failed: \(String(decoding: data, as: UTF8.self))"
        )
    }
}
