package com.example.crm_service.entities.message

import com.example.crm_service.entities.BaseEntity
import com.example.crm_service.services.message.MessageState
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import java.util.*

@Entity
class Action (
    var state: MessageState,
    var date: Date,
    var comment: String

) : BaseEntity () {

    @ManyToOne
    var message: Message? = null
}