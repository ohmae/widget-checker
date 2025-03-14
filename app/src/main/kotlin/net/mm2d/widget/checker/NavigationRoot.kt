/*
 * Copyright (c) 2025 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.widget.checker

import android.content.ComponentName
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import net.mm2d.widget.checker.ui.checker.CheckerScreen
import net.mm2d.widget.checker.ui.license.LicenseScreen
import net.mm2d.widget.checker.ui.list.ListScreen

@Serializable
data object ListScreen

@Serializable
data class ExperimentScreen(
    val packageName: String,
    val className: String,
)

@Serializable
data object LicenseScreen

@Composable
fun NavigationRoot() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ListScreen,
        enterTransition = {
            fadeIn() + slideIn(initialOffset = { IntOffset(0, 0) })
        },
        exitTransition = {
            fadeOut() + slideOut(targetOffset = { IntOffset(-it.width, 0) })
        },
        popEnterTransition = {
            fadeIn() + slideIn(initialOffset = { IntOffset(0, 0) })
        },
        popExitTransition = {
            fadeOut() + slideOut(targetOffset = { IntOffset(it.width, 0) })
        },
    ) {
        composable<ListScreen> {
            ListScreen(
                navigateToExperiment = { packageName, className ->
                    navController.navigate(ExperimentScreen(packageName, className))
                },
                navigateToLicense = {
                    navController.navigate(LicenseScreen)
                },
            )
        }
        composable<ExperimentScreen> {
            val arguments = it.toRoute<ExperimentScreen>()
            CheckerScreen(
                componentName = ComponentName(arguments.packageName, arguments.className),
                popBackStack = {
                    navController.popBackStack()
                },
            )
        }
        composable<LicenseScreen> {
            LicenseScreen(
                popBackStack = {
                    navController.popBackStack()
                },
            )
        }
    }
}
