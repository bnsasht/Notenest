package com.example.project.repository

import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.project.model.NoteModel
import com.google.firebase.database.*

class NoteRepoImplementation : NoteRepo {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("Notes")

    override fun uploadPdfToStorage(fileUri: Uri, callback: (Boolean, String) -> Unit) {
        MediaManager.get().upload(fileUri)
            .unsigned("note_app_preset")
            .option("resource_type", "raw")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    Log.d("Cloudinary", "Upload started...")
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                }

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {

                    val publicUrl = resultData?.get("secure_url") as? String

                    if (!publicUrl.isNullOrEmpty()) {
                        Log.d("Cloudinary", "Upload success: $publicUrl")
                        callback(true, publicUrl)
                    } else {
                        Log.e("Cloudinary", "secure_url is null")
                        callback(false, "Upload failed: URL missing")
                    }
                }


                override fun onError(requestId: String?, error: ErrorInfo?) {
                    val errorMsg = error?.description ?: "Unknown Cloudinary Error"
                    Log.e("Cloudinary", "Upload failed: $errorMsg")
                    callback(false, errorMsg)
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                }
            })
            .dispatch()
    }


    override fun addNoteToDatabase(noteId: String, model: NoteModel, callback: (Boolean, String) -> Unit) {
        ref.child(noteId).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Note Published")
            else callback(false, it.exception?.message ?: "Database Error")
        }
    }

    override fun getAllNotes(callback: (Boolean, List<NoteModel>?) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notes = mutableListOf<NoteModel>()
                for (child in snapshot.children) {
                    child.getValue(NoteModel::class.java)?.let { notes.add(it) }
                }
                callback(true, notes)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(false, null)
            }
        })
    }

    override fun deleteNote(noteId: String, callback: (Boolean, String) -> Unit) {
        ref.child(noteId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Deleted Successfully")
            else callback(false, it.exception?.message ?: "Error")
        }
    }

    override fun updateNote(noteId: String, model: NoteModel, callback: (Boolean, String) -> Unit) {
        ref.child(noteId).updateChildren(model.toMap()).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Updated Successfully")
            else callback(false, it.exception?.message ?: "Error")
        }
    }

    override fun toggleFavorite(noteId: String, userId: String, isFavorite: Boolean, callback: (Boolean, String) -> Unit) {
        val favRef = ref.child(noteId).child("favorites").child(userId)
        if (isFavorite) {
            favRef.setValue(true).addOnCompleteListener { callback(it.isSuccessful, "") }
        } else {
            favRef.removeValue().addOnCompleteListener { callback(it.isSuccessful, "") }
        }
    }
}