package com.barmalat.medicalclinic.model.dtos;

import jakarta.validation.constraints.NotBlank;

public record PatientDto(Long id, @NotBlank(message = "email is mandatory") String email, String idCardNo,
                         String firstName, String lastName, String phoneNumber,
                         String birthday) {
}