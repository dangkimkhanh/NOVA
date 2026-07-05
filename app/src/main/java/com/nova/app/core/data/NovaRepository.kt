package com.nova.app.core.data

import com.nova.app.core.designsystem.NovaIcons
import com.nova.app.core.model.ActionKind
import com.nova.app.core.model.AdminMetric
import com.nova.app.core.model.AppSettings
import com.nova.app.core.model.BadgeItem
import com.nova.app.core.model.ChatMessage
import com.nova.app.core.model.ChatThread
import com.nova.app.core.model.ChatUiState
import com.nova.app.core.model.CommunityUiState
import com.nova.app.core.model.CommunityPost
import com.nova.app.core.model.CommunityTopic
import com.nova.app.core.model.CompatibilityMetric
import com.nova.app.core.model.DiscoverUiState
import com.nova.app.core.model.DiscoveryCandidate
import com.nova.app.core.model.EventItem
import com.nova.app.core.model.FeedPost
import com.nova.app.core.model.HomeUiState
import com.nova.app.core.model.LaunchUiState
import com.nova.app.core.model.MessagesUiState
import com.nova.app.core.model.NotificationItem
import com.nova.app.core.model.PremiumPlan
import com.nova.app.core.model.ProfileUiState
import com.nova.app.core.model.SampleMedia
import com.nova.app.core.model.SafetyItem
import com.nova.app.core.model.ScreenAction
import com.nova.app.core.model.ScreenSpec
import com.nova.app.core.model.SearchFilter
import com.nova.app.core.model.SessionState
import com.nova.app.core.model.StatCard
import com.nova.app.core.model.StoryItem
import com.nova.app.core.model.UserCard
import com.nova.app.core.model.WalletEntry
import com.nova.app.core.navigation.AppRoute
import com.nova.app.core.navigation.ScreenStateKind
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface NovaRepository {
    val session: StateFlow<SessionState>
    val settings: StateFlow<AppSettings>
    val home: StateFlow<HomeUiState>
    val discover: StateFlow<DiscoverUiState>
    val messages: StateFlow<MessagesUiState>
    val chat: StateFlow<ChatUiState>
    val community: StateFlow<CommunityUiState>
    val profile: StateFlow<ProfileUiState>

    suspend fun completeOnboarding()
    suspend fun completeAuth()
    suspend fun completeProfile()
    suspend fun toggleTheme()
    suspend fun togglePremium()
    suspend fun updateLanguage(language: String)
    suspend fun toggleIncognito()
    suspend fun toggleTravelMode()
    suspend fun likeCandidate()
    suspend fun superLikeCandidate()
    suspend fun skipCandidate()
    suspend fun saveCandidate()
    suspend fun sendMessage(text: String)
    suspend fun toggleTopic(topicId: String)
    suspend fun joinEvent(eventId: String)
}

class FakeNovaRepository : NovaRepository {
    private val sessionState = MutableStateFlow(SessionState())
    private val settingsState = MutableStateFlow(defaultSettings())
    private val discoverState = MutableStateFlow(defaultDiscoverState())
    private val homeState = MutableStateFlow(defaultHomeState())
    private val messagesState = MutableStateFlow(defaultMessagesState())
    private val chatState = MutableStateFlow(defaultChatState())
    private val communityState = MutableStateFlow(defaultCommunityState())
    private val profileState = MutableStateFlow(defaultProfileState(settingsState.value))

    override val session: StateFlow<SessionState> = sessionState.asStateFlow()
    override val settings: StateFlow<AppSettings> = settingsState.asStateFlow()
    override val home: StateFlow<HomeUiState> = homeState.asStateFlow()
    override val discover: StateFlow<DiscoverUiState> = discoverState.asStateFlow()
    override val messages: StateFlow<MessagesUiState> = messagesState.asStateFlow()
    override val chat: StateFlow<ChatUiState> = chatState.asStateFlow()
    override val community: StateFlow<CommunityUiState> = communityState.asStateFlow()
    override val profile: StateFlow<ProfileUiState> = profileState.asStateFlow()

    override suspend fun completeOnboarding() {
        sessionState.update { it.copy(isFirstLaunch = false, onboardingCompleted = true) }
        syncProfile()
    }

    override suspend fun completeAuth() {
        sessionState.update { it.copy(otpVerified = true) }
        syncProfile()
    }

    override suspend fun completeProfile() {
        sessionState.update { it.copy(profileCompleted = true) }
        syncProfile()
    }

