package com.alad1nks.dubovozki.feature.busschedule.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.alad1nks.dubovozki.core.model.DayOfWeekFilter
import com.alad1nks.dubovozki.core.model.StationFilter
import com.alad1nks.dubovozki.feature.busschedule.model.BusScheduleTopAppBarUiState
import com.alad1nks.dubovozki.feature.busschedule.model.BusScheduleUiState
import com.alad1nks.dubovozki.feature.busschedule.model.BusUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BusScheduleRoute(
    viewModel: BusScheduleViewModel,
    modifier: Modifier = Modifier,
) {
    val topAppBarUiState by viewModel.topAppBarUiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    BusScheduleScreen(
        topAppBarUiState = topAppBarUiState,
        uiState = uiState,
        onStationFilterSelect = viewModel::selectStationFilter,
        onStationFilterSpinnerClick = viewModel::expandStationFilterSpinner,
        onStationFilterSpinnerDismissRequest = viewModel::hideStationFilterSpinner,
        onDayOfWeekFilterSelect = viewModel::selectDayOfWeekFilter,
        onDayOfWeekFilterSpinnerClick = viewModel::expandDayOfWeekFilterSpinner,
        onDayOfWeekFilterSpinnerDismissRequest = viewModel::hideDayOfWeekFilterSpinner,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BusScheduleScreen(
    topAppBarUiState: BusScheduleTopAppBarUiState,
    uiState: BusScheduleUiState,
    onStationFilterSelect: (StationFilter) -> Unit,
    onStationFilterSpinnerClick: () -> Unit,
    onStationFilterSpinnerDismissRequest: () -> Unit,
    onDayOfWeekFilterSelect: (DayOfWeekFilter) -> Unit,
    onDayOfWeekFilterSpinnerClick: () -> Unit,
    onDayOfWeekFilterSpinnerDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    pagerState: PagerState = rememberPagerState { 2 },
) {
    Column(
        modifier = modifier,
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
            scrollBehavior = scrollBehavior,
        )

        BusScheduleTabRow(
            selectedTabIndex = pagerState.currentPage,
            onSelect = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(it)
                }
            },
        )

        HorizontalPager(
            state = pagerState,
        ) { page ->
            when (uiState) {
                is BusScheduleUiState.Loading -> {}
                is BusScheduleUiState.Content -> {
                    BusScheduleContent(
                        moscowBusList = uiState.moscowBusList,
                        dubkiBusList = uiState.dubkiBusList,
                        page = page,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .nestedScroll(scrollBehavior.nestedScrollConnection),
                    )
                }
            }
        }
    }
}

@Composable
private fun BusScheduleContent(
    moscowBusList: List<BusUi>,
    dubkiBusList: List<BusUi>,
    page: Int,
    modifier: Modifier = Modifier,
    moscowState: LazyListState = rememberLazyListState(),
    dubkiState: LazyListState = rememberLazyListState(),
) {
    when (page) {
        0 -> {
            BusListLazyColumn(
                busList = moscowBusList,
                state = moscowState,
                modifier = modifier,
            )
        }
        else -> {
            BusListLazyColumn(
                busList = dubkiBusList,
                state = dubkiState,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun BusListLazyColumn(
    busList: List<BusUi>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier,
        state = state,
    ) {
        items(
            items = busList,
            key = { bus -> bus.id },
        ) { bus ->
            BusListItem(
                dayTime = bus.dayTime,
                timeDifference = bus.timeDifference,
                station = bus.station,
            )
        }
    }
}
