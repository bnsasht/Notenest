package com.example.project.model

data class NoteModel(
    val noteId: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val pdfUrl: String = "",
    val timestamp: Long = 0L,
    val favorites: Map<String, Boolean> = emptyMap()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "noteId" to noteId,
            "userId" to userId,
            "title" to title,
            "description" to description,
            "pdfUrl" to pdfUrl,
            "timestamp" to timestamp,
            "favorites" to favorites
        )
    }
}