    override suspend fun toggleTheme() {
        settingsState.update { it.copy(darkMode = !it.darkMode) }
        syncProfile()
    }

    override suspend fun togglePremium() {
        settingsState.update { it.copy(premiumEnabled = !it.premiumEnabled) }
        syncProfile()
    }

    override suspend fun updateLanguage(language: String) {
        settingsState.update { it.copy(language = language) }
        syncProfile()
    }

    override suspend fun toggleIncognito() {
        settingsState.update { it.copy(incognitoEnabled = !it.incognitoEnabled) }
        syncProfile()
    }

    override suspend fun toggleTravelMode() {
        settingsState.update { it.copy(travelModeEnabled = !it.travelModeEnabled) }
        syncProfile()
    }

    override suspend fun likeCandidate() {
        discoverState.update { state ->
            val next = state.queue.drop(1).ifEmpty { sampleCandidates() }
            state.copy(queue = next, liked = state.liked + 1, activeIndex = 0)
        }
    }

    override suspend fun superLikeCandidate() {
        discoverState.update { state ->
            val next = state.queue.drop(1).ifEmpty { sampleCandidates() }
            state.copy(queue = next, superLiked = state.superLiked + 1, activeIndex = 0)
        }
    }

    override suspend fun skipCandidate() {
        discoverState.update { state ->
            val next = state.queue.drop(1).ifEmpty { sampleCandidates() }
            state.copy(queue = next, skipped = state.skipped + 1, activeIndex = 0)
        }
    }

    override suspend fun saveCandidate() {
        discoverState.update { it.copy(saved = it.saved + 1) }
    }

    override suspend fun sendMessage(text: String) {
        val outgoing = chatState.value
        val me = outgoing.thread.user.copy(id = "me", name = "You", age = 0, photoUrl = outgoing.thread.user.photoUrl)
        val replyText = when {
            text.contains("coffee", ignoreCase = true) -> "Coffee sounds like a first date."
            text.contains("music", ignoreCase = true) -> "Send me your current playlist."
            text.contains("travel", ignoreCase = true) -> "I already know our next city."
            else -> "I like how you started that."
        }
        chatState.update { state ->
            state.copy(
                messages = state.messages + ChatMessage(
                    id = "msg-${state.messages.size + 1}",
                    text = text,
                    sentByMe = true,
                    timeLabel = "Now",
                    isRead = true,
                ),
                typing = true,
            )
        }
        delay(420)
        chatState.update { state ->
            state.copy(
                messages = state.messages + ChatMessage(
                    id = "msg-${state.messages.size + 1}",
                    text = replyText,
                    sentByMe = false,
                    timeLabel = "Now",
                    translatedText = if (settingsState.value.autoTranslateEnabled) "Translated: $replyText" else null,
                    isRead = true,
                ),
                typing = false,
            )
        }
        messagesState.update { state ->
            val updatedThreads = state.threads.map {
                if (it.id == outgoing.thread.id) it.copy(lastMessage = text, unreadCount = 0, typing = false) else it
            }
            state.copy(threads = updatedThreads)
        }
    }

    override suspend fun toggleTopic(topicId: String) {
        communityState.update { state ->
            state.copy(topics = state.topics.map { if (it.id == topicId) it.copy(isJoined = !it.isJoined) else it })
        }
    }

    override suspend fun joinEvent(eventId: String) {
        communityState.update { state ->
            state.copy(events = state.events.map { if (it.id == eventId) it.copy(joined = true) else it })
        }
    }

    private fun syncProfile() {
        val settings = settingsState.value
        val session = sessionState.value
        profileState.update { current ->
            current.copy(
                settings = settings,
                stats = listOf(
                    StatCard("Matches", "128", "This month"),
                    StatCard("Soulmates", "4", "High compatibility"),
                    StatCard("Events", "17", "Joined"),
                    StatCard("Streak", "26", "Days active"),
                ),
                badges = defaultBadges(),
                wallet = defaultWallet(),
                safety = defaultSafety(settings),
                plans = defaultPlans(settings.premiumEnabled),
                notifications = defaultNotifications(),
                compatibility = defaultCompatibility(),
                filters = defaultFilters(),
                genericScreens = genericScreens(),
                adminMetrics = defaultAdminMetrics(),
                bio = if (session.profileCompleted) "Profile ready. Matching, community, and dates live." else current.bio,
            )
        }
    }

    private fun defaultSettings() = AppSettings()

