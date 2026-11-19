package com.alad1nks.dubovozki.feature.busschedule.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import com.alad1nks.dubovozki.core.model.Bus
import com.alad1nks.dubovozki.feature.designsystem.theme.AppTheme
import java.util.Locale
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class BusListItemTextTest {
    @Test
    fun englishLabelsLoadAndUpdate() = checkLabels("en", "Odintsovo", "in 1 minute", "in 2 minutes")

    @Test
    fun russianLabelsLoadAndUpdate() = checkLabels("ru", "Одинцово", "через 1 минута", "через 2 минуты")

    @Test
    fun kazakhLabelsLoadAndUpdate() = checkLabels("kk", "Одинцово", "1 минут кейін", "2 минут кейін")

    private fun checkLabels(language: String, station: String, initial: String, updated: String) {
        val previousLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag(language))
            runDesktopComposeUiTest {
                val remaining = mutableStateOf(60_000)
                setContent {
                    AppTheme {
                        BusListItem("12:00", remaining.value, Bus.Station.ODINTSOVO)
                    }
                }
                waitForIdle()
                onNodeWithText(station, substring = true).assertExists()
                onNodeWithText(initial, substring = true).assertExists()
                runOnIdle { remaining.value = 120_000 }
                waitForIdle()
                onNodeWithText(updated, substring = true).assertExists()
            }
        } finally {
            Locale.setDefault(previousLocale)
        }
    }
}
