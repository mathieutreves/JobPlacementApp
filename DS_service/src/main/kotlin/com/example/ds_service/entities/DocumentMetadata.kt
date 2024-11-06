package com.example.ds_service.entities

import jakarta.persistence.*
import java.util.*

@Entity
class DocumentMetadata (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var name: String,
    var size: Long,
    var creationTimestamp: Date,
    var contentType: String?,

    @OneToOne
    @MapsId
    var document: Document? = null
)