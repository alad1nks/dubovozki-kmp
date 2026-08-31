package com.alad1nks.dubovozki.e2e

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.alad1nks.dubovozki.core.storage.common.AppPreferences
import com.alad1nks.dubovozki.feature.designsystem.TestTags
import com.alad1nks.dubovozki.shared.CommonModules
import com.alad1nks.dubovozki.shared.PlatformModules
import com.alad1nks.dubovozki.shared.ui.App
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.dsl.koinApplication
import java.io.File
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class DesktopEntryPointE2ETest {
    @Test
    fun realPlatformModulesReadRestAgainOnRefreshAndPersistSettings() {
        val requests = ConcurrentHashMap<String, Int>()
        val server =
            HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply {
                json("/busSchedule.json", BUS_JSON, requests)
                json("/services.json", SERVICES_JSON, requests)
                json("/servicesSchedule/linenRoom.json", SERVICE_SCHEDULE_JSON, requests)
                start()
            }
        val dataStoreFile =
            File(
                System.getProperty("java.io.tmpdir"),
                "dubovozki-e2e-${System.nanoTime()}/preferences.preferences_pb",
            )
        System.setProperty("dubovozki.e2e.firebase.url", "http://127.0.0.1:${server.address.port}")
        System.setProperty("dubovozki.e2e.datastore.path", dataStoreFile.absolutePath)
        check(dataStoreFile.parentFile.mkdirs())

        try {
            runComposeUiTest {
                val showContent = mutableStateOf(true)
                val isolatedKoin =
                    koinApplication {
                        modules(CommonModules + PlatformModules)
                    }
                setContent {
                    if (showContent.value) App(isolatedKoinApplication = isolatedKoin)
                }

                waitUntil(timeoutMillis = 10_000) { requests["/busSchedule.json"] == 1 }
                click(TestTags.BUS_REFRESH)
                waitUntil(timeoutMillis = 10_000) { requests["/busSchedule.json"] == 2 }

                click(TestTags.NAV_SETTINGS)
                click(TestTags.SETTINGS_THEME)
                click(TestTags.theme("DARK"))
                click(TestTags.SETTINGS_LANGUAGE)
                click(TestTags.language("ENGLISH"))

                val preferences = isolatedKoin.koin.get<AppPreferences>()
                runBlocking {
                    assertEquals("dark", preferences.getString("theme_mode").first { it != null })
                    assertEquals("en", preferences.getString("language").first { it != null })
                }

                runOnUiThread { showContent.value = false }
                waitForIdle()
                isolatedKoin.close()
            }
        } finally {
            server.stop(0)
            System.clearProperty("dubovozki.e2e.firebase.url")
            System.clearProperty("dubovozki.e2e.datastore.path")
        }
    }

    private fun HttpServer.json(
        path: String,
        body: String,
        requests: ConcurrentHashMap<String, Int>,
    ) {
        createContext(path) { exchange ->
            requests.merge(path, 1, Int::plus)
            val bytes = body.encodeToByteArray()
            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(200, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }
    }

    private fun androidx.compose.ui.test.ComposeUiTest.click(tag: String) {
        val node = onNodeWithTag(tag)
        runOnUiThread { node.performClick() }
        waitForIdle()
    }

    private companion object {
        const val BUS_JSON =
            """{"revision":"desktop-v1","busList":[{"id":1,"dayOfWeek":2,"dayTime":32400000,"dayTimeString":"09:00","direction":"msk","station":"odn"}]}"""
        const val SERVICES_JSON =
            """{"contactLink":"https://example.test/contact","donutLink":"https://example.test/donate"}"""
        const val SERVICE_SCHEDULE_JSON =
            """{"firstBuilding":[{"day":1,"time":"10:00-12:00"}],"secondBuilding":[],"thirdBuilding":[]}"""
    }
}
