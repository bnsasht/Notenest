package com.example.project.repository

import android.net.Uri
import com.example.project.model.NoteModel

interface NoteRepo {
    fun uploadPdfToStorage(fileUri: Uri, callback: (Boolean, String) -> Unit)

    fun addNoteToDatabase(noteId: String, model: NoteModel, callback: (Boolean, String) -> Unit)

    fun getAllNotes(callback: (Boolean, List<NoteModel>?) -> Unit)

    fun deleteNote(noteId: String, callback: (Boolean, String) -> Unit)

    fun updateNote(noteId: String, model: NoteModel, callback: (Boolean, String) -> Unit)

    fun toggleFavorite(noteId: String, userId: String, isFavorite: Boolean, callback: (Boolean, String) -> Unit)
}