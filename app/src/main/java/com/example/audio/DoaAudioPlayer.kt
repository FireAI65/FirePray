package com.example.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.model.DoaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class AudioPlayMode(val label: String) {
    BOTH("Arab & Terjemahan"),
    ARABIC_ONLY("Arab Saja"),
    TRANSLATION_ONLY("Terjemahan Saja")
}

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val currentDoaId: String? = null,
    val currentDoaTitle: String? = null,
    val currentPartText: String = "",
    val playMode: AudioPlayMode = AudioPlayMode.BOTH,
    val speechRate: Float = 1.0f
)

class DoaAudioPlayer(private val context: Context) : TextToSpeech.OnInitListener {

    private val tag = "DoaAudioPlayer"
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    private var activeDoa: DoaItem? = null
    private val arabicLocale = Locale("ar")
    private val indonesianLocale = Locale("id", "ID")

    private var hasArabicSupport = false

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            val arabicResult = tts?.isLanguageAvailable(arabicLocale) ?: TextToSpeech.LANG_NOT_SUPPORTED
            hasArabicSupport = (arabicResult != TextToSpeech.LANG_NOT_SUPPORTED && arabicResult != TextToSpeech.LANG_MISSING_DATA)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    val isTranslationPart = utteranceId?.contains("TRANS") == true
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = true,
                        currentPartText = if (isTranslationPart) "Membaca Terjemahan..." else "Melafalkan Doa..."
                    )
                }

                override fun onDone(utteranceId: String?) {
                    if (utteranceId?.startsWith("PRAYER_AR_") == true && _playbackState.value.playMode == AudioPlayMode.BOTH) {
                        // Move to translation part
                        val item = activeDoa
                        if (item != null && _playbackState.value.isPlaying) {
                            speakTranslationPart(item)
                            return
                        }
                    }
                    // If finished translation or single mode
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        currentDoaId = null,
                        currentDoaTitle = null,
                        currentPartText = ""
                    )
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    Log.e(tag, "TTS Error playing utterance: $utteranceId")
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        currentDoaId = null,
                        currentDoaTitle = null,
                        currentPartText = ""
                    )
                }
            })
            Log.d(tag, "TTS initialized. Arabic support: $hasArabicSupport")
        } else {
            Log.e(tag, "TTS init failed with status: $status")
        }
    }

    fun playDoa(doa: DoaItem, mode: AudioPlayMode = _playbackState.value.playMode) {
        if (!isTtsReady || tts == null) {
            Log.w(tag, "TTS is not ready yet")
            return
        }

        // If clicking on same item currently playing, toggle stop
        if (_playbackState.value.isPlaying && _playbackState.value.currentDoaId == doa.id) {
            stop()
            return
        }

        tts?.stop()
        activeDoa = doa
        _playbackState.value = _playbackState.value.copy(
            isPlaying = true,
            currentDoaId = doa.id,
            currentDoaTitle = doa.title,
            currentPartText = "Menyiapkan suara...",
            playMode = mode
        )

        applySpeechRate(_playbackState.value.speechRate)

        when (mode) {
            AudioPlayMode.ARABIC_ONLY -> {
                speakArabicPart(doa, singleMode = true)
            }
            AudioPlayMode.TRANSLATION_ONLY -> {
                speakTranslationPart(doa)
            }
            AudioPlayMode.BOTH -> {
                speakArabicPart(doa, singleMode = false)
            }
        }
    }

    private fun speakArabicPart(doa: DoaItem, singleMode: Boolean) {
        val utteranceId = if (singleMode) "PRAYER_AR_ONLY_${doa.id}" else "PRAYER_AR_${doa.id}"

        if (hasArabicSupport) {
            tts?.language = arabicLocale
            val params = Bundle()
            tts?.speak(doa.arabicText, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        } else {
            // Fallback: Read clear Latin transliteration
            tts?.language = indonesianLocale
            val params = Bundle()
            tts?.speak(doa.latinText, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        }
    }

    private fun speakTranslationPart(doa: DoaItem) {
        tts?.language = indonesianLocale
        val params = Bundle()
        val textToSpeak = "Artinya: " + doa.translation
        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, "PRAYER_TRANS_${doa.id}")
    }

    fun stop() {
        tts?.stop()
        activeDoa = null
        _playbackState.value = _playbackState.value.copy(
            isPlaying = false,
            currentDoaId = null,
            currentDoaTitle = null,
            currentPartText = ""
        )
    }

    fun setSpeechRate(rate: Float) {
        _playbackState.value = _playbackState.value.copy(speechRate = rate)
        applySpeechRate(rate)
    }

    private fun applySpeechRate(rate: Float) {
        tts?.setSpeechRate(rate)
    }

    fun setPlayMode(mode: AudioPlayMode) {
        _playbackState.value = _playbackState.value.copy(playMode = mode)
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isTtsReady = false
    }
}
