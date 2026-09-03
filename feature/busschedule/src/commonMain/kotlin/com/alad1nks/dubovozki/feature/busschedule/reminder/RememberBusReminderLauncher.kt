package com.alad1nks.dubovozki.feature.busschedule.reminder

import androidx.compose.runtime.Composable

@Composable
internal expect fun rememberBusReminderLauncher(
    onResult: (BusReminderResult) -> Unit,
): BusReminderLauncher
