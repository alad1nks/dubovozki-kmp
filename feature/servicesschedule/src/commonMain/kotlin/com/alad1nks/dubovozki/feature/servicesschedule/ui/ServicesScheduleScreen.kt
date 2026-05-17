package com.alad1nks.dubovozki.feature.servicesschedule.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.alad1nks.dubovozki.core.model.ServicesScheduleType
import com.alad1nks.dubovozki.feature.designsystem.theme.AppTheme
import com.alad1nks.dubovozki.feature.servicesschedule.model.ServicesScheduleItemUi
import com.alad1nks.dubovozki.feature.servicesschedule.model.ServicesScheduleUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek

@Composable
internal fun ServicesScheduleRoute(
    viewModel: ServicesScheduleViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val servicesScheduleType = viewModel.servicesScheduleType

    ServiceScheduleScreen(
        uiState = uiState,
        servicesScheduleType = servicesScheduleType,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
private fun ServiceScheduleScreen(
    uiState: ServicesScheduleUiState,
    servicesScheduleType: ServicesScheduleType,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState { 3 },
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val selectedTabIndex = pagerState.currentPage

    Column(
        modifier = modifier,
    ) {
        ServicesScheduleTopAppBar(
            servicesScheduleType = servicesScheduleType,
            onBackClick = onBackClick,
        )

        ServicesScheduleTabRow(
            selectedTabIndex = selectedTabIndex,
            onSelect = { page ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(page)
                }
            },
        )

        HorizontalPager(
            state = pagerState,
        ) { page ->
            when (uiState) {
                is ServicesScheduleUiState.Loading -> {
                    ServiceScheduleLoading(modifier = Modifier.fillMaxSize())
                }

                is ServicesScheduleUiState.Content -> {
                    ServiceScheduleContent(
                        firstBuildingSchedule = uiState.firstBuildingSchedule,
                        secondBuildingSchedule = uiState.secondBuildingSchedule,
                        thirdBuildingSchedule = uiState.thirdBuildingSchedule,
                        page = page,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceScheduleLoading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ServiceScheduleContent(
    firstBuildingSchedule: List<ServicesScheduleItemUi>,
    secondBuildingSchedule: List<ServicesScheduleItemUi>,
    thirdBuildingSchedule: List<ServicesScheduleItemUi>,
    page: Int,
    modifier: Modifier = Modifier,
) {
    val schedule =
        when (page) {
            0 -> firstBuildingSchedule
            1 -> secondBuildingSchedule
            else -> thirdBuildingSchedule
        }

    LazyColumn(
        modifier = modifier,
    ) {
        items(schedule) { item ->
            ServicesScheduleListItem(
                day = item.day,
                time = item.time,
                isToday = item.isToday,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ServiceScheduleScreenLoadingPreview() {
    AppTheme {
        ServiceScheduleScreen(
            uiState = ServicesScheduleUiState.Loading,
            servicesScheduleType = ServicesScheduleType.LINEN_ROOM,
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ServiceScheduleScreenDataPreview() {
    val serviceItemList =
        DayOfWeek.entries.map { day ->
            ServicesScheduleItemUi(
                day = day,
                time = "09:30-18:00",
                isToday = false,
            )
        }

    val uiState =
        ServicesScheduleUiState.Content(
            firstBuildingSchedule = serviceItemList,
            secondBuildingSchedule = serviceItemList,
            thirdBuildingSchedule = serviceItemList,
        )

    AppTheme {
        ServiceScheduleScreen(
            uiState = uiState,
            servicesScheduleType = ServicesScheduleType.LINEN_ROOM,
            onBackClick = {},
        )
    }
}
