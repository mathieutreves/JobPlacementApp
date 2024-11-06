package com.example.ds_service.entities

import jakarta.persistence.*

@Entity
class Document (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Lob
    var content: ByteArray,

    @OneToOne(mappedBy = "document", cascade = [(CascadeType.ALL)])
    @PrimaryKeyJoinColumn
    var metadata: DocumentMetadata? = null
)