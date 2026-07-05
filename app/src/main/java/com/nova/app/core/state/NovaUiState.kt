package com.nova.app.core.state

sealed interface NovaLoadState<out T> {
    data object Loading : NovaLoadState<Nothing>
    data class Success<T>(val data: T) : NovaLoadState<T>
    data object Empty : NovaLoadState<Nothing>
    data class Error(val message: String, val actionLabel: String = "Retry") : NovaLoadState<Nothing>
    data object Offline : NovaLoadState<Nothing>
    data object PermissionRequired : NovaLoadState<Nothing>
    data object FirstTimeUser : NovaLoadState<Nothing>
    data object Premium : NovaLoadState<Nothing>
}
