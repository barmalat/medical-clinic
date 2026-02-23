package com.barmalat.medicalclinic.model.commands;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreatePatientCommand(
        @NotBlank(message = "email is mandatory") @Email(message = "invalid email format") String email,
        @NotBlank(message = "password is mandatory") String password, String idCardNo, String firstName,
        String lastName, String phoneNumber, String birthday) {
}