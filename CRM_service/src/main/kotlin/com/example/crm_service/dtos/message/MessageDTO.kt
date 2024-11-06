package com.example.crm_service.dtos.message

import com.example.crm_service.entities.message.Message
import java.util.*

data class MessageDTO(
    val id: Long,
    val sender: String,
    val date: Date,
    val subject: String,
    val body: String,
    val channel: String,
    val priority: Long,
    val state: String,
    val actions: List<ActionDTO>
)

fun Message.toDTO(): MessageDTO =
    MessageDTO(this.id, this.sender, this.date, this.subject, this.body, this.channel, this.priority, this.state.toString(), this.actions.map { it.toDTO() })