package com.example.crm_service.repositories

import com.example.crm_service.entities.Note
import org.springframework.data.jpa.repository.JpaRepository

interface NoteRepository: JpaRepository<Note, Long> {
}