    private fun defaultHomeState() = HomeUiState(
        stories = listOf(
            StoryItem(user = sampleUsers()[0], mediaUrl = SampleMedia.landscape1, caption = "Rooftop sunset and a latte.", music = "SZA - Snooze"),
            StoryItem(user = sampleUsers()[1], mediaUrl = SampleMedia.landscape2, caption = "Hiking before breakfast.", music = "Odesza - A Moment Apart"),
            StoryItem(user = sampleUsers()[2], mediaUrl = SampleMedia.landscape3, caption = "Vinyl night with friends.", music = "Khruangbin - Time"),
            StoryItem(user = sampleUsers()[3], mediaUrl = SampleMedia.landscape4, caption = "Museum, then cocktail bar.", music = "Glass Animals - Gooey"),
        ),
        feed = listOf(
            FeedPost(
                id = "feed-1",
                author = sampleUsers()[0],
                caption = "A soft launch into my new city. Coffee, design, and long walks only.",
                mediaUrls = listOf(SampleMedia.portrait2),
                likes = 384,
                comments = 48,
                saves = 94,
                tags = listOf("City life", "Coffee", "Fashion"),
                timeLabel = "12m ago",
            ),
            FeedPost(
                id = "feed-2",
                author = sampleUsers()[1],
                caption = "Looking for a hiking buddy who can also recommend a good playlist.",
                mediaUrls = listOf(SampleMedia.landscape3),
                likes = 221,
                comments = 31,
                saves = 62,
                tags = listOf("Hiking", "Music", "Weekend"),
                timeLabel = "1h ago",
            ),
        ),
        featured = sampleCandidates().first(),
        events = sampleEvents(),
        communities = sampleTopics(),
        suggestions = listOf("Video date tonight", "Coffee nearby", "Soulmate picks", "Join a community"),
    )

    private fun defaultDiscoverState() = DiscoverUiState(
        queue = sampleCandidates(),
        activeIndex = 0,
        liked = 18,
        superLiked = 4,
        saved = 9,
        skipped = 27,
    )

    private fun defaultMessagesState() = MessagesUiState(
        threads = sampleThreads(),
        onlineNow = 6,
        filters = listOf("All", "Matches", "Voice", "Groups"),
        searchHint = "Search by name, interest, or city",
    )

    private fun defaultChatState() = ChatUiState(
        thread = sampleThreads().first(),
        messages = listOf(
            ChatMessage(id = "m1", text = "You seem like someone who actually reads bios.", sentByMe = false, timeLabel = "09:02", isRead = true),
            ChatMessage(id = "m2", text = "I do. It saves time and gives me a better first topic.", sentByMe = true, timeLabel = "09:04", isRead = true),
            ChatMessage(id = "m3", text = "Good. Then I expect a coffee recommendation soon.", sentByMe = false, timeLabel = "09:05", isRead = true),
        ),
        typing = true,
        suggestions = listOf("Coffee this week?", "Send your playlist", "Video call at 8?", "What are you looking for?"),
        translationEnabled = true,
        callHint = "Best time to call: 8:30 PM",
    )

    private fun defaultCommunityState() = CommunityUiState(
        topics = sampleTopics(),
        posts = listOf(
            CommunityPost(
                id = "cp-1",
                topic = "Travel",
                author = sampleUsers()[0],
                text = "Best date spots in Bangkok that don't feel touristy?",
                mediaUrl = SampleMedia.landscape1,
                likes = 84,
                comments = 14,
                timeLabel = "2h ago",
            ),
            CommunityPost(
                id = "cp-2",
                topic = "Fitness",
                author = sampleUsers()[1],
                text = "Looking for a running partner for 5k mornings.",
                mediaUrl = null,
                likes = 58,
                comments = 9,
                timeLabel = "5h ago",
            ),
        ),
        events = sampleEvents(),
        trending = listOf("Travel friends", "Coffee dates", "Startup founders", "Anime night"),
    )

    private fun defaultProfileState(settings: AppSettings) = ProfileUiState(
        user = sampleUsers()[4],
        bio = "Builder by day, city explorer by night. Looking for something emotionally honest and curious.",
        prompts = listOf(
            "A green flag I notice immediately is...",
            "My ideal first date is...",
            "The song that feels like home is...",
        ),
        badges = defaultBadges(),
        stats = listOf(
            StatCard("Matches", "128", "Lifetime"),
            StatCard("Soulmates", "4", "High compatibility"),
            StatCard("Events", "17", "Joined"),
            StatCard("Streak", "26", "Days"),
        ),
        settings = settings,
        wallet = defaultWallet(),
        safety = defaultSafety(settings),
        plans = defaultPlans(settings.premiumEnabled),
        notifications = defaultNotifications(),
        genericScreens = genericScreens(),
        adminMetrics = defaultAdminMetrics(),
        compatibility = defaultCompatibility(),
        filters = defaultFilters(),
    )

