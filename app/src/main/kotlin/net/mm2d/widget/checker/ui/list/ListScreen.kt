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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    navigateToExperiment: (String) -> Unit,
) {
    val viewModel: ListViewModel = viewModel()
    val appEntries by viewModel.getAppEntriesStream().collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text("Widget List") },
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
                    }
                }
            }
        }
    }
}
