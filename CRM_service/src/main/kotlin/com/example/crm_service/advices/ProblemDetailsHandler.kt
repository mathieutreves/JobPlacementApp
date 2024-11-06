package com.example.crm_service.advices

import com.example.crm_service.exceptions.contact.*
import com.example.crm_service.exceptions.customer.CustomerNotFoundException
import com.example.crm_service.exceptions.jobOffer.*
import com.example.crm_service.exceptions.message.*
import com.example.crm_service.exceptions.professional.*
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ProblemDetailsHandler : ResponseEntityExceptionHandler() {

    // CONTACT EXCEPTIONS
    @ExceptionHandler(ContactNotFoundException::class)
    fun handleContactNotFound(e: ContactNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(EmailNotFoundException::class)
    fun handleEmailNotFound(e: EmailNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(AddressNotFoundException::class)
    fun handleAddressNotFound(e: AddressNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(PhoneNotFoundException::class)
    fun handlePhoneNotFound(e: PhoneNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(AddressWrongFormatException::class)
    fun handleAddressNotFound(e: AddressWrongFormatException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)


    // MESSAGE EXCEPTIONS
    @ExceptionHandler(MessageNotFoundException::class)
    fun handleMessageNotFound(e: MessageNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(MessageStateInvalidTransitionException::class)
    fun handleMessageStateInvalidTransition(e: MessageStateInvalidTransitionException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)


    // PROFESSIONAL EXCEPTIONS
    @ExceptionHandler(ProfessionalNotFoundException::class)
    fun handleProfessionalNotFound(e: ProfessionalNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(SkillNotFoundException::class)
    fun handleSkillNotFound(e: SkillNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(NoteNotFoundException::class)
    fun handleNoteNotFound(e: NoteNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)


    // JOB OFFER EXCEPTIONS
    @ExceptionHandler(JobOfferNotFoundException::class)
    fun handleJobOfferNotFound(e: JobOfferNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(JobOfferStateInvalidTransitionException::class)
    fun handleJobOfferSkillNotFound(e: JobOfferStateInvalidTransitionException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)


    // CUSTOMER
    @ExceptionHandler(CustomerNotFoundException::class)
    fun handleCustomerNotFound(e: CustomerNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)
}