    private fun defaultBadges() = listOf(
        BadgeItem("b1", "Daily Login", "Opened the app 26 days in a row", 100, "26", true),
        BadgeItem("b2", "Verified", "Photo and identity verified", 100, "V", true),
        BadgeItem("b3", "Community Leader", "Helpful and active in communities", 72, "C", false),
        BadgeItem("b4", "Video Date", "Completed a successful video date", 48, "VD", false),
    )

    private fun defaultWallet() = listOf(
        WalletEntry("w1", "Coins added", "Gift credit from a match", "+120", "Today", true),
        WalletEntry("w2", "Premium", "Monthly subscription", "-$19.99", "Yesterday", false),
        WalletEntry("w3", "Boost", "Profile boost used", "-25", "2d ago", false),
    )

    private fun defaultSafety(settings: AppSettings) = listOf(
        SafetyItem("Report", "Report suspicious or abusive behavior", "Open", false),
        SafetyItem("Block", "Block a user instantly", "Manage", false),
        SafetyItem("Emergency", "Quick access to emergency contacts", "Setup", false),
        SafetyItem("Location Share", "Share live location during dates", "On", settings.locationSharingEnabled),
        SafetyItem("Photo Verification", "Verify your photos for trust", "On", settings.photoVerificationEnabled),
        SafetyItem("Video Verification", "Record a short verification clip", "Off", settings.videoVerificationEnabled),
    )

    private fun defaultPlans(premiumEnabled: Boolean) = listOf(
        PremiumPlan(
            name = "Free",
            price = "$0",
            cycle = "Forever",
            subtitle = "Core matching, stories, and community access.",
            features = listOf("Daily likes", "Basic filters", "Messaging", "Community access"),
            highlighted = !premiumEnabled,
        ),
        PremiumPlan(
            name = "Premium",
            price = "$19.99",
            cycle = "per month",
            subtitle = "Unlimited matches, advanced filters, and AI coach.",
            features = listOf("Unlimited likes", "Incognito", "Boost", "See who liked you", "Undo swipe", "Travel mode"),
            highlighted = premiumEnabled,
        ),
    )

    private fun defaultNotifications() = listOf(
        NotificationItem("n1", "New match", "Mia liked your profile and sent a message.", "5m", "Match", true),
        NotificationItem("n2", "Event reminder", "Coffee date meetup starts in 2 hours.", "1h", "Event", true),
        NotificationItem("n3", "Community reply", "Your post got 12 replies in Travel.", "3h", "Community", false),
        NotificationItem("n4", "Premium offer", "Unlock advanced filters and travel mode.", "Yesterday", "Promotion", false),
    )

    private fun defaultCompatibility() = listOf(
        CompatibilityMetric("Personality", 94, "Shared energy"),
        CompatibilityMetric("Lifestyle", 88, "Aligned pace"),
        CompatibilityMetric("Emotion", 91, "Deep trust"),
        CompatibilityMetric("Communication", 97, "Clear and direct"),
        CompatibilityMetric("Love Language", 89, "Strong overlap"),
        CompatibilityMetric("Conflict", 84, "Healthy repair"),
    )

    private fun defaultFilters() = listOf(
        SearchFilter("Distance", "Nearby"),
        SearchFilter("Age", "24-34"),
        SearchFilter("MBTI", "INFJ / ENFP"),
        SearchFilter("Language", "English, Vietnamese"),
        SearchFilter("Goal", "Long-term"),
    )

    private fun defaultAdminMetrics() = listOf(
        AdminMetric("Revenue", "$48.2K", "+12.4%", true),
        AdminMetric("Subscriptions", "7.8K", "+8.6%", true),
        AdminMetric("Reports", "42", "-18%", true),
        AdminMetric("Active users", "129K", "+9.1%", true),
    )

