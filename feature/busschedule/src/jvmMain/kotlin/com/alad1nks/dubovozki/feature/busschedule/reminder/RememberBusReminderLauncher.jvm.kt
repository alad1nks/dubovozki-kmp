package com.alad1nks.dubovozki.feature.busschedule.reminder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

@Composable
internal actual fun rememberBusReminderLauncher(
    onResult: (BusReminderResult) -> Unit,
): BusReminderLauncher {
    val currentOnResult = rememberUpdatedState(onResult)
    return remember {
        BusReminderLauncher(emptySet()) {
            currentOnResult.value(BusReminderResult.Unsupported)
        }
    }
}
