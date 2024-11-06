package com.example.crm_service.controllers

import com.example.crm_service.dtos.contact.ContactDTO
import com.example.crm_service.services.contact.ContactService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/API/contacts")
class ContactController (private val contactService: ContactService) {

    @GetMapping("/", "")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getAllContacts(
        @RequestParam("email", required = false) email: String?,
        @RequestParam("address", required = false) address: String?,
        @RequestParam("phone", required = false) phone: String?,
        @RequestParam("name", required = false) name: String?,
        @RequestParam("surname", required = false) surname: String?,
        @RequestParam("page", required = false, defaultValue = "0") page: Int,
        @RequestParam("size", required = false, defaultValue = "10") size: Int
    ) : ResponseEntity<List<ContactDTO>> {
        return contactService.getAllContacts(email, address, phone, name, surname, page, size)
    }

    @GetMapping("/{contactId}")
    @PreAuthorize("hasRole('ROLE_GUEST') or hasRole('ROLE_OPERATOR')")
    fun getContactById(
        @PathVariable("contactId") contactId: Long
    ) : ResponseEntity<ContactDTO> {
        return contactService.getContactById(contactId)
    }

    @PostMapping("/", "")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun createContact(
        @RequestParam("name") name: String,
        @RequestParam("surname") surname: String,
        @RequestParam("SSNCode", required = false) ssnCode: String?,
        @RequestParam("category", required = false) category: String?,
        @RequestParam("email", required = false) email: String?,
        @RequestParam("telephone", required = false) telephone: String?,
        @RequestParam("address", required = false) address: String?
    ) : ResponseEntity<String> {
        return contactService.createContact(name, surname, ssnCode, category, email, address, telephone)
    }


    @PostMapping("/{contactId}/email")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun addEmail(
        @PathVariable("contactId") contactId: Long,
        @RequestParam("email") newEmail: String
    ) : ResponseEntity<String> {
        return contactService.addEmail(contactId, newEmail)
    }

    @DeleteMapping("/{contactId}/email/{emailId}")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun deleteEmail(
        @PathVariable("contactId") contactId: Long,
        @PathVariable("emailId") emailId: Long
    ) : ResponseEntity<String> {
        return contactService.deleteEmail(contactId, emailId)
    }

    @PutMapping("/{contactId}/email/{emailId}")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun changeEmail(
        @PathVariable("contactId") contactId: Long,
        @PathVariable("emailId") emailId: Long,
        @RequestParam("email") email: String
    ) : ResponseEntity<String> {
        return contactService.changeEmail(contactId, emailId, email)
    }


    @PostMapping("/{contactId}/address")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun addAddress(
        @PathVariable("contactId") contactId: Long,
        @RequestParam("address") newAddress: String
    ) : ResponseEntity<String> {
        return contactService.addAddress(contactId, newAddress)
    }

    @DeleteMapping("/{contactId}/address/{addressId}")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun deleteAddress(
        @PathVariable("contactId") contactId: Long,
        @PathVariable("addressId") addressId: Long
    ) : ResponseEntity<String> {
        return contactService.deleteAddress(contactId, addressId)
    }

    @PutMapping("/{contactId}/address/{addressId}")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun changeAddress(
        @PathVariable("contactId") contactId: Long,
        @PathVariable("addressId") addressId: Long,
        @RequestParam("address") address: String
    ) : ResponseEntity<String> {
        return contactService.changeAddress(contactId, addressId, address)
    }


    @PostMapping("/{contactId}/phone")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun addPhone(
        @PathVariable("contactId") contactId: Long,
        @RequestParam("phone") newPhone: String
    ) : ResponseEntity<String> {
        return contactService.addPhone(contactId, newPhone)
    }

    @DeleteMapping("/{contactId}/phone/{phoneId}")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun deletePhone(
        @PathVariable("contactId") contactId: Long,
        @PathVariable("phoneId") phoneId: Long
    ) : ResponseEntity<String> {
        return contactService.deletePhone(contactId, phoneId)
    }

    @PutMapping("/{contactId}/phone/{phoneId}")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun changePhone(
        @PathVariable("contactId") contactId: Long,
        @PathVariable("phoneId") phoneId: Long,
        @RequestParam("phone") phone: String
    ) : ResponseEntity<String> {
        return contactService.changePhone(contactId, phoneId, phone)
    }


    @PutMapping("/{contactId}/name")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun changeName(
        @PathVariable("contactId") contactId: Long,
        @RequestParam("name") newName: String
    ) : ResponseEntity<String> {
        return contactService.changeName(contactId, newName)
    }

    @PutMapping("/{contactId}/surname")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun changeSurname(
        @PathVariable("contactId") contactId: Long,
        @RequestParam("surname") newSurname: String
    ) : ResponseEntity<String> {
        return contactService.changeSurname(contactId, newSurname)
    }

    @PutMapping("/{contactId}/ssnCode")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun changeSSNCode(
        @PathVariable("contactId") contactId: Long,
        @RequestParam("ssnCode") newSSNCode: String
    ) : ResponseEntity<String> {
        return contactService.changeSSNCode(contactId, newSSNCode)
    }

    @PutMapping("/{contactId}/category")
    @PreAuthorize("hasRole('ROLE_OPERATOR')")
    fun changeCategory(
        @PathVariable("contactId") contactId: Long,
        @RequestParam("category") newCategory: String
    ) : ResponseEntity<String> {
        return contactService.changeCategory(contactId, newCategory)
    }
}