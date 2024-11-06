package com.example.ds_service.dtos

import com.example.ds_service.entities.Document

@Suppress("ArrayInDataClass")
data class DocumentDTO(
    val id: Long?,
    val content: ByteArray
)

fun Document.toDto() : DocumentDTO =
    DocumentDTO(this.id, this.content)
