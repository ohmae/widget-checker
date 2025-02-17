/*
 * Copyright (c) 2025 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.widget.checker.ui.checker

import android.app.Application
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.SizeF
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CheckerViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val context: Context
        get() = getApplication()
    private val appWidgetManager: AppWidgetManager
        get() = AppWidgetManager.getInstance(context)
    private var packageName: String = ""
    private var className: String = ""
    private val appWidgetHost: AppWidgetHost = AppWidgetHost(application, HOST_ID)
    private var providerInfo: AppWidgetProviderInfo? = null
    private var id: Int = AppWidgetManager.INVALID_APPWIDGET_ID
    private val sizeFlow = MutableStateFlow(SizeF(0f, 0f))

    fun getSizeStream(): StateFlow<SizeF> = sizeFlow.asStateFlow()
    fun getAppWidgetProviderInfo(): AppWidgetProviderInfo? = providerInfo
    fun getAppWidgetId(): Int = id

    fun initialize(
        packageName: String,
        className: String,
    ) {
        if (this.packageName == packageName && this.className == className) return
        if (this.className.isNotEmpty()) {
            clear()
        }
        this.packageName = packageName
        this.className = className
        providerInfo = appWidgetManager.installedProviders
            .find { it.provider.packageName == packageName && it.provider.className == className }
        providerInfo ?: return
        appWidgetHost.startListening()
        id = appWidgetHost.allocateAppWidgetId()
    }

    fun bind(): Boolean {
        if (id == AppWidgetManager.INVALID_APPWIDGET_ID) return false
        val providerInfo = providerInfo ?: return false
        val density = context.resources.displayMetrics.density
        val widthDp = providerInfo.minWidth / density
        val heightDp = providerInfo.minHeight / density
        val options = makeOptions(widthDp, heightDp)
        sizeFlow.value = SizeF(widthDp, heightDp)
        return appWidgetManager.bindAppWidgetIdIfAllowed(id, providerInfo.provider, options)
    }

    private fun makeOptions(
        width: Float,
        height: Float,
    ): Bundle {
        val options = bundleOf(
            AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH to width.toInt(),
            AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT to height.toInt(),
            AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH to width.toInt(),
            AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT to height.toInt(),
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            options.putParcelableArrayList(
                AppWidgetManager.OPTION_APPWIDGET_SIZES,
                arrayListOf(SizeF(width, height)),
            )
        }
        return options
    }

    fun cancel() {
        if (id == AppWidgetManager.INVALID_APPWIDGET_ID) return
        appWidgetHost.deleteAppWidgetId(id)
        id = AppWidgetManager.INVALID_APPWIDGET_ID
    }

    fun createView(): View =
        appWidgetHost.createView(context, id, providerInfo).also {
            it.setPadding(0, 0, 0, 0)
        }

    private fun clear() {
        cancel()
        providerInfo = null
    }

    fun setWidth(
        width: Float,
    ) {
        sizeFlow.update { SizeF(width, it.height) }
        val options = makeOptions(width, sizeFlow.value.height)
        appWidgetManager.updateAppWidgetOptions(id, options)
    }

    fun setHeight(
        height: Float,
    ) {
        sizeFlow.update { SizeF(it.width, height) }
        val options = makeOptions(sizeFlow.value.width, height)
        appWidgetManager.updateAppWidgetOptions(id, options)
    }

    override fun onCleared() {
        clear()
        appWidgetHost.stopListening()
        appWidgetHost.deleteHost()
    }

    companion object {
        private const val HOST_ID = 1
    }
}
