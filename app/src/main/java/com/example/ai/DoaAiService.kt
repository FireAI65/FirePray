package com.example.ai

import android.util.Log
import com.example.data.model.DoaItem
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.util.UUID

data class AiMoodRecommendation(
    val moodSummary: String,
    val spiritualWisdom: String,
    val recommendedDuas: List<DoaItem>,
    val dhikrTip: String
)

sealed interface AiRecommendationState {
    data object Idle : AiRecommendationState
    data object Loading : AiRecommendationState
    data class Success(val recommendation: AiMoodRecommendation) : AiRecommendationState
    data class Error(val message: String) : AiRecommendationState
}

class DoaAiService {

    private val tag = "DoaAiService"

    private val doaItemSchema = Schema.obj(
        properties = mapOf(
            "title" to Schema.string(description = "Judul nama doa yang ringkas dan jelas"),
            "category" to Schema.string(description = "Kategori sumber doa: Al-Qur'an atau Hadist Shahih"),
            "arabicText" to Schema.string(description = "Teks doa bahasa Arab lengkap dengan harakat/tashkeel"),
            "latinText" to Schema.string(description = "Transliterasi bacaan latin bahasa Indonesia yang mudah dibaca"),
            "translation" to Schema.string(description = "Arti terjemahan doa dalam Bahasa Indonesia yang menyentuh hati"),
            "source" to Schema.string(description = "Sumber dalil spesifik, misal QS. Al-Baqarah: 201 atau HR. Bukhari"),
            "benefit" to Schema.string(description = "Khasiat atau keutamaan doa ini terhadap kondisi batin pengguna")
        )
    )

    private val responseSchema = Schema.obj(
        properties = mapOf(
            "moodSummary" to Schema.string(description = "Ringkasan pemahaman kondisi batin pengguna secara empatik"),
            "spiritualWisdom" to Schema.string(description = "Nasihat hikmah islami penyejuk hati yang menenangkan jiwa"),
            "recommendedDuas" to Schema.array(
                items = doaItemSchema,
                description = "Daftar 2 sampai 3 doa paling relevan dari Al-Qur'an dan Hadits"
            ),
            "dhikrTip" to Schema.string(description = "Amalan dzikir ringkas harian penenang hati (misal: istighfar atau hauqalah)")
        )
    )

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                this.responseSchema = this@DoaAiService.responseSchema
            }
        )
    }

    suspend fun getPrayerRecommendationForMood(
        moodLabel: String,
        userNotes: String = ""
    ): AiMoodRecommendation {
        return withTimeout(60_000L) {
            val prompt = buildString {
                appendLine("Kamu adalah pembimbing spiritual Islam dan pakar kumpulan doa-doa Al-Qur'an dan Hadits yang penuh empati, lembut, dan bijaksana.")
                appendLine("Pengguna sedang merasakan suasana hati / kondisi batin: '$moodLabel'.")
                if (userNotes.isNotBlank()) {
                    appendLine("Keluh kesah atau catatan tambahan pengguna: '$userNotes'")
                }
                appendLine("Tugasmu:")
                appendLine("1. Berikan ringkasan empati atas apa yang dirasakan pengguna.")
                appendLine("2. Berikan nasihat spiritual islami penyejuk hati yang penuh kelembutan, harapan, dan keimanan.")
                appendLine("3. Rekomendasikan 2 atau 3 doa shahih dari Al-Qur'an dan Hadist yang paling tepat untuk mengobati dan menguatkan kondisi hati tersebut.")
                appendLine("Setiap doa WAJIB memiliki teks Arab berharakat lengkap, transliterasi Latin yang jelas, terjemahan Indonesia, sumber dalil shahih, dan penjelasan keutamaan.")
                appendLine("4. Berikan tips amalan dzikir penenang hati.")
                appendLine("Berikan jawaban dalam format JSON sesuai schema.")
            }

            val response = model.generateContent(prompt)
            val jsonText = response.text ?: throw IllegalStateException("Model mengembalikan respon kosong.")

            parseResponse(jsonText)
        }
    }

    private fun parseResponse(jsonText: String): AiMoodRecommendation {
        val root = JSONObject(jsonText)
        val moodSummary = root.optString("moodSummary", "Kondisi hati yang membutuhkan ketenangan.")
        val spiritualWisdom = root.optString("spiritualWisdom", "Ingatlah, hanya dengan mengingat Allah hati menjadi tenang (QS. Ar-Ra'd: 28).")
        val dhikrTip = root.optString("dhikrTip", "Perbanyaklah istighfar dan membaca La hawla wa la quwwata illa billah.")

        val duasArray = root.getJSONArray("recommendedDuas")
        val duas = mutableListOf<DoaItem>()

        for (i in 0 until duasArray.length()) {
            val obj = duasArray.getJSONObject(i)
            val item = DoaItem(
                id = "ai_" + UUID.randomUUID().toString().take(8),
                title = obj.optString("title", "Doa Penyejuk Hati"),
                category = obj.optString("category", "Hadist Shahih"),
                arabicText = obj.optString("arabicText", "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ"),
                latinText = obj.optString("latinText", "Hasbunallah wa ni'mal wakil"),
                translation = obj.optString("translation", "Cukuplah Allah menjadi Penolong kami dan Allah adalah sebaik-baik Pelindung."),
                source = obj.optString("source", "QS. Ali Imran: 173"),
                benefit = obj.optString("benefit", "Mendatangkan ketenteraman batin dan pertolongan Allah."),
                recommendedCount = 3,
                isBookmarked = false,
                isFromAi = true
            )
            duas.add(item)
        }

        return AiMoodRecommendation(
            moodSummary = moodSummary,
            spiritualWisdom = spiritualWisdom,
            recommendedDuas = duas,
            dhikrTip = dhikrTip
        )
    }
}
