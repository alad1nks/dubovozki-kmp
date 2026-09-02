package com.alad1nks.dubovozki.feature.busschedule.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.alad1nks.dubovozki.core.model.Bus
import com.alad1nks.dubovozki.core.model.DayOfWeekFilter
import com.alad1nks.dubovozki.core.model.StationFilter
import com.alad1nks.dubovozki.feature.busschedule.model.BusScheduleTopAppBarUiState
import com.alad1nks.dubovozki.feature.busschedule.model.BusScheduleUiState
import com.alad1nks.dubovozki.feature.busschedule.model.BusUi
import com.alad1nks.dubovozki.feature.busschedule.reminder.BusReminderLauncher
import com.alad1nks.dubovozki.feature.busschedule.reminder.BusReminderMethod
import com.alad1nks.dubovozki.feature.busschedule.reminder.BusReminderRequest
import com.alad1nks.dubovozki.feature.busschedule.reminder.BusReminderResult
import com.alad1nks.dubovozki.feature.busschedule.reminder.rememberBusReminderLauncher
import com.alad1nks.dubovozki.feature.designsystem.TestTags
import com.alad1nks.dubovozki.feature.designsystem.component.LoadingState
import com.alad1nks.dubovozki.feature.designsystem.component.MessageState
import com.alad1nks.dubovozki.feature.designsystem.component.OfflineBanner
import com.alad1nks.dubovozki.feature.designsystem.e2eTestTag
import com.alad1nks.dubovozki.resources.AppResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BusScheduleRoute(
    viewModel: BusScheduleViewModel,
    modifier: Modifier = Modifier,
) {
    val topAppBarUiState by viewModel.topAppBarUiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var reminderResult by remember { mutableStateOf<BusReminderResult?>(null) }
    val reminderLauncher: BusReminderLauncher =
        rememberBusReminderLauncher { result -> reminderResult = result }
    val reminderResultMessage =
        when (val result = reminderResult) {
            is BusReminderResult.Scheduled ->
                stringResource(
                    when (result.method) {
                        BusReminderMethod.ALARM -> AppResource.String.bus_reminder_success_alarm
                        BusReminderMethod.NOTIFICATION -> AppResource.String.bus_reminder_success_notification
                    },
                )
            BusReminderResult.PermissionDenied ->
                stringResource(AppResource.String.bus_reminder_permission_denied)
            BusReminderResult.TooLate -> stringResource(AppResource.String.bus_reminder_too_late)
            BusReminderResult.Unsupported -> stringResource(AppResource.String.bus_reminder_unsupported)
            BusReminderResult.Failed -> stringResource(AppResource.String.bus_reminder_failed)
            null -> null
        }
    LaunchedEffect(reminderResultMessage) {
        reminderResultMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            reminderResult = null
        }
    }

    BusScheduleScreen(
        topAppBarUiState = topAppBarUiState,
        uiState = uiState,
        onStationFilterSelect = viewModel::selectStationFilter,
        onStationFilterSpinnerClick = viewModel::expandStationFilterSpinner,
        onStationFilterSpinnerDismissRequest = viewModel::hideStationFilterSpinner,
        onDayOfWeekFilterSelect = viewModel::selectDayOfWeekFilter,
        onDayOfWeekFilterSpinnerClick = viewModel::expandDayOfWeekFilterSpinner,
        onDayOfWeekFilterSpinnerDismissRequest = viewModel::hideDayOfWeekFilterSpinner,
        onResetFilters = viewModel::resetFilters,
        onRefresh = viewModel::refresh,
        supportedReminderMethods = reminderLauncher.supportedMethods,
        onScheduleReminder = reminderLauncher.launch,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BusScheduleScreen(
    topAppBarUiState: BusScheduleTopAppBarUiState,
    uiState: BusScheduleUiState,
    onStationFilterSelect: (StationFilter) -> Unit,
    onStationFilterSpinnerClick: () -> Unit,
    onStationFilterSpinnerDismissRequest: () -> Unit,
    onDayOfWeekFilterSelect: (DayOfWeekFilter) -> Unit,
    onDayOfWeekFilterSpinnerClick: () -> Unit,
    onDayOfWeekFilterSpinnerDismissRequest: () -> Unit,
    onResetFilters: () -> Unit,
    onRefresh: () -> Unit,
    supportedReminderMethods: Set<BusReminderMethod>,
    onScheduleReminder: (BusReminderRequest) -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    pagerState: PagerState = rememberPagerState { 2 },
    moscowState: LazyListState = rememberLazyListState(),
    dubkiState: LazyListState = rememberLazyListState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    var selectedReminder by remember { mutableStateOf<SelectedBusReminder?>(null) }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .widthIn(max = 900.dp),
        ) {
            BusScheduleTopAppBar(
                stationFilterSpinnerExpanded = topAppBarUiState.stationFilterSpinnerExpanded,
                selectedStationFilter = topAppBarUiState.selectedStationFilter,
                onStationFilterSelect = onStationFilterSelect,
                onStationFilterSpinnerClick = onStationFilterSpinnerClick,
                onStationFilterSpinnerDismissRequest = onStationFilterSpinnerDismissRequest,
                dayOfWeekFilterSpinnerExpanded = topAppBarUiState.dayOfWeekFilterSpinnerExpanded,
                selectedDayOfWeekFilter = topAppBarUiState.selectedDayOfWeekFilter,
                onDayOfWeekFilterSelect = onDayOfWeekFilterSelect,
                onDayOfWeekFilterSpinnerClick = onDayOfWeekFilterSpinnerClick,
                onDayOfWeekFilterSpinnerDismissRequest = onDayOfWeekFilterSpinnerDismissRequest,
                onRefreshClick = onRefresh,
                scrollBehavior = scrollBehavior,
            )

            BusScheduleTabRow(
                selectedTabIndex = pagerState.currentPage,
                onSelect = { page -> coroutineScope.launch { pagerState.animateScrollToPage(page) } },
            )

            when (uiState) {
                is BusScheduleUiState.Loading ->
                    LoadingState(
                        message = stringResource(AppResource.String.common_loading),
                        modifier = Modifier.fillMaxSize(),
                    )
                is BusScheduleUiState.Error ->
                    MessageState(
                        title = stringResource(AppResource.String.common_error_title),
                        supportingText = stringResource(AppResource.String.common_error_supporting),
                        actionLabel = stringResource(AppResource.String.common_retry),
                        onAction = onRefresh,
                        modifier = Modifier.fillMaxSize(),
                    )
                is BusScheduleUiState.Content -> {
                    FreshnessStatus(
                        updatedAtEpochMillis = uiState.updatedAtEpochMillis,
                        isStale = uiState.isStale,
                        onRefresh = onRefresh,
                    )
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f).e2eTestTag(TestTags.BUS_PAGER),
                    ) { page ->
                        val busList = if (page == 0) uiState.moscowBusList else uiState.dubkiBusList
                        val firstBusIndex =
                            if (page == 0) uiState.firstMoscowBusIndex else uiState.firstDubkiBusIndex
                        val listState = if (page == 0) moscowState else dubkiState

                        BusSchedulePage(
                            busList = busList,
                            firstBusIndex = firstBusIndex,
                            listState = listState,
                            positionKey =
                                "${topAppBarUiState.selectedStationFilter}:" +
                                    "${topAppBarUiState.selectedDayOfWeekFilter}:" +
                                    uiState.updatedAtEpochMillis,
                            remindersEnabled =
                                topAppBarUiState.selectedDayOfWeekFilter == DayOfWeekFilter.TODAY,
                            onResetFilters = onResetFilters,
                            onBusLongClick = { bus ->
                                bus.departureEpochMillis
                                    ?.takeIf { bus.timeDifference?.let { it > 0 } == true }
                                    ?.let { departureEpochMillis ->
                                        selectedReminder =
                                            SelectedBusReminder(
                                                bus = bus,
                                                departureEpochMillis = departureEpochMillis,
                                            )
                                    }
                            },
                            scrollBehavior = scrollBehavior,
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        )

        selectedReminder?.let { reminder ->
            BusReminderDialog(
                bus = reminder.bus,
                departureEpochMillis = reminder.departureEpochMillis,
                supportedMethods = supportedReminderMethods,
                onConfirm = { request ->
                    selectedReminder = null
                    onScheduleReminder(request)
                },
                onDismissRequest = { selectedReminder = null },
            )
        }
    }
}

@Composable
private fun FreshnessStatus(
    updatedAtEpochMillis: Long?,
    isStale: Boolean,
    onRefresh: () -> Unit,
) {
    if (isStale) {
        OfflineBanner(
            message =
                stringResource(
                    AppResource.String.common_offline_updated_at,
                    formatUpdatedAt(updatedAtEpochMillis),
                ),
            actionLabel = stringResource(AppResource.String.common_retry),
            onAction = onRefresh,
        )
    } else if (updatedAtEpochMillis != null) {
        Text(
            text =
                stringResource(
                    AppResource.String.common_updated_at,
                    formatUpdatedAt(updatedAtEpochMillis),
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BusSchedulePage(
    busList: List<BusUi>,
    firstBusIndex: Int?,
    listState: LazyListState,
    positionKey: String,
    remindersEnabled: Boolean,
    onResetFilters: () -> Unit,
    onBusLongClick: (BusUi) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    LaunchedEffect(positionKey) {
        firstBusIndex?.let { listState.scrollToItem(it) }
    }

    if (busList.isEmpty()) {
        MessageState(
            title = stringResource(AppResource.String.bus_schedule_empty_title),
            supportingText = stringResource(AppResource.String.bus_schedule_empty_supporting),
            actionLabel = stringResource(AppResource.String.bus_schedule_reset_filters),
            onAction = onResetFilters,
            modifier = modifier.fillMaxSize(),
            stateTestTag = TestTags.BUS_EMPTY,
            actionTestTag = TestTags.BUS_RESET_FILTERS,
        )
        return
    }

    Column(modifier = modifier.fillMaxSize()) {
        NextDepartureCard(
            bus = firstBusIndex?.let(busList::getOrNull),
            onGoToNext = {
                firstBusIndex?.let { index ->
                    coroutineScope.launch { listState.animateScrollToItem(index) }
                }
            },
        )
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            state = listState,
        ) {
            items(
                items = busList,
                key = { bus -> bus.id },
            ) { bus ->
                BusListItem(
                    dayTime = bus.dayTime,
                    timeDifference = bus.timeDifference,
                    station = bus.station,
                    onLongClick =
                        if (remindersEnabled && bus.timeDifference?.let { it > 0 } == true) {
                            { onBusLongClick(bus) }
                        } else {
                            null
                        },
                    modifier = Modifier.e2eTestTag(TestTags.bus(bus.id)),
                )
            }
        }
    }
}

private data class SelectedBusReminder(
    val bus: BusUi,
    val departureEpochMillis: Long,
)

@Composable
private fun NextDepartureCard(
    bus: BusUi?,
    onGoToNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(12.dp)
                .e2eTestTag(TestTags.BUS_NEXT_CARD)
                .semantics(mergeDescendants = true) {},
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(AppResource.String.bus_schedule_next_departure),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
            )
            if (bus == null) {
                Text(
                    text = stringResource(AppResource.String.bus_schedule_no_more_today),
                    style = MaterialTheme.typography.titleMedium,
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = bus.dayTime, style = MaterialTheme.typography.headlineMedium)
                        Text(
                            text = bus.station.text,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        bus.timeDifference?.let {
                            Text(
                                text = timeDifferenceText(it),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                    TextButton(
                        onClick = onGoToNext,
                        modifier = Modifier.e2eTestTag(TestTags.BUS_NEXT_GO),
                    ) {
                        Text(text = stringResource(AppResource.String.bus_schedule_go_to_next))
                    }
                }
            }
        }
    }
}

private val Bus.Station.text: String
    @Composable get() =
        when (this) {
            Bus.Station.ODINTSOVO -> stringResource(AppResource.String.bus_schedule_station_odintsovo)
            Bus.Station.SLAVYANSKY_BULVAR ->
                stringResource(AppResource.String.bus_schedule_station_slavyansky_bulvar)
            Bus.Station.MOLODYOZHNAYA ->
                stringResource(AppResource.String.bus_schedule_station_molodyozhnaya)
        }

private fun formatUpdatedAt(updatedAtEpochMillis: Long?): String {
    if (updatedAtEpochMillis == null) return "—"
    val dateTime =
        Instant.fromEpochMilliseconds(updatedAtEpochMillis + MOSCOW_OFFSET_MILLIS)
            .toLocalDateTime(TimeZone.UTC)
    return "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
}

private const val MOSCOW_OFFSET_MILLIS = 3 * 60 * 60 * 1_000L
