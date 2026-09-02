package com.alad1nks.dubovozki.feature.services.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.LocalLaundryService
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.alad1nks.dubovozki.core.model.ServicesScheduleType
import com.alad1nks.dubovozki.feature.designsystem.TestTags
import com.alad1nks.dubovozki.feature.designsystem.component.LoadingState
import com.alad1nks.dubovozki.feature.designsystem.component.MessageState
import com.alad1nks.dubovozki.feature.designsystem.component.OfflineBanner
import com.alad1nks.dubovozki.feature.designsystem.e2eTestTag
import com.alad1nks.dubovozki.feature.services.model.ServicesUiState
import com.alad1nks.dubovozki.resources.AppResource
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant

@Composable
internal fun ServicesRoute(
    viewModel: ServicesViewModel,
    navigateToServicesSchedule: (ServicesScheduleType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    ServicesScreen(
        uiState = uiState,
        onLinenRoomClick = { navigateToServicesSchedule(ServicesScheduleType.LINEN_ROOM) },
        onRefresh = viewModel::refresh,
        modifier = modifier,
    )
}

@Composable
internal fun ServicesScreen(
    uiState: ServicesUiState,
    onLinenRoomClick: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val linkErrorMessage = stringResource(AppResource.String.services_link_error)
    val safeContactLink = uiState.let { (it as? ServicesUiState.Content)?.contactLink }.takeIfSupported()
    val safeDonutLink = uiState.let { (it as? ServicesUiState.Content)?.donutLink }.takeIfSupported()

    fun openUri(uri: String) {
        coroutineScope.launch {
            try {
                uriHandler.openUri(uri)
            } catch (_: Exception) {
                snackbarHostState.showSnackbar(linkErrorMessage)
            }
        }
    }

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
            ServicesTopAppBar(onRefreshClick = onRefresh)
            LinenRoomItem(onClick = onLinenRoomClick)

            when (uiState) {
                is ServicesUiState.Loading ->
                    LoadingState(
                        message = stringResource(AppResource.String.common_loading),
                        modifier = Modifier.fillMaxSize(),
                    )
                is ServicesUiState.Error ->
                    MessageState(
                        title = stringResource(AppResource.String.common_error_title),
                        supportingText = stringResource(AppResource.String.common_error_supporting),
                        actionLabel = stringResource(AppResource.String.common_retry),
                        onAction = onRefresh,
                        modifier = Modifier.fillMaxSize(),
                    )
                is ServicesUiState.Content -> {
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
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    ExternalServicesContent(
                        contactLink = safeContactLink,
                        donutLink = safeDonutLink,
                        onOpenUri = ::openUri,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun LinenRoomItem(
    onClick: () -> Unit,
) {
    ServiceItem(
        onClick = onClick,
        headlineText = stringResource(AppResource.String.services_linen_room_headline),
        supportingText = stringResource(AppResource.String.services_linen_room_supporting),
        leadingImageVector = Icons.Outlined.LocalLaundryService,
        modifier = Modifier.e2eTestTag(TestTags.SERVICES_LINEN),
    )
}

@Composable
private fun ExternalServicesContent(
    contactLink: String?,
    donutLink: String?,
    onOpenUri: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        contactLink?.let { link ->
            ServiceItem(
                onClick = { onOpenUri(link) },
                headlineText = stringResource(AppResource.String.services_contact_headline),
                supportingText = stringResource(AppResource.String.services_contact_supporting),
                leadingImageVector = Icons.AutoMirrored.Outlined.Message,
                opensExternalLink = true,
                modifier = Modifier.e2eTestTag(TestTags.SERVICES_CONTACT),
            )
        }
        donutLink?.let { link ->
            ServiceItem(
                onClick = { onOpenUri(link) },
                headlineText = stringResource(AppResource.String.services_donut_headline),
                supportingText = stringResource(AppResource.String.services_donut_supporting),
                leadingImageVector = Icons.Outlined.MonetizationOn,
                opensExternalLink = true,
                modifier = Modifier.e2eTestTag(TestTags.SERVICES_DONATE),
            )
        }
        if (contactLink == null && donutLink == null) {
            Text(
                text = stringResource(AppResource.String.services_links_unavailable),
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .e2eTestTag(TestTags.SERVICES_LINKS_UNAVAILABLE)
                        .padding(24.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServicesTopAppBar(
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LargeTopAppBar(
        title = { Text(text = stringResource(AppResource.String.services_top_app_bar)) },
        actions = {
            IconButton(
                onClick = onRefreshClick,
                modifier = Modifier.e2eTestTag(TestTags.SERVICES_REFRESH),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = stringResource(AppResource.String.common_refresh),
                )
            }
        },
        modifier = modifier,
    )
}

private fun String?.takeIfSupported(): String? =
    this?.takeIf { value ->
        value.startsWith("https://") || value.startsWith("http://") || value.startsWith("mailto:") ||
            value.startsWith("tg://")
    }

private fun formatUpdatedAt(updatedAtEpochMillis: Long?): String {
    if (updatedAtEpochMillis == null) return "—"
    val dateTime =
        Instant.fromEpochMilliseconds(updatedAtEpochMillis + MOSCOW_OFFSET_MILLIS)
            .toLocalDateTime(TimeZone.UTC)
    return "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
}

private const val MOSCOW_OFFSET_MILLIS = 3 * 60 * 60 * 1_000L