    private fun genericScreens() = listOf(
        ScreenSpec(
            id = "saved",
            title = "Saved",
            subtitle = "Curate the people and posts you want to revisit.",
            heroLabel = "Saved for later",
            heroDescription = "Bookmarks, favorite profiles, and conversations you want to bring back into the flow.",
            stats = listOf(StatCard("Profiles", "24"), StatCard("Posts", "18"), StatCard("Events", "7")),
            chips = listOf("Profiles", "Posts", "Events", "Music"),
            bullets = listOf("Organize by mood", "Keep the best icebreakers", "Jump back in anytime"),
            photos = listOf(SampleMedia.portrait1, SampleMedia.portrait2, SampleMedia.landscape2, SampleMedia.landscape3),
            actions = listOf(ScreenAction("Open Premium", NovaIcons.Premium, AppRoute.Premium), ScreenAction("Back to Profile", NovaIcons.Profile, AppRoute.Profile, ActionKind.Secondary)),
        ),
        ScreenSpec(
            id = "likes",
            title = "Likes",
            subtitle = "See who reacted, liked, or saved your vibe.",
            heroLabel = "Who liked you",
            heroDescription = "A mix of matches, communities, and story reactions that can turn into conversations.",
            stats = listOf(StatCard("Likes", "128"), StatCard("Super likes", "9"), StatCard("Views", "4.1K")),
            chips = listOf("Today", "This week", "This month"),
            bullets = listOf("Fast response cards", "One-tap match actions", "Voice intro preview"),
            photos = listOf(SampleMedia.portrait3, SampleMedia.portrait4, SampleMedia.portrait5, SampleMedia.portrait6),
            actions = listOf(ScreenAction("Upgrade", NovaIcons.Premium, AppRoute.Premium), ScreenAction("See matches", NovaIcons.Messages, AppRoute.Messages, ActionKind.Secondary)),
        ),
        ScreenSpec(
            id = "blocked",
            title = "Blocked users",
            subtitle = "Trust and safety list with instant control.",
            heroLabel = "Safe by default",
            heroDescription = "Manage blocked users, report history, and quick unblock flows from a single privacy surface.",
            stats = listOf(StatCard("Blocked", "6"), StatCard("Reports", "3"), StatCard("Safe mode", "On")),
            chips = listOf("Block", "Report", "Mute"),
            bullets = listOf("Emergency access", "Scam alerts", "Identity verification"),
            photos = listOf(SampleMedia.portrait7, SampleMedia.portrait8),
            actions = listOf(ScreenAction("Open Safety", NovaIcons.Safety, AppRoute.Safety), ScreenAction("Back", NovaIcons.ChevronRight, AppRoute.Profile, ActionKind.Secondary)),
        ),
        ScreenSpec(
            id = "privacy",
            title = "Privacy center",
            subtitle = "Control visibility, read receipts, and location sharing.",
            heroLabel = "Privacy first",
            heroDescription = "Every control is tuned for trust, not friction. Hide where needed, show only when it matters.",
            stats = listOf(StatCard("Incognito", if (settingsState.value.incognitoEnabled) "On" else "Off"), StatCard("Location", if (settingsState.value.locationSharingEnabled) "Shared" else "Hidden")),
            chips = listOf("Incognito", "Location", "Read receipts", "Hide profile"),
            bullets = listOf("Fine-grained visibility", "Travel mode ready", "Safety controls in one place"),
            photos = listOf(SampleMedia.landscape1, SampleMedia.landscape4),
            actions = listOf(ScreenAction("Open Settings", NovaIcons.Settings, AppRoute.Settings), ScreenAction("Open Safety", NovaIcons.Safety, AppRoute.Safety, ActionKind.Secondary)),
        ),
        ScreenSpec(
            id = "help",
            title = "Help center",
            subtitle = "Support, troubleshooting, and product guidance.",
            heroLabel = "Need a hand?",
            heroDescription = "Search articles, chat with support, and learn how to keep your profile strong.",
            stats = listOf(StatCard("Articles", "120"), StatCard("Chat", "24/7"), StatCard("Response", "< 2h")),
            chips = listOf("Account", "Payments", "Matches", "Safety"),
            bullets = listOf("AI search answers", "Self-service flows", "Escalate when needed"),
            photos = listOf(SampleMedia.portrait9, SampleMedia.portrait10),
            actions = listOf(ScreenAction("Feedback", NovaIcons.Feedback, AppRoute.Settings), ScreenAction("About", NovaIcons.Info, AppRoute.Profile, ActionKind.Secondary)),
        ),
        ScreenSpec(
            id = "feedback",
            title = "Feedback",
            subtitle = "Capture product feedback and UX pain points.",
            heroLabel = "Say what you need",
            heroDescription = "A premium feedback surface with screenshots, context, and an easy submit flow.",
            stats = listOf(StatCard("Open", "14"), StatCard("Resolved", "126")),
            chips = listOf("UX", "Bug", "Feature", "Billing"),
            bullets = listOf("Screenshot attachment", "Priority tagging", "Direct follow up"),
            photos = listOf(SampleMedia.landscape2, SampleMedia.landscape3),
            actions = listOf(ScreenAction("Submit", NovaIcons.Send, AppRoute.Status(ScreenStateKind.Success)), ScreenAction("Back", NovaIcons.ChevronRight, AppRoute.Profile, ActionKind.Secondary)),
        ),
        ScreenSpec(
            id = "about",
            title = "About NOVA",
            subtitle = "The product story, roadmap, and release notes.",
            heroLabel = "Built for real connection",
            heroDescription = "NOVA blends dating, social discovery, and community in one premium mobile experience.",
            stats = listOf(StatCard("Version", "1.0"), StatCard("Users", "129K")),
            chips = listOf("Dating", "Community", "AI", "Safety"),
            bullets = listOf("Modern stack", "Design-first product", "Scalable architecture"),
            photos = listOf(SampleMedia.landscape1, SampleMedia.landscape2, SampleMedia.landscape4),
            actions = listOf(ScreenAction("Security", NovaIcons.Security, AppRoute.Safety), ScreenAction("Back", NovaIcons.ChevronRight, AppRoute.Profile, ActionKind.Secondary)),
        ),
        ScreenSpec(
            id = "security",
            title = "Security",
            subtitle = "Passwords, devices, verification, and session health.",
            heroLabel = "Trust layer",
            heroDescription = "All critical security settings live in one place with clear progress and warnings.",
            stats = listOf(StatCard("Verified", "Yes"), StatCard("Devices", "2")),
            chips = listOf("2FA", "Device lock", "Identity", "Photo"),
            bullets = listOf("Secure sign-in", "Session alerts", "Verification history"),
            photos = listOf(SampleMedia.portrait5, SampleMedia.portrait6),
            actions = listOf(ScreenAction("Identity", NovaIcons.Verified, AppRoute.Safety), ScreenAction("Back", NovaIcons.ChevronRight, AppRoute.Profile, ActionKind.Secondary)),
        ),
        ScreenSpec(
            id = "verification",
            title = "Verification",
            subtitle = "Identity, video, and photo verification flows.",
            heroLabel = "Verified humans only",
            heroDescription = "A trust layer that keeps fake profiles out and gives real people confidence.",
            stats = listOf(StatCard("Photo", "On"), StatCard("Video", "Ready"), StatCard("ID", "In review")),
            chips = listOf("Photo", "Video", "ID", "Face match"),
            bullets = listOf("Reduce spam", "Increase match trust", "Better conversion"),
            photos = listOf(SampleMedia.portrait1, SampleMedia.portrait8, SampleMedia.portrait9),
            actions = listOf(ScreenAction("Start", NovaIcons.Camera, AppRoute.Status(ScreenStateKind.Permission)), ScreenAction("Back", NovaIcons.ChevronRight, AppRoute.Profile, ActionKind.Secondary)),
        ),
        ScreenSpec(
            id = "travel",
            title = "Travel mode",
            subtitle = "Reach people in the city you're visiting before you land.",
            heroLabel = "Match anywhere",
            heroDescription = "Discover locals, events, and coffee spots for your next trip.",
            stats = listOf(StatCard("Cities", "18"), StatCard("Nearby", "214")),
            chips = listOf("Cities", "Nearby", "Events", "Coffee"),
            bullets = listOf("Smooth city switching", "Local recommendations", "Time-zone aware chat"),
            photos = listOf(SampleMedia.landscape3, SampleMedia.landscape4),
            actions = listOf(ScreenAction("Enable", NovaIcons.Travel, AppRoute.Events), ScreenAction("Back", NovaIcons.ChevronRight, AppRoute.Premium, ActionKind.Secondary)),
        ),
        ScreenSpec(
            id = "incognito",
            title = "Incognito mode",
            subtitle = "Browse and match privately when you want a low-profile session.",
            heroLabel = "Private browsing",
            heroDescription = "Only the people you like can discover you while this mode is active.",
            stats = listOf(StatCard("Visible", "Selected"), StatCard("Private", "On")),
            chips = listOf("Invisible", "Selected likes", "Private", "Safe"),
            bullets = listOf("Hide from discover", "Selective visibility", "Premium gated"),
            photos = listOf(SampleMedia.portrait2, SampleMedia.portrait7),
            actions = listOf(ScreenAction("Upgrade", NovaIcons.Premium, AppRoute.Premium), ScreenAction("Back", NovaIcons.ChevronRight, AppRoute.Profile, ActionKind.Secondary)),
        ),
        ScreenSpec(
            id = "boost",
            title = "Boost",
            subtitle = "Move your profile to the top and get instant reach.",
            heroLabel = "Visibility spike",
            heroDescription = "A premium growth surface with timing, analytics, and a fast activation button.",
            stats = listOf(StatCard("Boosts", "3"), StatCard("Reach", "+240%")),
            chips = listOf("Top slot", "Peak hours", "Analytics"),
            bullets = listOf("Immediate ranking lift", "Best time suggestions", "Boost history"),
            photos = listOf(SampleMedia.landscape2, SampleMedia.landscape3),
            actions = listOf(ScreenAction("Activate", NovaIcons.Boost, AppRoute.Status(ScreenStateKind.Success)), ScreenAction("Back", NovaIcons.ChevronRight, AppRoute.Premium, ActionKind.Secondary)),
        ),
        ScreenSpec(
            id = "who_liked",
            title = "Who liked you",
            subtitle = "Premium reveal for the people most likely to match.",
            heroLabel = "Reveal queue",
            heroDescription = "A confidence-building way to see the people already interested in you.",
            stats = listOf(StatCard("Hidden likes", "24"), StatCard("Mutual", "9")),
            chips = listOf("Recent", "Top match", "Local"),
            bullets = listOf("Fast matching", "Priority sorting", "Confidence boost"),
            photos = listOf(SampleMedia.portrait3, SampleMedia.portrait4, SampleMedia.portrait5),
            actions = listOf(ScreenAction("Open Premium", NovaIcons.Premium, AppRoute.Premium), ScreenAction("Back", NovaIcons.ChevronRight, AppRoute.Profile, ActionKind.Secondary)),
        ),
        ScreenSpec(
            id = "daily_reward",
            title = "Daily reward",
            subtitle = "Claim coins, boosts, and bonus gifts.",
            heroLabel = "Keep the streak",
            heroDescription = "A light daily loop that feels rewarding without breaking the premium tone.",
            stats = listOf(StatCard("Streak", "26"), StatCard("Coins", "420")),
            chips = listOf("Coins", "Boost", "Gift", "Streak"),
            bullets = listOf("Daily incentives", "Progressive rewards", "Soft habit loop"),
            photos = listOf(SampleMedia.landscape1, SampleMedia.landscape2),
            actions = listOf(ScreenAction("Claim", NovaIcons.Gift, AppRoute.Status(ScreenStateKind.Success)), ScreenAction("Back", NovaIcons.ChevronRight, AppRoute.Profile, ActionKind.Secondary)),
        ),
        ScreenSpec(
            id = "ux",
            title = "UX audit",
            subtitle = "Design system checks, accessibility, and performance states.",
            heroLabel = "Ship quality",
            heroDescription = "Review component reuse, empty/error handling, and touch target compliance.",
            stats = listOf(StatCard("Components", "24"), StatCard("States", "7")),
            chips = listOf("A11y", "Spacing", "States", "Motion"),
            bullets = listOf("Touch target >= 44px", "Dark mode contrast", "Animation restraint"),
            photos = listOf(SampleMedia.landscape4, SampleMedia.portrait10),
            actions = listOf(ScreenAction("Open Design System", NovaIcons.Sparkle, AppRoute.DesignSystem), ScreenAction("Back", NovaIcons.ChevronRight, AppRoute.Admin, ActionKind.Secondary)),
        ),
    )

