package com.example.crm_service.services.contact

import com.example.crm_service.dtos.contact.ContactDTO
import com.example.crm_service.dtos.contact.toDTO
import com.example.crm_service.entities.contact.Address
import com.example.crm_service.entities.contact.Contact
import com.example.crm_service.entities.contact.Email
import com.example.crm_service.entities.contact.Phone
import com.example.crm_service.exceptions.contact.*
import com.example.crm_service.repositories.contact.*
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ContactServiceImpl(
    private val contactRepo: ContactRepository,
    private val emailRepo: EmailRepository,
    private val addressRepo: AddressRepository,
    private val phoneRepo: PhoneRepository
) : ContactService {

    private val logger = LoggerFactory.getLogger(ContactServiceImpl::class.java)

    override fun getAllContacts(
        email: String?,
        address: String?,
        phone: String?,
        name: String?,
        surname: String?,
        page: Int,
        size: Int
    ): ResponseEntity<List<ContactDTO>> {
        val pageable = PageRequest.of(page, size)

        // Use parameters passed to the endpoint to filter the search in the DB
        val specifications = listOfNotNull(
            ContactSpecifications.withEmail(email),
            ContactSpecifications.withAddress(address),
            ContactSpecifications.withPhone(phone),
            ContactSpecifications.withName(name),
            ContactSpecifications.withSurname(surname)
        )

        val specs = if (specifications.isNotEmpty()) {
            specifications.reduce(Specification<Contact>::and)
        } else {
            Specification.where(null) // This acts as a no-op specification.
        }

        // Return the filtered search, also page it if necessary
        return ResponseEntity(contactRepo.findAll(specs, pageable).toList().map { it.toDTO() }, HttpStatus.OK)
    }

    override fun getContactById(contactId: Long): ResponseEntity<ContactDTO> {
        val contact = contactRepo.findByIdOrNull(contactId)
            ?: throw ContactNotFoundException("Contact not found: $contactId")

        return ResponseEntity(contact.toDTO(), HttpStatus.OK)
    }

    override fun createContact(
        name: String,
        surname: String,
        ssnCode: String?,
        category: String?,
        email: String?,
        address: String?,
        telephone: String?
    ): ResponseEntity<String> {

        val newContact = Contact(
            name = name, surname = surname, ssnCode = ssnCode, category = category ?: "UNKNOWN"
        )

        email?.let { newContact.addEmail(Email(it)) }
        telephone?.let { newContact.addTelephone(Phone(it)) }
        address?.let {
            val parts = it.split(',').map(String::trim)
            if (parts.size == 5) {
                newContact.addAddress(Address(parts[0], parts[1], parts[2], parts[3], parts[4]))
            } else {
                logger.error("Invalid address format")
                return ResponseEntity("Invalid address format", HttpStatus.BAD_REQUEST)
            }
        }
        val existingContact = contactRepo.findByNameAndSurname(newContact.name, newContact.surname)
        if (existingContact != null) {
            return ResponseEntity("Contact already exists", HttpStatus.CONFLICT)
        } else {
            contactRepo.save(newContact)
            logger.info("Contact created successfully with id ${newContact.id}: $name $surname")
            return ResponseEntity("Contact created successfully", HttpStatus.CREATED)
        }
    }

    override fun addEmail(contactId: Long, newEmail: String): ResponseEntity<String> {
        val contactToUpdate =
            contactRepo.findByIdOrNull(contactId) ?: throw ContactNotFoundException("Contact not found: $contactId")

        // Check if email is already saved in the repo. If not, create a new one.
        var responseStatus = HttpStatus.OK
        val email = emailRepo.findByEmail(newEmail) ?: Email(newEmail).also { emailRepo.save(it) }
            .also { logger.info("Created new email with id ${it.id}") }.also { responseStatus = HttpStatus.CREATED }
        contactToUpdate.addEmail(email)
        logger.info("Email $newEmail added successfully to contact $contactId with id: ${email.id}")
        return ResponseEntity("Email added successfully", responseStatus)
    }

    override fun deleteEmail(contactId: Long, emailId: Long): ResponseEntity<String> {
        val contactToUpdate =
            contactRepo.findByIdOrNull(contactId) ?: throw ContactNotFoundException("Contact not found: $contactId")

        val emailToRemove =
            emailRepo.findByIdOrNull(emailId) ?: throw EmailNotFoundException("Email not found: $emailId")

        // Check if the email is actually associated to this contact
        return if (contactToUpdate.emails.contains(emailToRemove)) {
            // Remove email from contact and vice versa
            contactToUpdate.removeEmail(emailToRemove)

            // Check if the email has other contact associated to it. If not, remove it from the DB.
            emailToRemove.contacts.isEmpty().takeIf { it }?.let { emailRepo.deleteById(emailId) }
                .also { logger.info("Email with id $emailId removed from the DB") }

            logger.info("Email $emailToRemove removed successfully from contact $contactId")
            ResponseEntity("Email removed successfully from the contact", HttpStatus.OK)
        } else {
            ResponseEntity("This email is not associated to the contact", HttpStatus.FORBIDDEN)
        }
    }

    override fun changeEmail(contactId: Long, emailId: Long, newEmail: String): ResponseEntity<String> {
        val contactToUpdate =
            contactRepo.findByIdOrNull(contactId) ?: throw ContactNotFoundException("Contact not found: $contactId")

        val emailToUpdate =
            emailRepo.findByIdOrNull(emailId) ?: throw EmailNotFoundException("Email not found: $emailId")

        // Check if the email is actually associated to this contact
        if (!emailToUpdate.contacts.contains(contactToUpdate)) return ResponseEntity(
            "This email is not associated to the contact",
            HttpStatus.FORBIDDEN
        )

        // Check if email is associated to only this contact or more
        if (emailToUpdate.contacts.size == 1) {
            // Update the email without changing the id
            val oldEmail = emailToUpdate.email
            emailToUpdate.email = newEmail
            logger.info("Email $oldEmail with id ${emailToUpdate.id} updated to $newEmail")
            return ResponseEntity("Email updated successfully", HttpStatus.OK)
        } else {
            // Disassociate the old email and the contact
            contactToUpdate.removeEmail(emailToUpdate)

            // Check if the email we want to change to is already present in the DB.
            // If so, associate it to the contact, if not, create a new one.
            val email = emailRepo.findByEmail(newEmail) ?: Email(newEmail).also { emailRepo.save(it) }
                .also { logger.info("Created new email with id ${it.id}") }
            contactToUpdate.addEmail(email)
            logger.info("Email of contact $contactId updated successfully to ${email.email} with id: ${email.id}")
            return ResponseEntity("Email updated successfully", HttpStatus.CREATED)
        }
    }

    override fun addAddress(contactId: Long, newAddress: String): ResponseEntity<String> {
        val contactToUpdate =
            contactRepo.findByIdOrNull(contactId) ?: throw ContactNotFoundException("Contact not found: $contactId")

        val addressParts = newAddress.split(',').map(String::trim)
        if (addressParts.size != 5) {
            logger.error("Invalid address format provided: $newAddress")
            throw AddressWrongFormatException("Invalid address format provided")
        }

        // Check if address is already saved in the repo. If not, create a new one.
        var responseStatus = HttpStatus.OK
        val address = addressRepo.findByStreetAndNumberAndCityAndPostalCodeAndCountry(
            addressParts[0], addressParts[1], addressParts[2], addressParts[3], addressParts[4]
        ) ?: Address(
            street = addressParts[0],
            number = addressParts[1],
            city = addressParts[2],
            postalCode = addressParts[3],
            country = addressParts[4]
        ).also { addressRepo.save(it) }.also { logger.info("Created new address with id ${it.id}") }
            .also { responseStatus = HttpStatus.CREATED }

        contactToUpdate.addAddress(address)
        logger.info("Address $newAddress added successfully to contact $contactId with id: ${address.id}")
        return ResponseEntity("Address added successfully", responseStatus)
    }

    override fun deleteAddress(contactId: Long, addressId: Long): ResponseEntity<String> {
        val contactToUpdate =
            contactRepo.findByIdOrNull(contactId) ?: throw ContactNotFoundException("Contact not found: $contactId")

        val addressToRemove =
            addressRepo.findByIdOrNull(addressId) ?: throw AddressNotFoundException("Address not found: $addressId")

        // Check if the address is actually associated to this contact
        return if (contactToUpdate.addresses.contains(addressToRemove)) {
            contactToUpdate.removeAddress(addressToRemove)

            // Check if the address has other contact associated to it. If not, delete it
            addressToRemove.contacts.isEmpty().takeIf { it }?.let { addressRepo.deleteById(addressId) }
                .also { logger.info("Address with id $addressId removed from the DB") }

            logger.info("Address $addressToRemove removed successfully from contact $contactId")
            ResponseEntity("Address removed successfully from the contact", HttpStatus.OK)
        } else {
            ResponseEntity("This address is not associated to the contact", HttpStatus.FORBIDDEN)
        }
    }

    override fun changeAddress(contactId: Long, addressId: Long, newAddress: String): ResponseEntity<String> {
        val contactToUpdate =
            contactRepo.findByIdOrNull(contactId) ?: throw ContactNotFoundException("Contact not found: $contactId")

        val addressToUpdate =
            addressRepo.findByIdOrNull(addressId) ?: throw AddressNotFoundException("Address not found: $addressId")

        // Check if the address is actually associated to this contact
        if (!addressToUpdate.contacts.contains(contactToUpdate)) return ResponseEntity(
            "This address is not associated to the contact",
            HttpStatus.FORBIDDEN
        )

        // Prepare the new address parts
        val addressParts = newAddress.split(',').map(String::trim)
        if (addressParts.size != 5) {
            logger.error("Invalid address format provided: $newAddress")
            throw AddressWrongFormatException("Invalid address format provided")
        }

        // Check if address is associated to only this contact or more
        if (addressToUpdate.contacts.size == 1) {
            // Update the address directly
            with(addressToUpdate) {
                street = addressParts[0]
                number = addressParts[1]
                city = addressParts[2]
                postalCode = addressParts[3]
                country = addressParts[4]
            }

            logger.info("Address with id ${addressToUpdate.id} updated")
            return ResponseEntity("Address updated successfully", HttpStatus.OK)
        } else {
            // Disassociate the old address and the contact
            contactToUpdate.removeAddress(addressToUpdate)

            // Check if the address we want to change to is already present in the DB.
            // If so, associate it to the contact, if not, create a new one.
            val address = addressRepo.findByStreetAndNumberAndCityAndPostalCodeAndCountry(
                addressParts[0], addressParts[1], addressParts[2], addressParts[3], addressParts[4]
            ) ?: Address(
                street = addressParts[0],
                number = addressParts[1],
                city = addressParts[2],
                postalCode = addressParts[3],
                country = addressParts[4]
            ).also { addressRepo.save(it) }.also { logger.info("Created new address with id ${it.id}") }

            contactToUpdate.addAddress(address)
            logger.info("Address of contact $contactId updated successfully with id: ${address.id}")
            return ResponseEntity("Address updated successfully", HttpStatus.CREATED)
        }
    }

    override fun addPhone(contactId: Long, newPhone: String): ResponseEntity<String> {
        val contactToUpdate =
            contactRepo.findByIdOrNull(contactId) ?: throw ContactNotFoundException("Contact not found: $contactId")

        // Check if phone number is already saved in the repo. If not, create a new one
        var responseStatus = HttpStatus.OK
        val phone = phoneRepo.findByNumber(newPhone) ?: Phone(newPhone).also { phoneRepo.save(it) }
            .also { logger.info("Created new phone with id: ${it.id}") }.also { responseStatus = HttpStatus.CREATED }
        contactToUpdate.addTelephone(phone)
        logger.info("Phone $newPhone added successfully to contact $contactId with id: ${phone.id}")
        return ResponseEntity("Phone added successfully", responseStatus)
    }

    override fun deletePhone(contactId: Long, phoneId: Long): ResponseEntity<String> {
        val contactToUpdate =
            contactRepo.findByIdOrNull(contactId) ?: throw ContactNotFoundException("Contact not found: $contactId")

        val phoneToRemove =
            phoneRepo.findByIdOrNull(phoneId) ?: throw PhoneNotFoundException("Phone not found: $phoneId")

        // Check if the phone is actually associated to this contact
        return if (contactToUpdate.phoneNumbers.contains(phoneToRemove)) {
            // Remove phone from contact and vice versa
            contactToUpdate.removeTelephone(phoneToRemove)

            // Check if the phone has other contact associated to it. If not, remove it from the DB
            phoneToRemove.contacts.isEmpty().takeIf { it }?.let { phoneRepo.deleteById(phoneId) }
                .also { logger.info("Phone with id $phoneId removed from the DB") }

            logger.info("Phone $phoneToRemove removed successfully from contact $contactId")
            ResponseEntity("Phone removed successfully", HttpStatus.OK)
        } else {
            ResponseEntity("This phone is not associated to the contact", HttpStatus.FORBIDDEN)
        }
    }

    override fun changePhone(contactId: Long, phoneId: Long, newPhone: String): ResponseEntity<String> {
        val contactToUpdate =
            contactRepo.findByIdOrNull(contactId) ?: throw ContactNotFoundException("Contact not found: $contactId")

        val phoneToUpdate =
            phoneRepo.findByIdOrNull(phoneId) ?: throw PhoneNotFoundException("Phone not found: $phoneId")

        // Check if the phone is actually associated to this contact
        if (!phoneToUpdate.contacts.contains(contactToUpdate)) return ResponseEntity(
            "This phone is not associated to the contact",
            HttpStatus.FORBIDDEN
        )

        // Check if phone is associated to only this contact or more
        if (phoneToUpdate.contacts.size == 1) {
            // Update the email without changing the id
            val oldPhone = phoneToUpdate.number
            phoneToUpdate.number = newPhone
            logger.info("Phone $oldPhone with id ${phoneToUpdate.id} updated to $newPhone")
            return ResponseEntity("Phone updated successfully", HttpStatus.OK)
        } else {
            // Disassociate the old email and the contact
            contactToUpdate.removeTelephone(phoneToUpdate)

            // Check if the phone we want to change to is already present in the DB.
            // If so, associate it to the contact, if not, create a new one.
            val phone = phoneRepo.findByNumber(newPhone) ?: Phone(newPhone).also { phoneRepo.save(it) }
                .also { logger.info("Created new phone with id: ${it.id}") }
            contactToUpdate.addTelephone(phone)
            logger.info("Phone of contact $contactId updated successfully to ${phone.number} with id: ${phone.id}")
            return ResponseEntity("Phone updated successfully", HttpStatus.CREATED)
        }
    }

    override fun changeName(contactId: Long, newName: String): ResponseEntity<String> {
        val contactToUpdate =
            contactRepo.findByIdOrNull(contactId) ?: throw ContactNotFoundException("Contact not found: $contactId")

        val oldName = contactToUpdate.name
        contactToUpdate.name = newName
        logger.info("Contact name changed from $oldName to $newName")
        return ResponseEntity("Updated contact $contactId name: $newName", HttpStatus.OK)
    }

    override fun changeSurname(contactId: Long, newSurname: String): ResponseEntity<String> {
        val contactToUpdate =
            contactRepo.findByIdOrNull(contactId) ?: throw ContactNotFoundException("Contact not found: $contactId")

        val oldSurname = contactToUpdate.surname
        contactToUpdate.surname = newSurname
        logger.info("Contact surname changed from $oldSurname to $newSurname")
        return ResponseEntity("Updated contact $contactId surname: $newSurname", HttpStatus.OK)
    }

    override fun changeSSNCode(contactId: Long, newSSNCode: String): ResponseEntity<String> {
        val contactToUpdate =
            contactRepo.findByIdOrNull(contactId) ?: throw ContactNotFoundException("Contact not found: $contactId")

        val oldSsnCode = contactToUpdate.ssnCode
        contactToUpdate.ssnCode = newSSNCode
        logger.info("Contact ssnCode changed from $oldSsnCode to $newSSNCode")
        return ResponseEntity("Updated contact $contactId ssnCode: $newSSNCode", HttpStatus.OK)
    }

    override fun changeCategory(contactId: Long, newCategory: String): ResponseEntity<String> {
        val contactToUpdate =
            contactRepo.findByIdOrNull(contactId) ?: throw ContactNotFoundException("Contact not found: $contactId")

        val oldCategory = contactToUpdate.category
        contactToUpdate.category = newCategory
        logger.info("Contact category changed from $oldCategory to $newCategory")
        return ResponseEntity("Updated contact $contactId category: $newCategory", HttpStatus.OK)
    }
}