package com.barmalat.medicalclinic.model.commands;

import jakarta.validation.constraints.NotBlank;

public record ChangePatientDataCommand(@NotBlank(message = "password is mandatory") String password) {
}