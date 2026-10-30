package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doa_bookmarks")
data class DoaBookmarkEntity(
    @PrimaryKey
    val doaId: String,
    val savedAt: Long = System.currentTimeMillis()
)
