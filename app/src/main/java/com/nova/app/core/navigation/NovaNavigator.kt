package com.nova.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.nova.app.core.model.ScreenSpec

sealed interface AppRoute {
    data object Splash : AppRoute
    data object Onboarding : AppRoute
    data object SignIn : AppRoute
    data object Otp : AppRoute
    data object ProfileSetup : AppRoute
    data object Interests : AppRoute
    data object Home : AppRoute
    data object Discover : AppRoute
    data object ProfileDetail : AppRoute
    data object Messages : AppRoute
    data object Community : AppRoute
    data object Profile : AppRoute
    data object Story : AppRoute
    data object Feed : AppRoute
    data object AICompatibility : AppRoute
    data object MatchSuccess : AppRoute
    data object Chat : AppRoute
    data object VoiceCall : AppRoute
    data object VideoCall : AppRoute
    data object CommunityDetail : AppRoute
    data object Events : AppRoute
    data object AIMatch : AppRoute
    data object Search : AppRoute
    data object Filter : AppRoute
    data object Notifications : AppRoute
    data object Premium : AppRoute
    data object Badge : AppRoute
    data object Wallet : AppRoute
    data object Gift : AppRoute
    data object Settings : AppRoute
    data object Safety : AppRoute
    data object Admin : AppRoute
    data object DesignSystem : AppRoute
    data class Template(val spec: ScreenSpec) : AppRoute
    data class Status(val kind: ScreenStateKind) : AppRoute
}

enum class ScreenStateKind {
    Success,
    Loading,
    Empty,
    Error,
    Offline,
    Permission,
    FirstTimeUser,
    Premium,
}

enum class MainTab {
    Home,
    Discover,
    Messages,
    Community,
    Profile,
}

@Stable
class NovaNavigator(initialRoute: AppRoute = AppRoute.Splash) {
    private val stack = mutableStateListOf(initialRoute)

    val current: AppRoute
        get() = stack.last()

    val canGoBack: Boolean
        get() = stack.size > 1

    fun navigate(route: AppRoute) {
        stack.add(route)
    }

    fun replace(route: AppRoute) {
        if (stack.isEmpty()) {
            stack.add(route)
        } else {
            stack[stack.lastIndex] = route
        }
    }

    fun openTab(route: AppRoute) {
        stack.clear()
        stack.add(route)
    }

    fun reset(route: AppRoute) {
        stack.clear()
        stack.add(route)
    }

    fun back() {
        if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
        }
    }
}

val LocalNovaNavigator = compositionLocalOf<NovaNavigator> {
    error("NovaNavigator not provided")
}

@Composable
fun rememberNovaNavigator(initialRoute: AppRoute = AppRoute.Splash): NovaNavigator {
    return remember { NovaNavigator(initialRoute) }
}

fun AppRoute.isMainTab(): Boolean = when (this) {
    AppRoute.Home,
    AppRoute.Discover,
    AppRoute.Messages,
    AppRoute.Community,
    AppRoute.Profile -> true
    else -> false
}
