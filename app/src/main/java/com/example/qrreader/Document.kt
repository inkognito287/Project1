package com.example.qrreader

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "document_table")
data class Document(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val photo: Drawable,
    val date: Date,
    val code: String
)
