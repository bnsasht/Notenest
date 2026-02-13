package com.example.project.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project.model.NoteModel
import com.example.project.repository.NoteRepoImplementation

class NoteViewModel(val repo: NoteRepoImplementation) : ViewModel() {

    private val _allNotes = MutableLiveData<List<NoteModel>?>()
    val allNotes: MutableLiveData<List<NoteModel>?> get() = _allNotes


    fun uploadPdfToStorage(fileUri: Uri, onResult: (Boolean, String) -> Unit) {
        repo.uploadPdfToStorage(fileUri) { success, result ->
            onResult(success, result)
        }
    }

    fun addNote(noteId: String, model: NoteModel, callback: (Boolean, String) -> Unit) {
        repo.addNoteToDatabase(noteId, model, callback)
    }

    fun getAllNotes() {
        repo.getAllNotes { success, data ->
            if (success) _allNotes.postValue(data)
        }
    }

    fun deleteNote(noteId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteNote(noteId, callback)
    }

    fun updateNote(noteId: String, model: NoteModel, callback: (Boolean, String) -> Unit) {
        repo.updateNote(noteId, model, callback)
    }

    fun toggleFavorite(noteId: String, userId: String, isFavorite: Boolean) {
        repo.toggleFavorite(noteId, userId, isFavorite) { _, _ -> }
    }
}