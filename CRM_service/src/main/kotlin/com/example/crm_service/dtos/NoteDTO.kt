package com.example.crm_service.dtos

import com.example.crm_service.entities.Note

data class NoteDTO(
    val id: Long,
    val note: String
)

fun Note.toDTO(): NoteDTO =
    NoteDTO(this.id, this.note)