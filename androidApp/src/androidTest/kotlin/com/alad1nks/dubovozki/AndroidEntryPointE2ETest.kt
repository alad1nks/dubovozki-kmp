package com.alad1nks.dubovozki

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
        scenario.close()
        System.clearProperty("dubovozki.e2e.firebase.host")
        System.clearProperty("dubovozki.e2e.firebase.port")
    }

    @Test
    fun actualActivityLaunchesAndNavigatesAcrossP0Destinations() {
        compose.onNodeWithTag(TestTags.APP_CONTENT).assertIsDisplayed()
        compose.onNodeWithTag(TestTags.NAV_SCHEDULE).assertIsSelected()

        compose.onNodeWithTag(TestTags.NAV_SERVICES).performClick().assertIsSelected()
        compose.onNodeWithTag(TestTags.SERVICES_LINEN).assertIsDisplayed()
        compose.onNodeWithTag(TestTags.NAV_SETTINGS).performClick().assertIsSelected()
        compose.onNodeWithTag(TestTags.SETTINGS_LANGUAGE).assertIsDisplayed()
    }

    private fun seedFirebaseEmulator() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val fixture = context.assets.open("happy.json").bufferedReader().use { it.readText() }
        val projectId = Firebase.app.options.projectId
        val connection =
            URL("http://10.0.2.2:9000/.json?ns=$projectId").openConnection() as HttpURLConnection
        connection.requestMethod = "PUT"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        connection.outputStream.bufferedWriter().use { it.write(fixture) }
        check(connection.responseCode in 200..299) {
            "Firebase Emulator seed failed with HTTP ${connection.responseCode}"
        }
        connection.disconnect()
    }
}
