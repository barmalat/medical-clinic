package com.barmalat.medicalclinic.model.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record DoctorDto(Long id,
                        @NotBlank(message = "email is mandatory") @Email(message = "invalid email format") String email,
                        String specialization, String firstName, String lastName, List<FacilityDto> facilities) {
}