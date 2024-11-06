package com.example.ds_service.repositories

import com.example.ds_service.entities.DocumentMetadata
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DocumentMetadataRepository: JpaRepository<DocumentMetadata, Long> {
    fun existsByNameIgnoreCase(name: String) : Boolean
}