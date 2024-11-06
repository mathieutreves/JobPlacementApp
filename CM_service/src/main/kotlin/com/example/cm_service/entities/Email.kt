package com.example.cm_service.entities

class Email (
    var from: String?,
    var to: String,
    var subject: String,
    var body: String,
    var priority: Long?
)