    private fun sampleUsers() = listOf(
        UserCard("u1", "Mia", 27, SampleMedia.portrait2, verified = true, distanceKm = 2, online = true, city = "Bangkok"),
        UserCard("u2", "Noah", 29, SampleMedia.portrait3, verified = true, distanceKm = 4, online = false, city = "Bangkok"),
        UserCard("u3", "Ava", 24, SampleMedia.portrait4, verified = true, distanceKm = 1, online = true, city = "Bangkok"),
        UserCard("u4", "Ken", 31, SampleMedia.portrait5, verified = false, distanceKm = 6, online = false, city = "Bangkok"),
        UserCard("me", "You", 28, SampleMedia.portrait6, verified = true, distanceKm = null, online = true, city = "Bangkok"),
    )

    private fun sampleCandidates() = listOf(
        DiscoveryCandidate(
            user = sampleUsers()[0],
            bio = "Brand strategist, coffee ritualist, and a believer in long conversations over loud rooms.",
            compatibility = 96,
            commonInterests = listOf("Coffee", "Travel", "Architecture", "Vinyl"),
            iceBreaker = "Which city changed your taste in people?",
            mutualFriends = 8,
            musicTaste = "Indie pop",
            height = "168 cm",
            job = "Brand Strategist",
            relationshipGoal = "Long-term",
            gallery = listOf(SampleMedia.portrait2, SampleMedia.landscape1, SampleMedia.landscape2),
        ),
        DiscoveryCandidate(
            user = sampleUsers()[1],
            bio = "Product designer who likes early runs, calm playlists, and people with good eye contact.",
            compatibility = 91,
            commonInterests = listOf("Design", "Running", "Music"),
            iceBreaker = "What's your ideal first date pace?",
            mutualFriends = 5,
            musicTaste = "Electronic",
            height = "180 cm",
            job = "Product Designer",
            relationshipGoal = "Relationship",
            gallery = listOf(SampleMedia.portrait3, SampleMedia.landscape3, SampleMedia.landscape4),
        ),
        DiscoveryCandidate(
            user = sampleUsers()[2],
            bio = "Hospitality founder exploring new neighborhoods and good people.",
            compatibility = 88,
            commonInterests = listOf("Food", "Travel", "Startup", "Coffee"),
            iceBreaker = "Where would you take someone for a first coffee in your city?",
            mutualFriends = 3,
            musicTaste = "Jazz",
            height = "165 cm",
            job = "Founder",
            relationshipGoal = "Dating first",
            gallery = listOf(SampleMedia.portrait4, SampleMedia.landscape1),
        ),
        DiscoveryCandidate(
            user = sampleUsers()[3],
            bio = "Fitness coach, anime watcher, and weekend language-exchange host.",
            compatibility = 84,
            commonInterests = listOf("Fitness", "Anime", "Language", "Community"),
            iceBreaker = "If we only had 90 minutes, what would we do?",
            mutualFriends = 2,
            musicTaste = "Afrobeat",
            height = "176 cm",
            job = "Coach",
            relationshipGoal = "Open to connection",
            gallery = listOf(SampleMedia.portrait5, SampleMedia.portrait6, SampleMedia.landscape2),
        ),
    )

