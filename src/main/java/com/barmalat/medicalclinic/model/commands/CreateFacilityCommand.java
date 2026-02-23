package com.barmalat.medicalclinic.model.commands;

import jakarta.validation.constraints.NotBlank;

public record CreateFacilityCommand(@NotBlank(message = "name is mandatory")String name, String city, String postalCode, String street, String streetNumber) {
}