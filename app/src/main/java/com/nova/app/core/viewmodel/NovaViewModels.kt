package com.nova.app.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nova.app.core.domain.CompleteAuthUseCase
import com.nova.app.core.domain.CompleteOnboardingUseCase
import com.nova.app.core.domain.CompleteProfileUseCase
import com.nova.app.core.domain.JoinEventUseCase
import com.nova.app.core.domain.LikeCandidateUseCase
import com.nova.app.core.domain.ObserveChatUseCase
import com.nova.app.core.domain.ObserveCommunityUseCase
import com.nova.app.core.domain.ObserveDiscoverUseCase
import com.nova.app.core.domain.ObserveHomeUseCase
import com.nova.app.core.domain.ObserveMessagesUseCase
import com.nova.app.core.domain.ObserveProfileUseCase
import com.nova.app.core.domain.ObserveSessionUseCase
import com.nova.app.core.domain.ObserveSettingsUseCase
import com.nova.app.core.domain.SaveCandidateUseCase
import com.nova.app.core.domain.SendMessageUseCase
import com.nova.app.core.domain.SkipCandidateUseCase
import com.nova.app.core.domain.SuperLikeCandidateUseCase
import com.nova.app.core.domain.ToggleIncognitoUseCase
import com.nova.app.core.domain.TogglePremiumUseCase
import com.nova.app.core.domain.ToggleThemeUseCase
import com.nova.app.core.domain.ToggleTopicUseCase
import com.nova.app.core.domain.ToggleTravelModeUseCase
import com.nova.app.core.domain.UpdateLanguageUseCase
import com.nova.app.core.model.AppSettings
import com.nova.app.core.model.ChatUiState
import com.nova.app.core.model.CommunityUiState
import com.nova.app.core.model.DiscoverUiState
import com.nova.app.core.model.HomeUiState
import com.nova.app.core.model.LaunchUiState
import com.nova.app.core.model.MessagesUiState
import com.nova.app.core.model.ProfileUiState
import com.nova.app.core.model.SessionState
import com.nova.app.core.navigation.AppRoute
import com.nova.app.core.state.NovaLoadState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

class LaunchViewModel(
    observeSession: ObserveSessionUseCase,
) : ViewModel() {
    val uiState: StateFlow<LaunchUiState> = observeSession()
        .map { session -> LaunchUiState(target = resolveTarget(session)) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, LaunchUiState(AppRoute.Onboarding))

    private fun resolveTarget(session: SessionState): AppRoute {
        return when {
            session.isFirstLaunch || !session.onboardingCompleted -> AppRoute.Onboarding
            !session.otpVerified -> AppRoute.SignIn
            !session.profileCompleted -> AppRoute.ProfileSetup
            else -> AppRoute.Home
        }
    }
}

class FlowViewModel(
    observeSession: ObserveSessionUseCase,
    observeSettings: ObserveSettingsUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    private val completeAuthUseCase: CompleteAuthUseCase,
    private val completeProfileUseCase: CompleteProfileUseCase,
    private val toggleThemeUseCase: ToggleThemeUseCase,
    private val togglePremiumUseCase: TogglePremiumUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase,
    private val toggleIncognitoUseCase: ToggleIncognitoUseCase,
    private val toggleTravelModeUseCase: ToggleTravelModeUseCase,
) : ViewModel() {
    val session: StateFlow<SessionState> = observeSession()
    val settings: StateFlow<AppSettings> = observeSettings()

    fun completeOnboarding() = viewModelScope.launch { completeOnboardingUseCase() }
    fun completeAuth() = viewModelScope.launch { completeAuthUseCase() }
    fun completeProfile() = viewModelScope.launch { completeProfileUseCase() }
    fun toggleTheme() = viewModelScope.launch { toggleThemeUseCase() }
    fun togglePremium() = viewModelScope.launch { togglePremiumUseCase() }
    fun updateLanguage(language: String) = viewModelScope.launch { updateLanguageUseCase(language) }
    fun toggleIncognito() = viewModelScope.launch { toggleIncognitoUseCase() }
    fun toggleTravelMode() = viewModelScope.launch { toggleTravelModeUseCase() }
}

