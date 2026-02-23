package com.barmalat.medicalclinic.model.commands;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateDoctorCommand(Long id,
                                  @NotBlank(message = "email is mandatory") @Email(message = "invalid email format") String email,
                                  @NotBlank(message = "password is mandatory") String password, String specialization,
                                  String firstName,
                                  String lastName) {
}