package com.nova.app.core.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NovaRepositoryTest {

    private lateinit var repository: FakeNovaRepository

    @Before
    fun setUp() {
        repository = FakeNovaRepository()
    }

    @Test
    fun `completeOnboarding updates session state`() = runTest {
        assertFalse(repository.session.value.onboardingCompleted)
        repository.completeOnboarding()
        assertTrue(repository.session.value.onboardingCompleted)
        assertFalse(repository.session.value.isFirstLaunch)
    }

    @Test
    fun `toggleTheme updates settings state`() = runTest {
        val initialTheme = repository.settings.value.darkMode
        repository.toggleTheme()
        assertNotEquals(initialTheme, repository.settings.value.darkMode)
        repository.toggleTheme()
        assertEquals(initialTheme, repository.settings.value.darkMode)
    }

    @Test
    fun `togglePremium updates settings state`() = runTest {
        assertFalse(repository.settings.value.premiumEnabled)
        repository.togglePremium()
        assertTrue(repository.settings.value.premiumEnabled)
    }

    @Test
    fun `likeCandidate removes candidate from queue and increases liked count`() = runTest {
        val initialQueueSize = repository.discover.value.queue.size
        val initialLikedCount = repository.discover.value.liked
        
        repository.likeCandidate()
        
        assertEquals(initialQueueSize - 1, repository.discover.value.queue.size)
        assertEquals(initialLikedCount + 1, repository.discover.value.liked)
    }

    @Test
    fun `sendMessage updates chat messages and typing state`() = runTest {
        val initialMessageCount = repository.chat.value.messages.size
        val messageText = "Hello, coffee?"
        
        repository.sendMessage(messageText)
        
        // After sending, the message from 'me' should be added and typing should be true initially (in the mock logic)
        // Note: The mock repo has a delay(420) before replying. runTest handles this.
        
        val updatedMessages = repository.chat.value.messages
        assertTrue(updatedMessages.size >= initialMessageCount + 2) // One from me, one reply
        assertEquals(messageText, updatedMessages[initialMessageCount].text)
        assertTrue(updatedMessages[initialMessageCount].sentByMe)
        
        // The last message in chat should be the reply
        assertFalse(updatedMessages.last().sentByMe)
        assertFalse(repository.chat.value.typing)
    }

    @Test
    fun `joinEvent updates event state`() = runTest {
        // e2 is not joined by default
        val eventId = "e2"
        val event = repository.community.value.events.first { it.id == eventId }
        assertFalse(event.joined)
        
        repository.joinEvent(eventId)
        
        assertTrue(repository.community.value.events.first { it.id == eventId }.joined)
    }
}
