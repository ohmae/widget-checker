/*
 * Copyright (c) 2025 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.widget.checker.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import net.mm2d.widget.checker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    navigateToExperiment: (String) -> Unit,
    navigateToLicense: () -> Unit,
) {
    val viewModel: ListViewModel = viewModel()
    val appEntries by viewModel.getAppEntriesStream().collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Toolbar(
                scrollBehavior = scrollBehavior,
                navigateToLicense = navigateToLicense,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            items(appEntries) { appEntry ->
                AppEntry(
                    appEntry = appEntry,
                    navigateToExperiment = navigateToExperiment,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateToLicense: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = { Text(text = stringResource(id = R.string.title_list)) },
        actions = {
            IconButton(
                onClick = { menuExpanded = !menuExpanded },
            ) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.menu_license)) },
                    onClick = {
                        menuExpanded = false
                        navigateToLicense()
                    },
                )
            }
        },
    )
}

@Composable
private fun AppEntry(
    appEntry: AppEntry,
    navigateToExperiment: (String) -> Unit,
) {
    Column {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = rememberDrawablePainter(appEntry.icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp),
            )
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
            ) {
                Text(
                    text = appEntry.label,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                )
                Text(
                    text = appEntry.packageName,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                )
            }
        }
        LazyRow {
            items(appEntry.widgets) { widget ->
                WidgetEntry(
                    widget = widget,
                    navigateToExperiment = navigateToExperiment,
                )
            }
        }
    }
}

@Composable
private fun WidgetEntry(
    widget: WidgetEntry,
    navigateToExperiment: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(8.dp)
            .width(128.dp)
            .clickable {
                navigateToExperiment(widget.className)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = widget.label,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
        Image(
            painter = rememberDrawablePainter(widget.previewImage),
            contentDescription = null,
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .padding(8.dp)
                .size(96.dp),
        )
    }
}
