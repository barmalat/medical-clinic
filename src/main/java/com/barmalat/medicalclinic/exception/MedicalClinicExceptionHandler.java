package com.barmalat.medicalclinic.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MedicalClinicExceptionHandler {
    @ExceptionHandler(MedicalClinicException.class)
    public ResponseEntity<ErrorMessageDto> handleException(MedicalClinicException e){
        return ResponseEntity.status(e.getStatus()).body(new ErrorMessageDto(e.getMessage(), e.getStatus()));
    }
}