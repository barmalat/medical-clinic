package com.barmalat.medicalclinic.exception;

import java.util.List;
import java.util.Map;

public record ValidationErrorMessageDto(String message, int status, Map<String, List<String>> errors) {
}