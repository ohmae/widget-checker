/*
 * Copyright (c) 2025 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.widget.checker.ui.list

import android.app.Application
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.mm2d.widget.checker.R

class ListViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val context: Context
        get() = getApplication()
    private val packageManager
        get() = context.packageManager
    private val appWidgetManager: AppWidgetManager
        get() = AppWidgetManager.getInstance(context)

    private val appEntriesFlow: MutableStateFlow<List<AppEntry>> = MutableStateFlow(emptyList())

    fun getAppEntriesStream(): StateFlow<List<AppEntry>> {
        updateAppEntries()
        return appEntriesFlow.asStateFlow()
    }

    private fun updateAppEntries() {
        viewModelScope.launch(Dispatchers.IO) {
            appEntriesFlow.value = appWidgetManager.installedProviders
                .map { it.toWidgetEntry() }
                .groupBy { it.packageName }
                .mapNotNull { it.toAppEntry() }
        }
    }

    private fun AppWidgetProviderInfo.toWidgetEntry(): WidgetEntry {
        val label = loadLabel(context.packageManager)
        val previewImage = loadPreviewImage(context, 0)
            ?: context.getDrawable(R.drawable.ic_default)!!
        return WidgetEntry(
            label,
            previewImage,
            minWidth,
            minHeight,
            provider.packageName,
            provider.className,
        )
    }

    private fun Map.Entry<String, List<WidgetEntry>>.toAppEntry(): AppEntry? {
        val applicationInfo = packageManager.getPackageInfo(key, 0)
            .applicationInfo ?: return null
        val label = applicationInfo.loadLabel(packageManager).toString()
        val icon = applicationInfo.loadIcon(packageManager)
            ?: context.getDrawable(R.drawable.ic_default)!!
        return AppEntry(
            label,
            icon,
            key,
            value,
        )
    }
}
