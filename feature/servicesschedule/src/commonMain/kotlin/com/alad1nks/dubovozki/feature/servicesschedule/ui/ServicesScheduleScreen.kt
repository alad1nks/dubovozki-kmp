package com.alad1nks.dubovozki.feature.servicesschedule.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alad1nks.dubovozki.core.model.ServicesScheduleType
import com.alad1nks.dubovozki.feature.designsystem.component.LoadingState
import com.alad1nks.dubovozki.feature.designsystem.component.MessageState
import com.alad1nks.dubovozki.feature.designsystem.component.OfflineBanner
import com.alad1nks.dubovozki.feature.servicesschedule.model.ServicesScheduleItemUi
import com.alad1nks.dubovozki.feature.servicesschedule.model.ServicesScheduleUiState
import com.alad1nks.dubovozki.resources.AppResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant

@Composable
internal fun ServicesScheduleRoute(
    viewModel: ServicesScheduleViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    ServiceScheduleScreen(
        uiState = uiState,
        servicesScheduleType = viewModel.servicesScheduleType,
        onBackClick = onBackClick,
        onRefresh = viewModel::refresh,
        modifier = modifier,
    )
}

@Composable
private fun ServiceScheduleScreen(
    uiState: ServicesScheduleUiState,
    servicesScheduleType: ServicesScheduleType,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState { 3 },
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .widthIn(max = 720.dp),
        ) {
            ServicesScheduleTopAppBar(
                servicesScheduleType = servicesScheduleType,
                onBackClick = onBackClick,
                onRefreshClick = onRefresh,
            )
            ServicesScheduleTabRow(
                selectedTabIndex = pagerState.currentPage,
                onSelect = { page -> coroutineScope.launch { pagerState.animateScrollToPage(page) } },
            )

            when (uiState) {
                is ServicesScheduleUiState.Loading ->
                    LoadingState(
                        message = stringResource(AppResource.String.common_loading),
                        modifier = Modifier.fillMaxSize(),
                    )
                is ServicesScheduleUiState.Error ->
                    MessageState(
                        title = stringResource(AppResource.String.common_error_title),
                        supportingText = stringResource(AppResource.String.common_error_supporting),
                        actionLabel = stringResource(AppResource.String.common_retry),
                        onAction = onRefresh,
                        modifier = Modifier.fillMaxSize(),
                    )
                is ServicesScheduleUiState.Content -> {
                    if (uiState.isStale) {
                        OfflineBanner(
                            message =
                                stringResource(
                                    AppResource.String.common_offline_updated_at,
                                    formatUpdatedAt(uiState.updatedAtEpochMillis),
                                ),
                            actionLabel = stringResource(AppResource.String.common_retry),
                            onAction = onRefresh,
                        )
                    } else if (uiState.updatedAtEpochMillis != null) {
                        Text(
                            text =
                                stringResource(
                                    AppResource.String.common_updated_at,
                                    formatUpdatedAt(uiState.updatedAtEpochMillis),
                                ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f),
                    ) { page ->
                        val schedule =
                            when (page) {
                                0 -> uiState.firstBuildingSchedule
                                1 -> uiState.secondBuildingSchedule
                                else -> uiState.thirdBuildingSchedule
                            }
                        ServiceScheduleContent(
                            schedule = schedule,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceScheduleContent(
    schedule: List<ServicesScheduleItemUi>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    if (schedule.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(AppResource.String.services_schedule_empty),
                modifier = Modifier.padding(24.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        return
    }

    LaunchedEffect(schedule) {
        schedule.indexOfFirst { it.isToday }
            .takeIf { it > 0 }
            ?.let { listState.scrollToItem(it) }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
    ) {
        items(
            items = schedule,
            key = { item -> "${item.day}:${item.time}" },
        ) { item ->
            ServicesScheduleListItem(
                day = item.day,
                time = item.time,
                isToday = item.isToday,
            )
        }
    }
}

private fun formatUpdatedAt(updatedAtEpochMillis: Long?): String {
    if (updatedAtEpochMillis == null) return "—"
    val dateTime =
        Instant.fromEpochMilliseconds(updatedAtEpochMillis)
            .toLocalDateTime(TimeZone.of("Europe/Moscow"))
    return "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
}
