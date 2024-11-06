package com.example.crm_service.entities.message

import com.example.crm_service.entities.BaseEntity
import com.example.crm_service.services.message.MessageState
import jakarta.persistence.*
import java.util.*

@Entity
class Message (
    var sender: String,
    var date: Date,
    var subject: String,
    var body: String,
    var channel: String,
    var priority: Long,
    var state: MessageState
) : BaseEntity() {

    @OneToMany(mappedBy = "message", cascade = [(CascadeType.ALL)], fetch = FetchType.LAZY)
    val actions = mutableSetOf<Action>()

    fun addAction(action: Action) {
        actions.add(action)
        action.message = this
    }
}