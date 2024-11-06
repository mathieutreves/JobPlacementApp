package com.example.crm_service.entities

import jakarta.persistence.*
import org.springframework.data.util.ProxyUtils

@MappedSuperclass
open class BaseEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0L

    override fun equals(other: Any?): Boolean {
        if (null === other) return false
        if (this === other) return true
        if (javaClass != ProxyUtils.getUserClass(other)) return false
        other as BaseEntity
        return if (0L == id) false
            else this.id == other.id
    }

    override fun hashCode() = 42

    override fun toString() = "@Entity ${this.javaClass.name}(id=$id)"
}