package com.alad1nks.dubovozki

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.alad1nks.dubovozki.feature.designsystem.TestTags
import com.google.firebase.Firebase
import com.google.firebase.app
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection
import java.net.URL

@RunWith(AndroidJUnit4::class)
class AndroidEntryPointE2ETest {
    @get:Rule
    val compose = createEmptyComposeRule()

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        System.setProperty("dubovozki.e2e.firebase.host", "10.0.2.2")
        System.setProperty("dubovozki.e2e.firebase.port", "9000")
        seedFirebaseEmulator()
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        if (::scenario.isInitialized) {
            scenario.close()
        }
        System.clearProperty("dubovozki.e2e.firebase.host")
        System.clearProperty("dubovozki.e2e.firebase.port")
    }

    @Test
    fun actualActivityLaunchesAndNavigatesAcrossP0Destinations() {
        compose.onNodeWithTag(TestTags.APP_CONTENT).assertIsDisplayed()
        compose.onNodeWithTag(TestTags.NAV_SCHEDULE).assertIsSelected()
        writeFirebaseEmulator(
            path = "busSchedule",
            value =
                """{"revision":"android-realtime-v2","busList":[""" +
                    listOf(1, 2, 3, 7).joinToString(",") { day ->
                        """{"id":501,"dayOfWeek":$day,"dayTime":43200000,"dayTimeString":"12:00","direction":"msk","station":"odn"}"""
                    } +
                    "]}",
        )
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithTag(TestTags.bus(501)).fetchSemanticsNodes().isNotEmpty()
        }

        compose.onNodeWithTag(TestTags.NAV_SERVICES).performClick().assertIsSelected()
        compose.onNodeWithTag(TestTags.SERVICES_LINEN).assertIsDisplayed()
        compose.onNodeWithTag(TestTags.SERVICES_LINEN).performClick()
        compose.onNodeWithTag(TestTags.SERVICE_SCHEDULE_BACK).assertIsDisplayed()
        compose.onNodeWithTag(TestTags.NAV_SERVICES).assertDoesNotExist()
        scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
        compose.onNodeWithTag(TestTags.NAV_SERVICES).assertIsSelected()

        compose.onNodeWithTag(TestTags.NAV_SETTINGS).performClick().assertIsSelected()
        compose.onNodeWithTag(TestTags.SETTINGS_LANGUAGE).assertIsDisplayed()
        compose.onNodeWithTag(TestTags.SETTINGS_THEME).performClick()
        compose.onNodeWithTag(TestTags.theme("DARK")).performClick()
        compose.onNodeWithTag(TestTags.currentTheme("DARK"), useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithTag(TestTags.SETTINGS_LANGUAGE).performClick()
        compose.onNodeWithTag(TestTags.language("ENGLISH")).performClick()
        compose.onNodeWithTag(TestTags.currentLanguage("ENGLISH"), useUnmergedTree = true).assertIsDisplayed()

        scenario.close()
        scenario = ActivityScenario.launch(MainActivity::class.java)
        compose.onNodeWithTag(TestTags.NAV_SETTINGS).performClick().assertIsSelected()
        compose.onNodeWithTag(TestTags.currentTheme("DARK"), useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithTag(TestTags.currentLanguage("ENGLISH"), useUnmergedTree = true).assertIsDisplayed()
    }

    private fun seedFirebaseEmulator() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val fixture = context.assets.open("happy.json").bufferedReader().use { it.readText() }
        writeFirebaseEmulator(path = "", value = fixture)
    }

    private fun writeFirebaseEmulator(
        path: String,
        value: String,
    ) {
        val projectId = Firebase.app.options.projectId
        val firebasePath = if (path.isEmpty()) "/.json" else "/$path.json"
        val connection =
            URL("http://10.0.2.2:9000$firebasePath?ns=$projectId").openConnection() as HttpURLConnection
        connection.requestMethod = "PUT"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        connection.outputStream.bufferedWriter().use { it.write(value) }
        check(connection.responseCode in 200..299) {
            "Firebase Emulator seed failed with HTTP ${connection.responseCode}"
        }
        connection.disconnect()
    }
}
