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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.alad1nks.dubovozki.feature.busschedule.model.BusScheduleUiState
import com.alad1nks.dubovozki.feature.busschedule.model.BusUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun BusScheduleRoute(
    viewModel: BusScheduleViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    BusScheduleScreen(
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
private fun BusScheduleScreen(
    uiState: BusScheduleUiState,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    pagerState: PagerState = rememberPagerState { 2 },
) {
    Column(
        modifier = modifier,
    ) {
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
                        modifier = Modifier.fillMaxSize(),
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
