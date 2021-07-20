package com.example.qrreader

import androidx.lifecycle.LiveData

class DocumentRepository(private val documentDao: DocumentDao) {
    val  readAllData: LiveData<List<Document>> = documentDao.readAllData()

    suspend fun addDocument (document: Document){
        documentDao.addDocument(document)
    }
}