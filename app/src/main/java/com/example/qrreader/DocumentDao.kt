package com.example.qrreader

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addDocument(document: Document)

    @Query("SELECT * FROM document_table ORDER BY id ASC")
    fun  readAllData(): LiveData <List<Document>>
}