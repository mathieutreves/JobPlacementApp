package com.example.crm_service.services.contact

import com.example.crm_service.dtos.contact.ContactDTO
import org.springframework.http.ResponseEntity

interface ContactService {
    fun getAllContacts(email: String?, address: String?, phone: String?, name: String?, surname: String?, page: Int, size: Int) : ResponseEntity<List<ContactDTO>>
    fun getContactById(contactId: Long) : ResponseEntity<ContactDTO>
    fun createContact(name: String, surname: String, ssnCode: String?, category: String?, email: String?, address: String?, telephone: String?) : ResponseEntity<String>

    fun addEmail(contactId: Long, newEmail: String) : ResponseEntity<String>
    fun deleteEmail(contactId: Long, emailId: Long) : ResponseEntity<String>
    fun changeEmail(contactId: Long, emailId: Long, newEmail: String) : ResponseEntity<String>

    fun addAddress(contactId: Long, newAddress: String) : ResponseEntity<String>
    fun deleteAddress(contactId: Long, addressId: Long) : ResponseEntity<String>
    fun changeAddress(contactId: Long, addressId: Long, newAddress: String) : ResponseEntity<String>

    fun addPhone(contactId: Long, newPhone: String) : ResponseEntity<String>
    fun deletePhone(contactId: Long, phoneId: Long) : ResponseEntity<String>
    fun changePhone(contactId: Long, phoneId: Long, newPhone: String) : ResponseEntity<String>

    fun changeName(contactId: Long, newName: String) : ResponseEntity<String>
    fun changeSurname(contactId: Long, newSurname: String) : ResponseEntity<String>
    fun changeSSNCode(contactId: Long, newSSNCode: String) : ResponseEntity<String>
    fun changeCategory(contactId: Long, newCategory: String) : ResponseEntity<String>
}