package com.example.crm_service.repositories.contact

import com.example.crm_service.entities.contact.Address
import com.example.crm_service.entities.contact.Contact
import com.example.crm_service.entities.contact.Email
import com.example.crm_service.entities.contact.Phone
import jakarta.persistence.criteria.JoinType
import org.springframework.data.jpa.domain.Specification

class ContactSpecifications {
    companion object {
        fun withEmail(email: String?): Specification<Contact>? {
            return email?.let {
                Specification<Contact> { root, _, cb ->
                    val join = root.join<Contact, Email>("emails", JoinType.INNER)
                    cb.like(join.get("email"), "%$it%")
                }
            }
        }

        fun withAddress(fullAddress: String?): Specification<Contact>? {
            return fullAddress?.let { addressString ->
                Specification<Contact> { root, query, cb ->
                    // Ensure distinct results
                    query?.distinct(true)

                    // Split the full address into its components
                    val addressParts = addressString.split(',').map(String::trim).takeIf { it.size == 5 }
                    addressParts?.let { parts ->
                        // Join Contact with Address entity
                        val join = root.join<Contact, Address>("addresses", JoinType.INNER)

                        // Create predicates for each part of the address
                        val streetPredicate = cb.equal(cb.lower(join.get("street")), parts[0].lowercase())
                        val numberPredicate = cb.equal(cb.lower(join.get("number")), parts[1].lowercase())
                        val cityPredicate = cb.equal(cb.lower(join.get("city")), parts[2].lowercase())
                        val postalCodePredicate = cb.equal(cb.lower(join.get("postalCode")), parts[3].lowercase())
                        val countryPredicate = cb.equal(cb.lower(join.get("country")), parts[4].lowercase())

                        // Combine predicates with AND condition
                        cb.and(streetPredicate, numberPredicate, cityPredicate, postalCodePredicate, countryPredicate)
                    } ?: throw IllegalArgumentException("Address string must be in the format 'street, number, city, postalCode, country'")
                }
            }
        }

        fun withPhone(phone: String?): Specification<Contact>? {
            return phone?.let {
                Specification<Contact> { root, _, cb ->
                    val join = root.join<Contact, Phone>("phoneNumbers", JoinType.INNER)
                    cb.like(join.get("number"), "%$it%")
                }
            }
        }

        fun withName(name: String?): Specification<Contact>? {
            return name?.let { searchTerm ->
                Specification { root, _, cb ->
                    cb.like(cb.lower(root.get("name")), "%${searchTerm.lowercase()}%")
                }
            }
        }

        fun withSurname(surname: String?): Specification<Contact>? {
            return surname?.let { searchTerm ->
                Specification { root, _, cb ->
                    cb.like(cb.lower(root.get("surname")), "%${searchTerm.lowercase()}%")
                }
            }
        }
    }
}