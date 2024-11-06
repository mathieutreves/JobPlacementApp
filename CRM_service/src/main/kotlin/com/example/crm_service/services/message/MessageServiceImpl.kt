package com.example.crm_service.services.message

import com.example.crm_service.dtos.message.ActionDTO
import com.example.crm_service.dtos.message.MessageDTO
import com.example.crm_service.dtos.message.toDTO
import com.example.crm_service.entities.contact.Contact
import com.example.crm_service.entities.contact.Email
import com.example.crm_service.entities.contact.Phone
import com.example.crm_service.entities.message.Action
import com.example.crm_service.entities.message.Message
import com.example.crm_service.exceptions.message.MessageNotFoundException
import com.example.crm_service.exceptions.message.MessageStateInvalidTransitionException
import com.example.crm_service.repositories.contact.ContactRepository
import com.example.crm_service.repositories.contact.EmailRepository
import com.example.crm_service.repositories.contact.PhoneRepository
import com.example.crm_service.repositories.message.MessageRepository
import com.example.crm_service.services.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
@Transactional
class MessageServiceImpl(
    private val messageRepo: MessageRepository,
    private val emailRepo: EmailRepository,
    private val phoneRepo: PhoneRepository,
    private val contactRepo: ContactRepository,
    private val notificationService: NotificationService
) : MessageService {

    private val logger = LoggerFactory.getLogger(MessageServiceImpl::class.java)

    override fun getAllMessages(state: String?, sort: String, page: Int, size: Int): ResponseEntity<List<MessageDTO>> {
        val sortOrders = sort.split(",").let {
            Sort.by(it[1].lowercase().let { dir ->
                if (dir == "asc") Sort.Order.asc(it[0]) else Sort.Order.desc(it[0])
            })
        }

        val pageable = PageRequest.of(page, size, sortOrders)
        return if (state != null) {
            ResponseEntity(messageRepo.findAllByState(MessageState.valueOf(state), pageable).toList().map { it.toDTO() }, HttpStatus.OK)
        } else {
            ResponseEntity(messageRepo.findAll(pageable).toList().map { it.toDTO() }, HttpStatus.OK)
        }
    }

    override fun createMessage(sender: String, contactInfo: String?, channel: String, subject: String, body: String, priority: Long?): ResponseEntity<String> {

        // Check if message sender exists, if not, create a new "unknown" one
        if ((MessageChannel.valueOf(channel.uppercase()) == MessageChannel.EMAIL && !emailRepo.existsByEmail(sender)) ||
            ((MessageChannel.valueOf(channel.uppercase()) == MessageChannel.CALL
                    || MessageChannel.valueOf(channel.uppercase()) == MessageChannel.TEXT) && !phoneRepo.existsByNumber(sender))) {
            val contact = Contact(
                name = contactInfo?.split(' ')?.first()?: "???",
                surname = contactInfo?.split(' ')?.last()?:"???",
                ssnCode = null,
                category = "UNKNOWN"
            )

            if (MessageChannel.valueOf(channel.uppercase()) == MessageChannel.EMAIL)
                contact.addEmail(Email(sender))
            else
                contact.addTelephone(Phone(sender))

            contactRepo.save(contact).also { logger.info("Saved unknown contact with id: ${contact.id}") }
        }

        // Create a new message
        val newMessage = Message(
            sender = sender,
            date = Date.from(Instant.now()),
            subject = subject,
            body = body,
            channel = channel,
            priority = priority?: 0, // set the lowest priority if not provided
            state = MessageState.RECEIVED
        )

        // Create a new action, it represents the initial state of the message
        val newAction = Action(
            state = newMessage.state,
            date = newMessage.date,
            comment = "Message Received"
        )

        newMessage.addAction(newAction)
        messageRepo.save(newMessage)
        logger.info("Message received successfully with id ${newMessage.id} from sender $sender")
        return ResponseEntity("Message received successfully", HttpStatus.CREATED)
    }

    override fun getMessageById(messageId: Long): ResponseEntity<MessageDTO> {
        val message = messageRepo.findByIdOrNull(messageId)
            ?: throw MessageNotFoundException("Message not found: $messageId")

        return ResponseEntity(message.toDTO(), HttpStatus.OK)
    }

    override fun changeStateById(messageId: Long, newState: String, comment: String): ResponseEntity<String> {
        val message = messageRepo.findByIdOrNull(messageId)
            ?: throw MessageNotFoundException("Message not found: $messageId")

        val oldMessageState = message.state
        val newMessageState = MessageState.valueOf(newState.uppercase())

        // Check what valid transitions we can have
        val validTransitions = mapOf(
            MessageState.RECEIVED to setOf(MessageState.READ),
            MessageState.READ to setOf(MessageState.DISCARDED, MessageState.PROCESSING, MessageState.DONE, MessageState.FAILED),
            MessageState.PROCESSING to setOf(MessageState.DONE, MessageState.FAILED)
        )

        if (!validTransitions[oldMessageState]!!.contains(newMessageState)) {
            throw MessageStateInvalidTransitionException("Cannot change state of message from $oldMessageState to $newState")
        }

        // If the transition was valid, create a new action and add it to the message
        message.state = newMessageState
        message.addAction(Action(
            state = newMessageState,
            date = Date.from(Instant.now()),
            comment = comment
        ))

        // Call the email service, if we have the sender email information
        if (MessageChannel.valueOf(message.channel) == MessageChannel.EMAIL)
            notificationService.notifyEmailService(messageId, oldMessageState, newMessageState, message.sender)

        logger.info("Message $messageId state changed successfully from $oldMessageState to $newState")
        return ResponseEntity("Message $messageId state changed successfully from $oldMessageState to $newState", HttpStatus.OK)
    }

    override fun getHistoryById(messageId: Long): ResponseEntity<List<ActionDTO>> {
        val message = messageRepo.findByIdOrNull(messageId)
            ?: throw MessageNotFoundException("Message not found: $messageId")

        // Return all the history linked to that specific message
        return ResponseEntity(message.actions.map { it.toDTO() }, HttpStatus.OK)
    }

    override fun changePriorityById(messageId: Long, priority: Long): ResponseEntity<String> {
        val message = messageRepo.findByIdOrNull(messageId)
            ?: throw MessageNotFoundException("Message not found: $messageId")

        // Change the message priority
        val oldPriority = message.priority
        message.priority = priority
        logger.info("Message $messageId priority changed from $oldPriority to $priority")
        return ResponseEntity("Message $messageId priority changed from $oldPriority to $priority", HttpStatus.OK)
    }
}