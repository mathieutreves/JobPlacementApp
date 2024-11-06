package com.example.crm_service.dtos.message

import com.example.crm_service.entities.message.Action
import java.util.*

data class ActionDTO(
    val id: Long,
    val state: String,
    val date: Date,
    val comment: String
)

fun Action.toDTO(): ActionDTO =
    ActionDTO(this.id, this.state.toString(), this.date, this.comment)