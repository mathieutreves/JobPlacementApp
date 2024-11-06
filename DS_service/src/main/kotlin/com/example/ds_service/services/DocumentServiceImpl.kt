package com.example.ds_service.services

import com.example.ds_service.dtos.DocumentDTO
import com.example.ds_service.dtos.DocumentMetadataDTO
import com.example.ds_service.dtos.toDto
import com.example.ds_service.entities.Document
import com.example.ds_service.entities.DocumentMetadata
import com.example.ds_service.exceptions.DocumentNotFoundException
import com.example.ds_service.exceptions.DuplicateDocumentException
import com.example.ds_service.exceptions.WrongDocumentNameException
import com.example.ds_service.repositories.DocumentMetadataRepository
import com.example.ds_service.repositories.DocumentRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.util.Date

@Service
@Transactional
class DocumentServiceImpl(private val docRepo: DocumentRepository, private val docMetaRepo: DocumentMetadataRepository): DocumentService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun getAllDocuments(page: Int, limit: Int): ResponseEntity<List<DocumentDTO>> {
        return ResponseEntity(docRepo.findAll(PageRequest.of(page, limit)).toList().map { it.toDto() }, HttpStatus.OK)
    }

    override fun getDocumentMetadataByID(id: Long): ResponseEntity<DocumentMetadataDTO> {
        val docMetadata = docMetaRepo.findByIdOrNull(id)
        docMetadata ?: throw DocumentNotFoundException("Document not found")

        return ResponseEntity(docMetadata.toDto(), HttpStatus.OK)
    }

    override fun getByteContentByID(id: Long): ResponseEntity<ByteArray> {
        val doc = docRepo.findByIdOrNull(id)
        doc ?: throw DocumentNotFoundException("Document not found")

        val headers = HttpHeaders()
        headers.add(HttpHeaders.CONTENT_TYPE, doc.metadata?.contentType)

        return ResponseEntity(doc.content, headers, HttpStatus.OK)
    }

    override fun createDocument(name: String, file: MultipartFile): ResponseEntity<String> {
        // Validate filename
        if (!name.matches("[-_. A-Za-z0-9]+".toRegex()))
            throw WrongDocumentNameException("Name should contains only valid characters")

        // Check if file exists
        if (docMetaRepo.existsByNameIgnoreCase(name))
            throw DuplicateDocumentException("A document with the same name already exists")

        // Create DocumentMetadata entity
        val docMetadata = DocumentMetadata(
            name = name,
            size = file.size,
            creationTimestamp = Date.from(Instant.now()),
            contentType = file.contentType
        )

        // Create Document entity and set metadata
        val doc = Document(content = file.bytes, metadata = docMetadata)
        // Link metadata to document
        docMetadata.document = doc

        // Save document in repo
        docRepo.save(doc)
        log.info("Created new document with id:${doc.id} (name: ${name}, size: ${file.size}, contentType: ${file.contentType})")
        return ResponseEntity("Document created", HttpStatus.CREATED)
    }

    override fun updateDocument(id: Long, name: String, file: MultipartFile) : ResponseEntity<String> {
        // Validate filename
        if (!name.matches("[-_. A-Za-z0-9]+".toRegex()))
            throw WrongDocumentNameException("Name should contains only valid characters")

        val docToUpdate = docRepo.findByIdOrNull(id)
        val docMetaToUpdate = docMetaRepo.findByIdOrNull(id)

        docToUpdate ?: throw DocumentNotFoundException("Document not found")
        docMetaToUpdate ?: throw DocumentNotFoundException("Document metadata not found")

        val oldName = docMetaToUpdate.name
        val oldSize = docMetaToUpdate.size
        val oldContentType = docMetaToUpdate.contentType

        docMetaToUpdate.name = name
        docMetaToUpdate.size = file.size
        //docMetaToUpdate.creationTimestamp = Date.from(Instant.now()) PUT should be idempotent (?)
        docMetaToUpdate.contentType = file.contentType

        docToUpdate.content = file.bytes
        docToUpdate.metadata = docMetaToUpdate

        log.info("Updated document with id:$id (name: $oldName -> ${name}, size: $oldSize -> ${file.size}, contentType: $oldContentType -> ${file.contentType})")
        return ResponseEntity("Document updated", HttpStatus.OK)
    }

    override fun deleteDocument(id: Long) : ResponseEntity<String> {
        val doc = docRepo.findByIdOrNull(id)
        doc ?: throw DocumentNotFoundException("Document not found")

        docRepo.deleteById(id)
        log.info("Deleted document with id:$id")
        return ResponseEntity("Document deleted", HttpStatus.OK)
    }
}