class HomeViewModel(
    observeHome: ObserveHomeUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<NovaLoadState<HomeUiState>>(NovaLoadState.Loading)
    val uiState: StateFlow<NovaLoadState<HomeUiState>> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(260)
            observeHome().collect { state ->
                _uiState.value = NovaLoadState.Success(state)
            }
        }
    }
}

class DiscoverViewModel(
    observeDiscover: ObserveDiscoverUseCase,
    private val likeCandidateUseCase: LikeCandidateUseCase,
    private val superLikeCandidateUseCase: SuperLikeCandidateUseCase,
    private val skipCandidateUseCase: SkipCandidateUseCase,
    private val saveCandidateUseCase: SaveCandidateUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<NovaLoadState<DiscoverUiState>>(NovaLoadState.Loading)
    val uiState: StateFlow<NovaLoadState<DiscoverUiState>> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(260)
            observeDiscover().collect { state ->
                _uiState.value = NovaLoadState.Success(state)
            }
        }
    }

    fun like() = viewModelScope.launch { likeCandidateUseCase() }
    fun superLike() = viewModelScope.launch { superLikeCandidateUseCase() }
    fun skip() = viewModelScope.launch { skipCandidateUseCase() }
    fun save() = viewModelScope.launch { saveCandidateUseCase() }
}

class MessagesViewModel(
    observeMessages: ObserveMessagesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<NovaLoadState<MessagesUiState>>(NovaLoadState.Loading)
    val uiState: StateFlow<NovaLoadState<MessagesUiState>> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(180)
            observeMessages().collect { state ->
                _uiState.value = NovaLoadState.Success(state)
            }
        }
    }
}

class ChatViewModel(
    observeChat: ObserveChatUseCase,
    private val sendMessage: SendMessageUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<NovaLoadState<ChatUiState>>(NovaLoadState.Loading)
    val uiState: StateFlow<NovaLoadState<ChatUiState>> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(220)
            observeChat().collect { state ->
                _uiState.value = NovaLoadState.Success(state)
            }
        }
    }

    fun send(text: String) = viewModelScope.launch {
        if (text.isBlank()) return@launch
        sendMessage(text.trim())
    }
}

class CommunityViewModel(
    observeCommunity: ObserveCommunityUseCase,
    private val toggleTopicUseCase: ToggleTopicUseCase,
    private val joinEventUseCase: JoinEventUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<NovaLoadState<CommunityUiState>>(NovaLoadState.Loading)
    val uiState: StateFlow<NovaLoadState<CommunityUiState>> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(220)
            observeCommunity().collect { state ->
                _uiState.value = NovaLoadState.Success(state)
            }
        }
    }

    fun toggleTopic(topicId: String) = viewModelScope.launch { toggleTopicUseCase(topicId) }
    fun joinEvent(eventId: String) = viewModelScope.launch { joinEventUseCase(eventId) }
}

class ProfileViewModel(
    observeProfile: ObserveProfileUseCase,
    private val toggleThemeUseCase: ToggleThemeUseCase,
    private val togglePremiumUseCase: TogglePremiumUseCase,
    private val toggleIncognitoUseCase: ToggleIncognitoUseCase,
    private val toggleTravelModeUseCase: ToggleTravelModeUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<NovaLoadState<ProfileUiState>>(NovaLoadState.Loading)
    val uiState: StateFlow<NovaLoadState<ProfileUiState>> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(220)
            observeProfile().collect { state ->
                _uiState.value = NovaLoadState.Success(state)
            }
        }
    }

    fun toggleTheme() = viewModelScope.launch { toggleThemeUseCase() }
    fun togglePremium() = viewModelScope.launch { togglePremiumUseCase() }
    fun toggleIncognito() = viewModelScope.launch { toggleIncognitoUseCase() }
    fun toggleTravelMode() = viewModelScope.launch { toggleTravelModeUseCase() }
    fun setLanguage(language: String) = viewModelScope.launch { updateLanguageUseCase(language) }
}
