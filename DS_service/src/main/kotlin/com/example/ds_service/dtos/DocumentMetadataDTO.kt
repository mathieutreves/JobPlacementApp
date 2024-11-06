package com.example.ds_service.dtos

import com.example.ds_service.entities.DocumentMetadata
import java.util.*

data class DocumentMetadataDTO(
    val id: Long?,
    val name: String,
    val size: Long,
    val creationTimestamp: Date,
    val contentType: String?
)

fun DocumentMetadata.toDto(): DocumentMetadataDTO =
    DocumentMetadataDTO(this.id, this.name, this.size, this.creationTimestamp, this.contentType)
