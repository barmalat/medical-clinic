package com.barmalat.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public record ErrorMessageDto(String message, HttpStatus status) {
}