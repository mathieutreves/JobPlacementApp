package com.example.crm_service

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.KafkaTemplate

@SpringBootApplication
class CrmServiceApplication {

	@Bean
	fun topic() = NewTopic("topicTest", 10, 1)

	@Bean
	fun runner(template: KafkaTemplate<String?, String?>) =
		ApplicationRunner { template.send("topicTest", "test")}
}

fun main(args: Array<String>) {
	runApplication<CrmServiceApplication>(*args)
}
