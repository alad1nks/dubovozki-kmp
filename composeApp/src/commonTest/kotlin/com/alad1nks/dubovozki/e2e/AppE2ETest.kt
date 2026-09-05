package com.alad1nks.dubovozki.e2e

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.test.v2.runComposeUiTest
import com.alad1nks.dubovozki.core.domain.MoscowTimeProvider
import com.alad1nks.dubovozki.core.firebase.BusScheduleApi
import com.alad1nks.dubovozki.core.firebase.ServicesApi
import com.alad1nks.dubovozki.core.firebase.ServicesScheduleApi
import com.alad1nks.dubovozki.core.firebase.model.BusScheduleResponse
import com.alad1nks.dubovozki.core.firebase.model.ServicesScheduleResponse.ServiceScheduleItemResponse
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.storage.common.AppPreferences
import com.alad1nks.dubovozki.feature.designsystem.TestTags
import com.alad1nks.dubovozki.shared.CommonModules
import com.alad1nks.dubovozki.shared.ui.App
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class AppE2ETest {
    @Test
    fun launchAndTopLevelNavigation() =
        runAppTest { driver, uriHandler ->
            onNodeWithTag(TestTags.APP_CONTENT).assertIsDisplayed()
            onNodeWithTag(TestTags.NAV_SCHEDULE).assertIsSelected()
            onNodeWithTag(TestTags.bus(1)).assertIsDisplayed()

            click(TestTags.NAV_SERVICES)
            onNodeWithTag(TestTags.NAV_SERVICES).assertIsSelected()
            onNodeWithTag(TestTags.SERVICES_LINEN).assertIsDisplayed()
            click(TestTags.NAV_SETTINGS)
            onNodeWithTag(TestTags.NAV_SETTINGS).assertIsSelected()
            onNodeWithTag(TestTags.SETTINGS_THEME).assertIsDisplayed()
            onNodeWithTag(TestTags.currentTheme("SYSTEM"), useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag(TestTags.currentLanguage("SYSTEM"), useUnmergedTree = true).assertIsDisplayed()

            assertEquals(0, driver.busApi.refreshCount)
            assertEquals(emptyList(), uriHandler.openedUris)
        }

    @Test
    fun detailHidesTopLevelNavigationAndBackReturnsToServices() =
        runAppTest { _, _ ->
            click(TestTags.NAV_SERVICES)
            click(TestTags.SERVICES_LINEN)

            onNodeWithTag(TestTags.SERVICE_SCHEDULE_BUILDING_1).assertIsSelected()
            onNodeWithTag(TestTags.serviceScheduleDay(1, isToday = true)).assertIsDisplayed()
            onAllNodesWithTag(TestTags.NAV_SERVICES).assertCountEquals(0)
            click(TestTags.SERVICE_SCHEDULE_BACK)

            onNodeWithTag(TestTags.NAV_SERVICES).assertIsSelected()
            onNodeWithTag(TestTags.SERVICES_LINEN).assertIsDisplayed()
        }

    @Test
    fun repeatedTopLevelNavigationKeepsSingleDestination() =
        runAppTest { _, _ ->
            repeat(3) {
                click(TestTags.NAV_SERVICES)
                click(TestTags.NAV_SCHEDULE)
                click(TestTags.NAV_SETTINGS)
            }
            onAllNodesWithTag(TestTags.SETTINGS_LANGUAGE).assertCountEquals(1)
            onNodeWithTag(TestTags.NAV_SETTINGS).assertIsSelected()
        }

    @Test
    fun directionsAndCombinedFiltersUseStableDomainSelectors() =
        runAppTest { _, _ ->
            onNodeWithTag(TestTags.bus(1)).assertIsDisplayed()
            click(TestTags.BUS_TAB_DUBKI)
            onNodeWithTag(TestTags.BUS_TAB_DUBKI).assertIsSelected()
            onNodeWithTag(TestTags.bus(2)).assertIsDisplayed()

            click(TestTags.BUS_FILTER_STATION)
            click(TestTags.stationFilter("MOLODYOZHNAYA"))
            click(TestTags.BUS_FILTER_DAY)
            click(TestTags.dayFilter("SATURDAY"))

            onNodeWithTag(TestTags.bus(8)).assertIsDisplayed()
            onAllNodesWithTag(TestTags.bus(2)).assertCountEquals(0)
        }

    @Test
    fun busDirectionsCanBeSwipedWithoutCoordinateSelectors() =
        runAppTest { _, _ ->
            onNodeWithTag(TestTags.bus(1)).assertIsDisplayed()
            onNodeWithTag(TestTags.BUS_PAGER).performTouchInput { swipeLeft() }
            waitUntilTag(TestTags.bus(2))
            onNodeWithTag(TestTags.BUS_TAB_DUBKI).assertIsSelected()

            onNodeWithTag(TestTags.BUS_PAGER).performTouchInput { swipeRight() }
            waitUntilTag(TestTags.bus(1))
            onNodeWithTag(TestTags.BUS_TAB_MOSCOW).assertIsSelected()
        }

    @Test
    fun everyStationAndDayFilterUsesTheExpectedDomainData() =
        runAppTest { _, _ ->
            selectStation("ODINTSOVO")
            waitUntilTag(TestTags.bus(1))
            onNodeWithTag(TestTags.bus(1)).assertIsDisplayed()
            selectStation("SLAVYANSKY_BULVAR")
            waitUntilTag(TestTags.bus(3))
            onNodeWithTag(TestTags.bus(3)).assertIsDisplayed()
            selectStation("MOLODYOZHNAYA")
            click(TestTags.BUS_TAB_DUBKI)
            waitUntilTag(TestTags.bus(4))
            onNodeWithTag(TestTags.bus(4)).assertIsDisplayed()

            selectStation("ALL")
            click(TestTags.BUS_TAB_MOSCOW)
            selectDay("TODAY")
            waitUntilTag(TestTags.bus(1))
            onNodeWithTag(TestTags.bus(1)).assertIsDisplayed()
            selectDay("TOMORROW")
            waitUntilTag(TestTags.bus(5))
            onNodeWithTag(TestTags.bus(5)).assertIsDisplayed()
            selectDay("WEEKDAYS")
            waitUntilTag(TestTags.bus(5))
            onNodeWithTag(TestTags.bus(5)).assertIsDisplayed()
            selectDay("SATURDAY")
            waitUntilTag(TestTags.bus(7))
            onNodeWithTag(TestTags.bus(7)).assertIsDisplayed()
            selectDay("SUNDAY")
            waitUntilTag(TestTags.bus(9))
            onNodeWithTag(TestTags.bus(9)).assertIsDisplayed()
        }

    @Test
    fun tomorrowOnSundayUsesMondaySchedule() {
        val driver = E2ETestDriver()
        driver.time.set(E2EClockFixtures.sundayBeforeMidnight)
        runAppTest(driver) { _, _ ->
            selectDay("TOMORROW")
            onNodeWithTag(TestTags.bus(1)).assertIsDisplayed()
        }
    }

    @Test
    fun emptyResultCanResetFilters() {
        val driver = E2ETestDriver(initialBus = Data.Success(E2EFixtures.emptyBusSchedule))

        runAppTest(driver) { _, _ ->
            waitUntilTag(TestTags.BUS_EMPTY)
            onNodeWithTag(TestTags.BUS_EMPTY).assertIsDisplayed()
            click(TestTags.BUS_RESET_FILTERS)
            onNodeWithTag(TestTags.BUS_FILTER_STATION).assertIsDisplayed()
        }
    }

    @Test
    fun busErrorRetryRecoversWithoutRestart() {
        val driver = E2ETestDriver(initialBus = Data.Error("offline"))
        driver.busApi.onRefresh(Data.Success(E2EFixtures.happyBusSchedule))

        runAppTest(driver) { _, _ ->
            onNodeWithTag(TestTags.COMMON_ERROR).assertIsDisplayed()
            click(TestTags.COMMON_RETRY)
            waitUntilTag(TestTags.bus(1))
            onNodeWithTag(TestTags.bus(1)).assertIsDisplayed()
            assertEquals(1, driver.busApi.refreshCount)
        }
    }

    @Test
    fun cachedBusDataStaysVisibleOfflineThenRealtimeFreshDataReplacesIt() {
        val driver = E2ETestDriver(initialBus = Data.Error("offline"))
        driver.preferences.seedString("language", "en")
        driver.preferences.seedString("bus_schedule_cache_v1", cache(E2EFixtures.happyBusSchedule))
        val fresh =
            E2EFixtures.happyBusSchedule.copy(
                revision = "fresh-v2",
                busList =
                    listOf(
                        BusScheduleResponse.Bus(
                            id = 201,
                            dayOfWeek = 2,
                            dayTime = 34_200_000,
                            dayTimeString = "09:30",
                            direction = "msk",
                            station = "odn",
                        ),
                    ),
            )

        runAppTest(driver) { _, _ ->
            waitUntilTag(TestTags.COMMON_OFFLINE)
            onNodeWithTag(TestTags.bus(1)).assertIsDisplayed()
            driver.busApi.emit(Data.Success(fresh))
            waitUntilTag(TestTags.bus(201))
            onAllNodesWithTag(TestTags.bus(1)).assertCountEquals(0)
        }
    }

    @Test
    fun refreshKeepsCombinedFilters() {
        val driver = E2ETestDriver()
        driver.busApi.onRefresh(Data.Success(E2EFixtures.happyBusSchedule.copy(revision = "refreshed-v2")))

        runAppTest(driver) { _, _ ->
            selectStation("MOLODYOZHNAYA")
            selectDay("SATURDAY")
            click(TestTags.BUS_TAB_DUBKI)
            onNodeWithTag(TestTags.bus(8)).assertIsDisplayed()
            click(TestTags.BUS_REFRESH)
            waitUntil(timeoutMillis = 2_000) { driver.busApi.refreshCount == 1 }
            onNodeWithTag(TestTags.bus(8)).assertIsDisplayed()
        }
    }

    @Test
    fun controlledClockUpdatesBusTimeDifference() {
        val driver = E2ETestDriver()
        driver.preferences.seedString("language", "en")
        runAppTest(driver) { _, _ ->
            onNodeWithTag(TestTags.bus(1)).assertTextContains("in 5 minutes")
            driver.time.set(E2EClockFixtures.atDeparture)
            waitUntilText(TestTags.bus(1), "now")
            driver.time.set(E2EClockFixtures.afterDeparture)
            waitUntilText(TestTags.bus(1), "1 minute ago")
        }
    }

    @Test
    fun partialInvalidDataDoesNotCrash() {
        val driver = E2ETestDriver(initialBus = Data.Success(E2EFixtures.partialInvalidBusSchedule))

        runAppTest(driver) { _, _ ->
            waitUntilTag(TestTags.bus(101))
            onNodeWithTag(TestTags.bus(101)).assertIsDisplayed()
            onAllNodesWithTag(TestTags.bus(102)).assertCountEquals(0)
            onAllNodesWithTag(TestTags.bus(103)).assertCountEquals(0)
        }
    }

    @Test
    fun serviceLinksAreInterceptedAndExact() =
        runAppTest { _, uriHandler ->
            click(TestTags.NAV_SERVICES)
            click(TestTags.SERVICES_CONTACT)
            click(TestTags.SERVICES_DONATE)

            waitUntil(timeoutMillis = 2_000) { uriHandler.openedUris.size == 2 }
            assertEquals(
                listOf("https://t.me/dubki_contact", "https://example.test/donate"),
                uriHandler.openedUris,
            )
        }

    @Test
    fun uriHandlerFailureShowsAnErrorWithoutOpeningExternalApps() {
        val driver = E2ETestDriver()
        driver.preferences.seedString("language", "en")
        runAppTest(driver, RecordingUriHandler(shouldFail = true)) { _, _ ->
            click(TestTags.NAV_SERVICES)
            click(TestTags.SERVICES_CONTACT)
            onNodeWithText("Couldn’t open the link").assertIsDisplayed()
        }
    }

    @Test
    fun missingAndUnsupportedServiceLinksAreHidden() {
        val driver =
            E2ETestDriver(
                initialServices =
                    Data.Success(
                        E2EFixtures.emptyServices.copy(
                            contactLink = "javascript:alert(1)",
                            donutLink = null,
                        ),
                    ),
            )

        runAppTest(driver) { _, _ ->
            click(TestTags.NAV_SERVICES)
            onNodeWithTag(TestTags.SERVICES_LINKS_UNAVAILABLE).assertIsDisplayed()
            onAllNodesWithTag(TestTags.SERVICES_CONTACT).assertCountEquals(0)
            onAllNodesWithTag(TestTags.SERVICES_DONATE).assertCountEquals(0)
        }
    }

    @Test
    fun serviceAndScheduleErrorsRecoverThroughRetry() {
        val driver =
            E2ETestDriver(
                initialServices = Data.Error("offline"),
                initialServiceSchedule = Data.Error("offline"),
            )
        driver.servicesApi.onRefresh(Data.Success(E2EFixtures.happyServices))
        driver.serviceScheduleApi.onRefresh(Data.Success(E2EFixtures.happyServiceSchedule))

        runAppTest(driver) { _, _ ->
            click(TestTags.NAV_SERVICES)
            click(TestTags.COMMON_RETRY)
            waitUntilTag(TestTags.SERVICES_LINEN)
            click(TestTags.SERVICES_LINEN)
            click(TestTags.COMMON_RETRY)
            waitUntilTag(TestTags.SERVICE_SCHEDULE_BUILDING_1)
            onNodeWithTag(TestTags.SERVICE_SCHEDULE_BUILDING_1).assertIsSelected()
        }
    }

    @Test
    fun cachedServicesAndScheduleStayAvailableOfflineAndUpdateRealtime() {
        val driver =
            E2ETestDriver(
                initialServices = Data.Error("offline"),
                initialServiceSchedule = Data.Error("offline"),
            )
        driver.preferences.seedString("services_cache_v1", cache(E2EFixtures.happyServices))
        driver.preferences.seedString("services_schedule_cache_v1", cache(E2EFixtures.happyServiceSchedule))

        runAppTest(driver) { _, _ ->
            click(TestTags.NAV_SERVICES)
            waitUntilTag(TestTags.COMMON_OFFLINE)
            onNodeWithTag(TestTags.SERVICES_CONTACT).assertIsDisplayed()
            driver.servicesApi.emit(Data.Success(E2EFixtures.emptyServices))
            waitUntilTag(TestTags.SERVICES_LINKS_UNAVAILABLE)

            click(TestTags.SERVICES_LINEN)
            waitUntilTag(TestTags.COMMON_OFFLINE)
            click(TestTags.SERVICE_SCHEDULE_BUILDING_2)
            driver.serviceScheduleApi.emit(Data.Success(E2EFixtures.oneEmptyBuildingServiceSchedule))
            waitUntilTag(TestTags.SERVICE_SCHEDULE_EMPTY)
            onNodeWithTag(TestTags.SERVICE_SCHEDULE_BUILDING_2).assertIsSelected()
        }
    }

    @Test
    fun invalidLinenRowsDoNotPreventOpeningOtherBuildings() {
        val driver =
            E2ETestDriver(
                initialServiceSchedule =
                    Data.Success(
                        E2EFixtures.happyServiceSchedule.copy(
                            firstBuilding =
                                listOf(
                                    ServiceScheduleItemResponse(0, "invalid"),
                                    ServiceScheduleItemResponse(8, "invalid"),
                                    ServiceScheduleItemResponse(1, " "),
                                ),
                        ),
                    ),
            )

        runAppTest(driver) { _, _ ->
            click(TestTags.NAV_SERVICES)
            click(TestTags.SERVICES_LINEN)
            waitUntilTag(TestTags.SERVICE_SCHEDULE_EMPTY)
            click(TestTags.SERVICE_SCHEDULE_BUILDING_2)
            onNodeWithTag(TestTags.serviceScheduleDay(2, isToday = false)).assertIsDisplayed()
        }
    }

    @Test
    fun linenEntryRemainsAvailableWhileServiceLinksFail() {
        val driver = E2ETestDriver(initialServices = Data.Error("offline"))

        runAppTest(driver) { _, _ ->
            click(TestTags.NAV_SERVICES)
            onNodeWithTag(TestTags.COMMON_ERROR).assertIsDisplayed()
            click(TestTags.SERVICES_LINEN)
            onNodeWithTag(TestTags.serviceScheduleDay(1, isToday = true)).assertIsDisplayed()
        }
    }

    @Test
    fun allServiceScheduleBuildingsRemainSelectable() =
        runAppTest { _, _ ->
            click(TestTags.NAV_SERVICES)
            click(TestTags.SERVICES_LINEN)

            onNodeWithTag(TestTags.SERVICE_SCHEDULE_BUILDING_1).assertIsSelected()
            click(TestTags.SERVICE_SCHEDULE_BUILDING_2)
            onNodeWithTag(TestTags.SERVICE_SCHEDULE_BUILDING_2).assertIsSelected()
            click(TestTags.SERVICE_SCHEDULE_BUILDING_3)
            onNodeWithTag(TestTags.SERVICE_SCHEDULE_BUILDING_3).assertIsSelected()
        }

    @Test
    fun settingsPersistInMemoryAndCurrentDestinationSurvivesLocaleChange() =
        runAppTest { driver, _ ->
            click(TestTags.NAV_SETTINGS)
            click(TestTags.SETTINGS_THEME)
            click(TestTags.theme("DARK"))
            waitUntilTag(TestTags.currentTheme("DARK"))
            click(TestTags.SETTINGS_LANGUAGE)
            click(TestTags.language("ENGLISH"))
            waitUntilTag(TestTags.currentLanguage("ENGLISH"))

            waitUntil(timeoutMillis = 2_000) {
                driver.preferences.stringValue("theme_mode") == "dark" &&
                    driver.preferences.stringValue("language") == "en"
            }
            onNodeWithTag(TestTags.NAV_SETTINGS).assertIsSelected()
            onNodeWithTag(TestTags.SETTINGS_LANGUAGE).assertIsDisplayed()
        }

    @Test
    fun everyThemeAndLocaleOptionUpdatesWithoutLeavingSettings() =
        runAppTest { driver, _ ->
            click(TestTags.NAV_SETTINGS)
            listOf("LIGHT", "DARK", "SYSTEM").forEach { theme ->
                click(TestTags.SETTINGS_THEME)
                click(TestTags.theme(theme))
                waitUntilTag(TestTags.currentTheme(theme))
                when (theme) {
                    "LIGHT" -> waitUntilTag(TestTags.appTheme(isDark = false))
                    "DARK" -> waitUntilTag(TestTags.appTheme(isDark = true))
                }
            }
            mapOf(
                "RUSSIAN" to "Настройки",
                "ENGLISH" to "Settings",
                "KAZAKH" to "Параметрлер",
            ).forEach { (language, localizedTitle) ->
                click(TestTags.SETTINGS_LANGUAGE)
                click(TestTags.language(language))
                waitUntilTag(TestTags.currentLanguage(language))
                onNodeWithTag(TestTags.NAV_SETTINGS).assertIsSelected()
                onAllNodesWithText(localizedTitle).assertCountEquals(2)
            }
            click(TestTags.SETTINGS_LANGUAGE)
            click(TestTags.language("SYSTEM"))
            waitUntilTag(TestTags.currentLanguage("SYSTEM"))
            waitUntil(timeoutMillis = 2_000) {
                driver.preferences.stringValue("theme_mode") == "system" &&
                    driver.preferences.stringValue("language") == "system"
            }
        }

    @Test
    fun initialBackendStateShowsDeterministicLoading() {
        val driver =
            E2ETestDriver(
                initialBus = Data.Initial(),
                initialServices = Data.Initial(),
                initialServiceSchedule = Data.Initial(),
            )

        runAppTest(driver) { _, _ ->
            waitUntilTag(TestTags.COMMON_LOADING)
            onNodeWithTag(TestTags.COMMON_LOADING).assertIsDisplayed()
            driver.busApi.emit(Data.Success(E2EFixtures.happyBusSchedule))
            waitUntilTag(TestTags.bus(1))
        }
    }

    private fun runAppTest(
        driver: E2ETestDriver = E2ETestDriver(),
        uriHandler: RecordingUriHandler = RecordingUriHandler(),
        block: androidx.compose.ui.test.ComposeUiTest.(E2ETestDriver, RecordingUriHandler) -> Unit,
    ) = runComposeUiTest {
        val showContent = mutableStateOf(true)
        val overrides =
            module {
                single<BusScheduleApi> { driver.busApi }
                single<ServicesApi> { driver.servicesApi }
                single<ServicesScheduleApi> { driver.serviceScheduleApi }
                single<AppPreferences> { driver.preferences }
                single<MoscowTimeProvider> { driver.time }
            }
        val isolatedKoin =
            koinApplication {
                allowOverride(true)
                modules(CommonModules + overrides)
            }

        setContent {
            if (showContent.value) {
                CompositionLocalProvider(LocalUriHandler provides uriHandler) {
                    App(isolatedKoinApplication = isolatedKoin)
                }
            }
        }
        try {
            waitUntilTag(TestTags.APP_CONTENT)
            block(driver, uriHandler)
        } finally {
            runOnUiThread { showContent.value = false }
            waitForIdle()
            isolatedKoin.close()
        }
    }

    private fun androidx.compose.ui.test.ComposeUiTest.waitUntilTag(tag: String) {
        waitUntil(timeoutMillis = 5_000) {
            onAllNodesWithTag(tag, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun androidx.compose.ui.test.ComposeUiTest.waitUntilText(
        tag: String,
        text: String,
    ) {
        waitUntil(timeoutMillis = 5_000) {
            runCatching {
                onNodeWithTag(tag).assertTextContains(text)
                true
            }.getOrDefault(false)
        }
    }

    private fun androidx.compose.ui.test.ComposeUiTest.click(tag: String) {
        val node = onNodeWithTag(tag)
        runOnUiThread { node.performClick() }
        waitForIdle()
    }

    private fun androidx.compose.ui.test.ComposeUiTest.selectStation(value: String) {
        click(TestTags.BUS_FILTER_STATION)
        click(TestTags.stationFilter(value))
    }

    private fun androidx.compose.ui.test.ComposeUiTest.selectDay(value: String) {
        click(TestTags.BUS_FILTER_DAY)
        click(TestTags.dayFilter(value))
    }

    private inline fun <reified T> cache(value: T): String =
        """{"value":${Json.encodeToString(value)},"updatedAtEpochMillis":1788163200000}"""
}

private class RecordingUriHandler(
    private val shouldFail: Boolean = false,
) : UriHandler {
    val openedUris = mutableListOf<String>()

    override fun openUri(uri: String) {
        if (shouldFail) error("intercepted URI failure")
        openedUris += uri
    }
}
