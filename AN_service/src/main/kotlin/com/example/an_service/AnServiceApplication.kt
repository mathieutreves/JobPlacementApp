package com.example.an_service

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaListener

@SpringBootApplication
class AnServiceApplication {

	@Bean
	fun topic() = NewTopic("topicTest", 10, 1)

	@KafkaListener(id = "analyticsTest", topics = ["topicTest"])
	fun listen(value: String?) {
		println(value)
	}
}

fun main(args: Array<String>) {
	runApplication<AnServiceApplication>(*args)
}
