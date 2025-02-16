/*
 * Copyright (c) 2024 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.widget.checker.ui.util

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import com.google.android.material.R as MR

object Launcher {
    fun openCustomTabs(
        context: Context,
        uri: Uri,
    ): Boolean =
        runCatching {
            val scheme =
                if (context.isDarkMode()) {
                    CustomTabsIntent.COLOR_SCHEME_DARK
                } else {
                    CustomTabsIntent.COLOR_SCHEME_LIGHT
                }
            val params = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(context.resolveColor(MR.attr.colorSurfaceContainerLowest))
                .build()
            val intent = CustomTabsIntent.Builder(CustomTabsHelper.session)
                .setShowTitle(true)
                .setColorScheme(scheme)
                .setDefaultColorSchemeParams(params)
                .build()
            intent.intent.setPackage(CustomTabsHelper.packageNameToBind)
            intent.launchUrl(context, uri)
            true
        }.getOrNull() ?: false
}
