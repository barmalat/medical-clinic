package com.barmalat.medicalclinic.model.dtos;

import jakarta.validation.constraints.NotBlank;

public record FacilityDto(Long id, @NotBlank(message = "name is mandatory") String name, String city, String postalCode,
                          String street, String streetNumber) {
}