package com.example.qrreader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DocumentViewModel(application: Application): AndroidViewModel(application) {

    private val readAllData: LiveData<List<Document>>
    private val repository: DocumentRepository


    init {
        val documentDao= DocumentDatabase.getDatabase(application).documentDao()
        repository= DocumentRepository(documentDao)
        readAllData=repository.readAllData
    }

    fun addDocument(document: Document){
        viewModelScope.launch(Dispatchers.IO){
            repository.addDocument(document)
        }
    }
}