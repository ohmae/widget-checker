/*
 * Copyright (c) 2025 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.widget.checker.ui.checker

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.mm2d.widget.checker.R

@Composable
fun CheckerScreen(
    componentName: ComponentName,
    popBackStack: () -> Unit,
) {
    val viewModel: CheckerViewModel = viewModel()
    var appWidgetId by remember(componentName) { mutableIntStateOf(AppWidgetManager.INVALID_APPWIDGET_ID) }
    val permissionLauncher = rememberLauncherForActivityResult(StartActivityForResult()) {
        popBackStack()
    }
    val configureLauncher = rememberLauncherForActivityResult(StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            appWidgetId = viewModel.getAppWidgetId()
            return@rememberLauncherForActivityResult
        }
        viewModel.cancel()
        popBackStack()
    }
    viewModel.initialize(componentName)
    LaunchedEffect(componentName) {
        val providerInfo = viewModel.getAppWidgetProviderInfo()
        if (providerInfo == null) {
            popBackStack()
            return@LaunchedEffect
        }
        val id = viewModel.getAppWidgetId()
        if (!viewModel.bind()) {
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, providerInfo.provider)
            permissionLauncher.launch(intent)
            return@LaunchedEffect
        }
        if (providerInfo.configure != null) {
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)
            intent.component = providerInfo.configure
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            configureLauncher.launch(intent)
            return@LaunchedEffect
        }
        appWidgetId = id
    }
    val providerInfo = viewModel.getAppWidgetProviderInfo() ?: return
    val density = LocalContext.current.resources.displayMetrics.density
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { Toolbar() },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                Spacer(modifier = Modifier.weight(1f))
            } else {
                val scope = rememberCoroutineScope()
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    factory = { context ->
                        val widgetView = viewModel.createView()
                        viewModel.getSizeStream()
                            .onEach {
                                widgetView.layoutParams = LayoutParams(
                                    (it.width * density).toInt(),
                                    (it.height * density).toInt(),
                                ).also {
                                    it.gravity = Gravity.CENTER
                                }
                            }
                            .launchIn(scope)
                        FrameLayout(context).also {
                            it.addView(widgetView)
                            it.setBackgroundColor(0xFF808080.toInt())
                        }
                    },
                )
            }

            var width by remember { mutableIntStateOf((providerInfo.minWidth / density).toInt()) }
            var height by remember { mutableIntStateOf((providerInfo.minHeight / density).toInt()) }
            val widthRange = 10f..560f
            val heightRange = 10f..560f
            var widthPosition by remember {
                mutableFloatStateOf((width - widthRange.start) / (widthRange.endInclusive - widthRange.start))
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "width",
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .width(40.dp),
                )
                Slider(
                    value = widthPosition,
                    onValueChange = {
                        widthPosition = it
                        width = lerp(widthRange.start, widthRange.endInclusive, it).toInt()
                        viewModel.setWidth(width.toFloat())
                    },
                    modifier = Modifier
                        .weight(1f),
                )
                Text(
                    text = width.toString(),
                    textAlign = TextAlign.End,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .width(24.dp),
                )
            }
            var heightPosition by remember {
                mutableFloatStateOf((height - heightRange.start) / (heightRange.endInclusive - heightRange.start))
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "height",
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .width(40.dp),
                )
                Slider(
                    value = heightPosition,
                    onValueChange = {
                        heightPosition = it
                        height = lerp(heightRange.start, heightRange.endInclusive, it).toInt()
                        viewModel.setHeight(height.toFloat())
                    },
                    modifier = Modifier
                        .weight(1f),
                )
                Text(
                    text = height.toString(),
                    textAlign = TextAlign.End,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .width(24.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar() {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.title_checker)) },
    )
}
