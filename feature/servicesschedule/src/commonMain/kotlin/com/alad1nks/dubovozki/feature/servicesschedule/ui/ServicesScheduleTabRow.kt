package com.alad1nks.dubovozki.feature.servicesschedule.ui

import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.alad1nks.dubovozki.feature.designsystem.TestTags
import com.alad1nks.dubovozki.feature.designsystem.e2eTestTag
import com.alad1nks.dubovozki.resources.AppResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ServicesScheduleTabRow(
    selectedTabIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    buildings: List<String> =
        listOf(
            stringResource(AppResource.String.services_schedule_building_1),
            stringResource(AppResource.String.services_schedule_building_2),
            stringResource(AppResource.String.services_schedule_building_3),
        ),
) {
    PrimaryTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
    ) {
        buildings.forEachIndexed { index, building ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onSelect(index) },
                modifier =
                    Modifier.e2eTestTag(
                        when (index) {
                            0 -> TestTags.SERVICE_SCHEDULE_BUILDING_1
                            1 -> TestTags.SERVICE_SCHEDULE_BUILDING_2
                            else -> TestTags.SERVICE_SCHEDULE_BUILDING_3
                        },
                    ),
                text = { Text(text = building) },
            )
        }
    }
}

@Preview
@Composable
private fun ServiceScheduleTabRowPreview() {
    ServicesScheduleTabRow(
        selectedTabIndex = 0,
        onSelect = {},
    )
}
