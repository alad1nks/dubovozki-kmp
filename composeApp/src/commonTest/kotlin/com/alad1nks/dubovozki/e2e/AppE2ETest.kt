package com.alad1nks.dubovozki.e2e

import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import com.alad1nks.dubovozki.core.domain.MoscowTimeProvider
import com.alad1nks.dubovozki.core.firebase.BusScheduleApi
import com.alad1nks.dubovozki.core.firebase.ServicesApi
import com.alad1nks.dubovozki.core.firebase.ServicesScheduleApi
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.storage.common.AppPreferences
import com.alad1nks.dubovozki.feature.designsystem.TestTags
import com.alad1nks.dubovozki.shared.CommonModules
import com.alad1nks.dubovozki.shared.ui.App
import org.koin.dsl.module
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class AppE2ETest {
    @Test
    fun launchAndTopLevelNavigation() = runAppTest { driver, uriHandler ->
        onNodeWithTag(TestTags.APP_CONTENT).assertIsDisplayed()
        onNodeWithTag(TestTags.NAV_SCHEDULE).assertIsSelected()
        onNodeWithTag(TestTags.BUS_NEXT_CARD).assertIsDisplayed()

        click(TestTags.NAV_SERVICES)
        onNodeWithTag(TestTags.NAV_SERVICES).assertIsSelected()
        onNodeWithTag(TestTags.SERVICES_LINEN).assertIsDisplayed()
        click(TestTags.NAV_SETTINGS)
        onNodeWithTag(TestTags.NAV_SETTINGS).assertIsSelected()
        onNodeWithTag(TestTags.SETTINGS_THEME).assertIsDisplayed()

        assertEquals(0, driver.busApi.refreshCount)
        assertEquals(emptyList(), uriHandler.openedUris)
    }

    @Test
    fun detailHidesTopLevelNavigationAndBackReturnsToServices() = runAppTest { _, _ ->
        click(TestTags.NAV_SERVICES)
        click(TestTags.SERVICES_LINEN)

        onNodeWithTag(TestTags.SERVICE_SCHEDULE_BUILDING_1).assertIsSelected()
        onAllNodesWithTag(TestTags.NAV_SERVICES).assertCountEquals(0)
        click(TestTags.SERVICE_SCHEDULE_BACK)

        onNodeWithTag(TestTags.NAV_SERVICES).assertIsSelected()
        onNodeWithTag(TestTags.SERVICES_LINEN).assertIsDisplayed()
    }

    @Test
    fun directionsAndCombinedFiltersUseStableDomainSelectors() = runAppTest { _, _ ->
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
            waitUntilTag(TestTags.BUS_NEXT_CARD)
            onNodeWithTag(TestTags.BUS_NEXT_CARD).assertIsDisplayed()
            assertEquals(1, driver.busApi.refreshCount)
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
    fun serviceLinksAreInterceptedAndExact() = runAppTest { _, uriHandler ->
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
    fun allServiceScheduleBuildingsRemainSelectable() = runAppTest { _, _ ->
        click(TestTags.NAV_SERVICES)
        click(TestTags.SERVICES_LINEN)

        onNodeWithTag(TestTags.SERVICE_SCHEDULE_BUILDING_1).assertIsSelected()
        click(TestTags.SERVICE_SCHEDULE_BUILDING_2)
        onNodeWithTag(TestTags.SERVICE_SCHEDULE_BUILDING_2).assertIsSelected()
        click(TestTags.SERVICE_SCHEDULE_BUILDING_3)
        onNodeWithTag(TestTags.SERVICE_SCHEDULE_BUILDING_3).assertIsSelected()
    }

    @Test
    fun settingsPersistInMemoryAndCurrentDestinationSurvivesLocaleChange() = runAppTest { driver, _ ->
        click(TestTags.NAV_SETTINGS)
        click(TestTags.SETTINGS_THEME)
        click(TestTags.theme("DARK"))
        click(TestTags.SETTINGS_LANGUAGE)
        click(TestTags.language("ENGLISH"))

        waitUntil(timeoutMillis = 2_000) {
            driver.preferences.stringValue("theme_mode") == "dark" &&
                driver.preferences.stringValue("language") == "en"
        }
        onNodeWithTag(TestTags.NAV_SETTINGS).assertIsSelected()
        onNodeWithTag(TestTags.SETTINGS_LANGUAGE).assertIsDisplayed()
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
            waitUntilTag(TestTags.BUS_NEXT_CARD)
        }
    }

    private fun runAppTest(
        driver: E2ETestDriver = E2ETestDriver(),
        block: androidx.compose.ui.test.ComposeUiTest.(E2ETestDriver, RecordingUriHandler) -> Unit,
    ) = runComposeUiTest {
        val uriHandler = RecordingUriHandler()
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

    private fun androidx.compose.ui.test.ComposeUiTest.click(tag: String) {
        val node = onNodeWithTag(tag)
        runOnUiThread { node.performClick() }
        waitForIdle()
    }
}

private class RecordingUriHandler : UriHandler {
    val openedUris = mutableListOf<String>()

    override fun openUri(uri: String) {
        openedUris += uri
    }
}
