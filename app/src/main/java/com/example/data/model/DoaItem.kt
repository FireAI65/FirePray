package com.example.data.model

data class DoaItem(
    val id: String,
    val title: String,
    val category: String, // "Al-Qur'an", "Hadist Shahih", "Penenang Hati", "Harian", "Rezeki", "Kesembuhan", "Perlindungan"
    val arabicText: String,
    val latinText: String,
    val translation: String,
    val source: String,
    val benefit: String,
    val recommendedCount: Int = 1,
    val isBookmarked: Boolean = false,
    val isFromAi: Boolean = false
)

enum class MoodCategory(
    val id: String,
    val label: String,
    val emoji: String,
    val description: String
) {
    SEDIH("sedih", "Sedih & Hampa", "😔", "Meringankan duka lara dan kesedihan batin"),
    CEMAS("cemas", "Gelisah & Cemas", "😰", "Menenangkan pikiran kalut dan overthinking"),
    SYUKUR("syukur", "Penuh Syukur", "🤲", "Menambah nikmat dan kebahagiaan hidup"),
    BIMBANG("bimbang", "Bimbang & Ragu", "🤔", "Memohon petunjuk jalan terbaik / Istikharah"),
    SAKIT("sakit", "Sakit & Lemah", "🤒", "Memohon kesembuhan dan ketabahan raga"),
    MARAH("marah", "Marah & Emosi", "😡", "Meredam amarah dan bisikan setan"),
    REZEKI("rezeki", "Hajat & Rezeki", "💼", "Kemudahan ikhtiar halal dan pelunas hutang"),
    TOBAT("tobat", "Sesal & Tobat", "🥺", "Memohon ampunan dosa dan hati yang bersih")
}
