package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiMoodRecommendation
import com.example.ai.AiRecommendationState
import com.example.ai.DoaAiService
import com.example.audio.AudioPlayMode
import com.example.audio.AudioPlaybackState
import com.example.audio.DoaAudioPlayer
import com.example.data.local.DoaDatabase
import com.example.data.model.DoaItem
import com.example.data.model.MoodCategory
import com.example.data.repository.DoaDataRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DoaViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "DoaViewModel"
    private val database = DoaDatabase.getDatabase(application)
    val repository = DoaDataRepository(database.doaBookmarkDao())
    val audioPlayer = DoaAudioPlayer(application)
    private val aiService = DoaAiService()

    // Navigation Tab
    enum class AppTab {
        EXPLORE,
        AI_MOOD,
        BOOKMARKS
    }

    private val _currentTab = MutableStateFlow(AppTab.EXPLORE)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    // Search and Category Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val categories = repository.allCategories

    // Combined filtered Doa List
    val displayedDoas: StateFlow<List<DoaItem>> = combine(
        repository.getDoasFlow(),
        _searchQuery,
        _selectedCategory,
        _currentTab
    ) { allDoas, query, category, tab ->
        var list = allDoas

        if (tab == AppTab.BOOKMARKS) {
            list = list.filter { it.isBookmarked }
        }

        if (category != "Semua") {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.translation.lowercase().contains(q) ||
                it.latinText.lowercase().contains(q) ||
                it.source.lowercase().contains(q) ||
                it.category.lowercase().contains(q)
            }
        }

        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleBookmark(doa: DoaItem) {
        viewModelScope.launch {
            repository.toggleBookmark(doa.id)
        }
    }

    // Audio Playback
    val playbackState: StateFlow<AudioPlaybackState> = audioPlayer.playbackState

    fun playAudio(doa: DoaItem) {
        audioPlayer.playDoa(doa)
    }

    fun stopAudio() {
        audioPlayer.stop()
    }

    fun setAudioPlayMode(mode: AudioPlayMode) {
        audioPlayer.setPlayMode(mode)
    }

    fun setSpeechRate(rate: Float) {
        audioPlayer.setSpeechRate(rate)
    }

    // Tasbih Counter feature for recitation tracking
    private val _tasbihCount = MutableStateFlow(0)
    val tasbihCount: StateFlow<Int> = _tasbihCount.asStateFlow()

    private val _activeTasbihDoa = MutableStateFlow<DoaItem?>(null)
    val activeTasbihDoa: StateFlow<DoaItem?> = _activeTasbihDoa.asStateFlow()

    fun openTasbihCounter(doa: DoaItem) {
        _activeTasbihDoa.value = doa
        _tasbihCount.value = 0
    }

    fun closeTasbihCounter() {
        _activeTasbihDoa.value = null
        _tasbihCount.value = 0
    }

    fun incrementTasbih() {
        _tasbihCount.value = _tasbihCount.value + 1
    }

    fun resetTasbih() {
        _tasbihCount.value = 0
    }

    // Fire AI Mood Recommendation State
    private val _selectedMood = MutableStateFlow<MoodCategory?>(MoodCategory.CEMAS)
    val selectedMood: StateFlow<MoodCategory?> = _selectedMood.asStateFlow()

    private val _userMoodNotes = MutableStateFlow("")
    val userMoodNotes: StateFlow<String> = _userMoodNotes.asStateFlow()

    private val _aiRecommendationState = MutableStateFlow<AiRecommendationState>(AiRecommendationState.Idle)
    val aiRecommendationState: StateFlow<AiRecommendationState> = _aiRecommendationState.asStateFlow()

    fun selectMood(mood: MoodCategory) {
        _selectedMood.value = mood
    }

    fun updateUserMoodNotes(notes: String) {
        _userMoodNotes.value = notes
    }

    fun consultFireAi() {
        val mood = _selectedMood.value ?: MoodCategory.CEMAS
        val notes = _userMoodNotes.value

        viewModelScope.launch {
            _aiRecommendationState.value = AiRecommendationState.Loading
            try {
                val recommendation = aiService.getPrayerRecommendationForMood(
                    moodLabel = "${mood.emoji} ${mood.label}: ${mood.description}",
                    userNotes = notes
                )
                // Register AI-generated prayers to repository so they can be bookmarked and played
                recommendation.recommendedDuas.forEach { doa ->
                    repository.addCustomAiDoa(doa)
                }
                _aiRecommendationState.value = AiRecommendationState.Success(recommendation)
            } catch (e: TimeoutCancellationException) {
                Log.e(tag, "AI recommendation timed out", e)
                _aiRecommendationState.value = AiRecommendationState.Error(
                    "Permintaan rekomendasi doa AI melebihi batas waktu (timeout). Silakan periksa jaringan dan coba lagi."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(tag, "AI recommendation error", e)
                _aiRecommendationState.value = AiRecommendationState.Error(
                    e.localizedMessage ?: "Gagal mendapatkan rekomendasi doa dari Fire AI. Silakan coba sesaat lagi."
                )
            }
        }
    }

    fun saveAiDoa(doa: DoaItem) {
        viewModelScope.launch {
            repository.addCustomAiDoa(doa)
            repository.toggleBookmark(doa.id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
