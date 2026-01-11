package com.barmalat.medicalclinic.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MedicalClinicExceptionHandler {
    @ExceptionHandler(MedicalClinicException.class)
    public ResponseEntity<MedicalClinicException> handleException(MedicalClinicException e){
        return ResponseEntity.status(e.getStatus()).body(new MedicalClinicException(e.getMessage(), e.getStatus()));
    }
}