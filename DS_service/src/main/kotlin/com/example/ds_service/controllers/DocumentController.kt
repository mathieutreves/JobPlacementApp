package com.example.ds_service.controllers

import com.example.ds_service.dtos.DocumentDTO
import com.example.ds_service.dtos.DocumentMetadataDTO
import com.example.ds_service.services.DocumentService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/API/documents")
class DocumentController(private val documentService: DocumentService) {
    @GetMapping("/", "")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getAllDocuments(
        @RequestParam(name = "page") page: Int,
        @RequestParam(name = "limit") limit: Int
    ): ResponseEntity<List<DocumentDTO>> {
        return documentService.getAllDocuments(page, limit)
    }

    @GetMapping("/{metadataId}")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getDocumentMetadataById(@PathVariable("metadataId") id: Long): ResponseEntity<DocumentMetadataDTO> {
        return documentService.getDocumentMetadataByID(id)
    }

    @GetMapping("/{metadataId}/data")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getByteContentById(@PathVariable("metadataId") id: Long): ResponseEntity<ByteArray> {
        return documentService.getByteContentByID(id)
    }

    @PostMapping("/", "")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun createDocument(
        @RequestParam("name") name: String,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<String> {
        return documentService.createDocument(name, file)
    }

    @PutMapping("/{metadataId}", consumes = ["multipart/form-data"])
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun updateDocument(
        @PathVariable("metadataId") id: Long,
        @RequestParam("name") name: String,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<String> {
        return documentService.updateDocument(id, name, file)
    }

    @DeleteMapping("/{metadataId}")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun deleteDocument(@PathVariable("metadataId") id: Long) : ResponseEntity<String> {
        return documentService.deleteDocument(id)
    }
}