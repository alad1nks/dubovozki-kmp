package com.alad1nks.dubovozki.feature.services.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import com.alad1nks.dubovozki.feature.services.model.ServicesUiState
import com.alad1nks.dubovozki.resources.AppResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ServicesRoute(
    viewModel: ServicesViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    ServicesScreen(
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
private fun ServicesScreen(
    uiState: ServicesUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        ServicesTopAppBar()

        when (uiState) {
            is ServicesUiState.Loading -> {}

            is ServicesUiState.Content -> {
                ServicesContent(
                    contactLink = uiState.contactLink,
                    donutLink = uiState.donutLink,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun ServicesContent(
    contactLink: String,
    donutLink: String,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier,
    ) {
        ServiceItem(
            onClick = { uriHandler.openUri(contactLink) },
            headlineText = stringResource(AppResource.String.services_contact_headline),
            supportingText = stringResource(AppResource.String.services_contact_supporting),
            leadingImageVector = Icons.AutoMirrored.Outlined.Message,
        )

        ServiceItem(
            onClick = { uriHandler.openUri(donutLink) },
            headlineText = stringResource(AppResource.String.services_donut_headline),
            supportingText = stringResource(AppResource.String.services_donut_supporting),
            leadingImageVector = Icons.Outlined.MonetizationOn,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServicesTopAppBar(
    modifier: Modifier = Modifier,
) {
    LargeTopAppBar(
        title = { Text(text = stringResource(AppResource.String.services_top_app_bar)) },
        modifier = modifier,
    )
}
