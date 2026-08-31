package com.alad1nks.dubovozki.feature.servicesschedule.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alad1nks.dubovozki.feature.designsystem.e2eTestTag
import androidx.compose.ui.tooling.preview.Preview
import com.alad1nks.dubovozki.core.model.ServicesScheduleType
import com.alad1nks.dubovozki.feature.designsystem.TestTags
import com.alad1nks.dubovozki.resources.AppResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ServicesScheduleTopAppBar(
    servicesScheduleType: ServicesScheduleType,
    onBackClick: () -> Unit,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text =
        when (servicesScheduleType) {
            ServicesScheduleType.LINEN_ROOM -> stringResource(AppResource.String.services_linen_room_headline)
        }

    TopAppBar(
        title = { Text(text = text) },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.e2eTestTag(TestTags.SERVICE_SCHEDULE_BACK),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(AppResource.String.common_back),
                )
            }
        },
        actions = {
            IconButton(
                onClick = onRefreshClick,
                modifier = Modifier.e2eTestTag(TestTags.SERVICE_SCHEDULE_REFRESH),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = stringResource(AppResource.String.common_refresh),
                )
            }
        },
    )
}

@Preview
@Composable
private fun ServicesScheduleTopAppBarLinenRoomPreview() {
    ServicesScheduleTopAppBar(
        servicesScheduleType = ServicesScheduleType.LINEN_ROOM,
        onBackClick = {},
        onRefreshClick = {},
    )
}
