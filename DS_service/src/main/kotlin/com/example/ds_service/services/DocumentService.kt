package com.example.ds_service.services

import com.example.ds_service.dtos.DocumentDTO
import com.example.ds_service.dtos.DocumentMetadataDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile

interface DocumentService {
    fun getAllDocuments(page: Int, limit: Int): ResponseEntity<List<DocumentDTO>>
    fun getDocumentMetadataByID(id: Long): ResponseEntity<DocumentMetadataDTO>
    fun getByteContentByID(id: Long): ResponseEntity<ByteArray>
    fun createDocument(name : String, file: MultipartFile) : ResponseEntity<String>
    fun updateDocument(id: Long, name: String, file: MultipartFile): ResponseEntity<String>
    fun deleteDocument(id: Long) : ResponseEntity<String>
}