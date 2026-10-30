package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DoaBookmarkDao {
    @Query("SELECT * FROM doa_bookmarks ORDER BY savedAt DESC")
    fun getAllBookmarks(): Flow<List<DoaBookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: DoaBookmarkEntity)

    @Query("DELETE FROM doa_bookmarks WHERE doaId = :doaId")
    suspend fun deleteBookmark(doaId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM doa_bookmarks WHERE doaId = :doaId)")
    suspend fun isBookmarked(doaId: String): Boolean
}
