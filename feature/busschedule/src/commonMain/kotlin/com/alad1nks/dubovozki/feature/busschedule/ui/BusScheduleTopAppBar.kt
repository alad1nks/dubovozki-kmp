package com.alad1nks.dubovozki.feature.busschedule.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alad1nks.dubovozki.core.model.DayOfWeekFilter
import com.alad1nks.dubovozki.core.model.StationFilter
import com.alad1nks.dubovozki.feature.designsystem.component.Spinner
import com.alad1nks.dubovozki.resources.AppResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BusScheduleTopAppBar(
    stationFilterSpinnerExpanded: Boolean,
    selectedStationFilter: StationFilter,
    onStationFilterSelect: (StationFilter) -> Unit,
    onStationFilterSpinnerClick: () -> Unit,
    onStationFilterSpinnerDismissRequest: () -> Unit,
    dayOfWeekFilterSpinnerExpanded: Boolean,
    selectedDayOfWeekFilter: DayOfWeekFilter,
    onDayOfWeekFilterSelect: (DayOfWeekFilter) -> Unit,
    onDayOfWeekFilterSpinnerClick: () -> Unit,
    onDayOfWeekFilterSpinnerDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    stationFilterList: List<StationFilter> = StationFilter.entries,
    dayOfWeekFilterList: List<DayOfWeekFilter> = DayOfWeekFilter.entries,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            BusScheduleTopAppBarSpinnerRow(
                stationFilterSpinnerExpanded = stationFilterSpinnerExpanded,
                stationFilterList = stationFilterList,
                selectedStationFilter = selectedStationFilter,
                onStationFilterSelect = onStationFilterSelect,
                onStationFilterSpinnerClick = onStationFilterSpinnerClick,
                onStationFilterSpinnerDismissRequest = onStationFilterSpinnerDismissRequest,
                dayOfWeekFilterSpinnerExpanded = dayOfWeekFilterSpinnerExpanded,
                dayOfWeekFilterList = dayOfWeekFilterList,
                selectedDayOfWeekFilter = selectedDayOfWeekFilter,
                onDayOfWeekFilterSelect = onDayOfWeekFilterSelect,
                onDayOfWeekFilterSpinnerClick = onDayOfWeekFilterSpinnerClick,
                onDayOfWeekFilterSpinnerDismissRequest = onDayOfWeekFilterSpinnerDismissRequest,
                modifier = Modifier.padding(end = 16.dp),
            )
        },
        modifier = modifier,
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        scrollBehavior = scrollBehavior,
    )
}

@Composable
private fun BusScheduleTopAppBarSpinnerRow(
    stationFilterSpinnerExpanded: Boolean,
    stationFilterList: List<StationFilter>,
    selectedStationFilter: StationFilter,
    onStationFilterSelect: (StationFilter) -> Unit,
    onStationFilterSpinnerClick: () -> Unit,
    onStationFilterSpinnerDismissRequest: () -> Unit,
    dayOfWeekFilterSpinnerExpanded: Boolean,
    dayOfWeekFilterList: List<DayOfWeekFilter>,
    selectedDayOfWeekFilter: DayOfWeekFilter,
    onDayOfWeekFilterSelect: (DayOfWeekFilter) -> Unit,
    onDayOfWeekFilterSpinnerClick: () -> Unit,
    onDayOfWeekFilterSpinnerDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
    ) {
        Spinner(
            expanded = stationFilterSpinnerExpanded,
            content = { Text(text = selectedStationFilter.text) },
            dropdownMenuContent = {
                stationFilterList.forEach { station ->
                    DropdownMenuItem(
                        text = { Text(text = station.text) },
                        onClick = { onStationFilterSelect(station) },
                    )
                }
            },
            onClick = onStationFilterSpinnerClick,
            onDismissRequest = onStationFilterSpinnerDismissRequest,
            modifier = Modifier.weight(1f),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Spinner(
            expanded = dayOfWeekFilterSpinnerExpanded,
            content = { Text(text = selectedDayOfWeekFilter.text) },
            dropdownMenuContent = {
                dayOfWeekFilterList.forEach { day ->
                    DropdownMenuItem(
                        text = { Text(text = day.text) },
                        onClick = { onDayOfWeekFilterSelect(day) },
                    )
                }
            },
            onClick = onDayOfWeekFilterSpinnerClick,
            onDismissRequest = onDayOfWeekFilterSpinnerDismissRequest,
            modifier = Modifier.weight(1f),
        )
    }
}

private inline val StationFilter.text: String @Composable get() =
    when (this) {
        StationFilter.ALL -> stringResource(AppResource.String.bus_schedule_station_filter_all)
        StationFilter.ODINTSOVO -> stringResource(AppResource.String.bus_schedule_station_filter_odintsovo)
        StationFilter.SLAVYANSKY_BULVAR ->
            stringResource(AppResource.String.bus_schedule_station_filter_slavyansky_bulvar)
        StationFilter.MOLODYOZHNAYA -> stringResource(AppResource.String.bus_schedule_station_filter_molodyozhnaya)
    }

private inline val DayOfWeekFilter.text: String @Composable get() =
    when (this) {
        DayOfWeekFilter.TODAY -> stringResource(AppResource.String.bus_schedule_day_of_week_filter_today)
        DayOfWeekFilter.TOMORROW -> stringResource(AppResource.String.bus_schedule_day_of_week_filter_tomorrow)
        DayOfWeekFilter.WEEKDAYS -> stringResource(AppResource.String.bus_schedule_day_of_week_filter_weekdays)
        DayOfWeekFilter.SATURDAY -> stringResource(AppResource.String.bus_schedule_day_of_week_filter_saturday)
        DayOfWeekFilter.SUNDAY -> stringResource(AppResource.String.bus_schedule_day_of_week_filter_sunday)
    }

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun BusScheduleTopAppBarPreview() {
    var stationsExpanded by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf(StationFilter.ODINTSOVO) }
    var daysExpanded by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf(DayOfWeekFilter.TODAY) }

    BusScheduleTopAppBar(
        stationFilterSpinnerExpanded = stationsExpanded,
        selectedStationFilter = selectedStation,
        onStationFilterSelect = {
            selectedStation = it
            stationsExpanded = false
        },
        onStationFilterSpinnerClick = { stationsExpanded = true },
        onStationFilterSpinnerDismissRequest = { stationsExpanded = false },
        dayOfWeekFilterSpinnerExpanded = daysExpanded,
        selectedDayOfWeekFilter = selectedDay,
        onDayOfWeekFilterSelect = {
            selectedDay = it
            daysExpanded = false
        },
        onDayOfWeekFilterSpinnerClick = { daysExpanded = true },
        onDayOfWeekFilterSpinnerDismissRequest = { daysExpanded = false },
    )
}
