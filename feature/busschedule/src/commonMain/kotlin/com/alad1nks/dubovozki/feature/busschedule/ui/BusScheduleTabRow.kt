package com.alad1nks.dubovozki.feature.busschedule.ui

import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.alad1nks.dubovozki.feature.designsystem.theme.AppTheme
import com.alad1nks.dubovozki.feature.designsystem.TestTags
import com.alad1nks.dubovozki.resources.AppResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BusScheduleTabRow(
    selectedTabIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    directions: List<String> =
        listOf(
            stringResource(AppResource.String.bus_schedule_tab_moscow),
            stringResource(AppResource.String.bus_schedule_tab_dubki),
        ),
) {
    PrimaryTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
    ) {
        directions.forEachIndexed { index, direction ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onSelect(index) },
                modifier = Modifier.testTag(if (index == 0) TestTags.BUS_TAB_MOSCOW else TestTags.BUS_TAB_DUBKI),
                text = { Text(text = direction) },
            )
        }
    }
}

@Preview
@Composable
private fun BusScheduleTabRowPreview() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    AppTheme {
        BusScheduleTabRow(
            selectedTabIndex = selectedTabIndex,
            onSelect = { selectedTabIndex = it },
        )
    }
}
