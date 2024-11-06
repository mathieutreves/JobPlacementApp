package com.example.crm_service.services.customer

import com.example.crm_service.dtos.customer.CustomerDTO
import com.example.crm_service.dtos.customer.toDTO
import com.example.crm_service.entities.Note
import com.example.crm_service.entities.contact.Email
import com.example.crm_service.entities.customer.Customer
import com.example.crm_service.exceptions.contact.ContactNotFoundException
import com.example.crm_service.exceptions.jobOffer.JobOfferNotFoundException
import com.example.crm_service.repositories.NoteRepository
import com.example.crm_service.repositories.contact.ContactRepository
import com.example.crm_service.repositories.customer.CustomerRepository
import com.example.crm_service.repositories.jobOffer.JobOfferRepository
import com.example.crm_service.services.contact.ContactServiceImpl
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository,
    private val contactRepository: ContactRepository,
    private val jobOfficeRepo: JobOfferRepository,
    private val noteRepo: NoteRepository
): CustomerService {

    private val logger = LoggerFactory.getLogger(ContactServiceImpl::class.java)

    override fun getAllCustomer(page: Int, size: Int): ResponseEntity<List<CustomerDTO>> {
        val pageable = PageRequest.of(page, size)
        return ResponseEntity(customerRepository.findAll(pageable).toList().map { it.toDTO() }, HttpStatus.OK)
    }

    override fun createCustomer(name: String, surname: String): ResponseEntity<String> {
        val contact = contactRepository.findByNameAndSurname(name, surname)
            ?: throw ContactNotFoundException("You have to create the contact information before adding them as a customer figure")

        val customer = customerRepository.findByName(name)
        if (customer != null) {
            return ResponseEntity("Customer already exists", HttpStatus.CONFLICT)
        }

        val newCustomer= Customer(name, surname)

        // Link this professional profile to the contact
        contact.category = when (contact.category) {
            "PROFESSIONAL" -> "BOTH"
            else -> "CUSTOMER"
        }
        contact.customerProfile = newCustomer
        newCustomer.contact = contact

        customerRepository.save(newCustomer)
        logger.info("Customer $name created")
        return ResponseEntity("Customer $name created", HttpStatus.CREATED)
    }

    override fun addJobOfferToCustomer(customerId: Long, jobOfferId: Long): ResponseEntity<String> {
        val customerToUpdate =
            customerRepository.findByIdOrNull(customerId) ?: throw ContactNotFoundException("Customer not found: $customerId")

        val jobOffer =
            jobOfficeRepo.findByIdOrNull(jobOfferId) ?: throw JobOfferNotFoundException("Job offer not found: $jobOfferId")

        customerToUpdate.addJobOffer(jobOffer)
        logger.info("Job offer $jobOfferId added to customer $customerId")
        return ResponseEntity("Job offer $jobOfferId added to customer $customerId", HttpStatus.OK)
    }

    override fun removeJobOfferFromCustomer(customerId: Long, jobOfferId: Long): ResponseEntity<String> {
        val customerToUpdate =
            customerRepository.findByIdOrNull(customerId) ?: throw ContactNotFoundException("Customer not found: $customerId")

        val jobOffer =
            jobOfficeRepo.findByIdOrNull(jobOfferId) ?: throw JobOfferNotFoundException("Job offer not found: $jobOfferId")

        customerToUpdate.removeJobOffer(jobOffer)
        logger.info("Job offer $jobOfferId removed from customer $customerId")
        return ResponseEntity("Job offer $jobOfferId removed from customer $customerId", HttpStatus.OK)
    }

    override fun addNoteToCustomer(customerId: Long, note: String): ResponseEntity<String> {
        val customerToUpdate =
            customerRepository.findByIdOrNull(customerId) ?: throw ContactNotFoundException("Customer not found: $customerId")

        val newNote = Note(note)
        noteRepo.save(newNote)
        customerToUpdate.addNotes(newNote)
        logger.info("Note $note added to customer $customerId")
        return ResponseEntity("Note $note added to customer $customerId", HttpStatus.OK)
    }

    override fun removeNoteFromCustomer(customerId: Long, noteId: Long): ResponseEntity<String> {
        val customerToUpdate =
            customerRepository.findByIdOrNull(customerId) ?: throw ContactNotFoundException("Customer not found: $customerId")

        val note =
            noteRepo.findByIdOrNull(noteId) ?: throw ContactNotFoundException("Note not found: $noteId")

        customerToUpdate.removeNotes(note)
        noteRepo.deleteById(noteId)
        logger.info("Note $noteId removed from customer $customerId")
        return ResponseEntity("Note $noteId removed from customer $customerId", HttpStatus.OK)
    }
}