    private fun sampleThreads() = listOf(
        ChatThread("t1", sampleUsers()[0], "Let's keep the coffee date idea alive.", 2, online = true, typing = true, pinned = true, matchLabel = "96% match"),
        ChatThread("t2", sampleUsers()[1], "Shared a playlist and a Saturday plan.", 0, online = false, pinned = false, matchLabel = "91% match"),
        ChatThread("t3", sampleUsers()[2], "You were right about the rooftop bar.", 1, online = true, pinned = false, matchLabel = "88% match"),
        ChatThread("t4", sampleUsers()[3], "Festival tickets? I am in.", 0, online = false, pinned = false, matchLabel = "84% match"),
    )

    private fun sampleTopics() = listOf(
        CommunityTopic("travel", "Travel", "Weekend trips, city guides, and hidden gems.", SampleMedia.landscape1, "18.2K", "Mia", 12, true),
        CommunityTopic("cafe", "Cafe", "Coffee spots, roaster reviews, and date-friendly menus.", SampleMedia.landscape2, "7.4K", "Ava", 8, true),
        CommunityTopic("game", "Game", "Co-op nights, tournaments, and low-pressure icebreakers.", SampleMedia.landscape3, "5.1K", "Ken", 4, false),
        CommunityTopic("anime", "Anime", "Watch parties, recommendations, and fan meetups.", SampleMedia.landscape4, "9.8K", "Noah", 6, false),
        CommunityTopic("startup", "Startup", "Founders, operators, and creative builders.", SampleMedia.portrait8, "11.2K", "Mia", 7, false),
    )

    private fun sampleEvents() = listOf(
        EventItem("e1", "Sunset Coffee Crawl", "Offline", "Fri, 7:00 PM", "Thao Dien, Ho Chi Minh City", "$12", SampleMedia.landscape1, "42 joined", true),
        EventItem("e2", "Language Exchange Night", "Online", "Sat, 8:30 PM", "Zoom", "Free", SampleMedia.landscape2, "126 joined", false),
        EventItem("e3", "Speed Dating Social", "Offline", "Sun, 6:00 PM", "District 1", "$19", SampleMedia.landscape3, "61 joined", false),
        EventItem("e4", "Hiking + Brunch", "Offline", "Next Tue", "Dalat", "$24", SampleMedia.landscape4, "18 joined", false),
    )

    private fun syncCommunity() {
        communityState.update { state ->
            state.copy(events = state.events.map { it.copy(joined = it.joined) })
        }
    }
}
