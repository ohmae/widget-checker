/*
 * Copyright (c) 2025 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.widget.checker.ui.list

import android.graphics.drawable.Drawable

data class AppEntry(
    val label: String,
    val icon: Drawable,
    val packageName: String,
    val widgets: List<WidgetEntry>,
)
