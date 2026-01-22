package com.barmalat.medicalclinic.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class DoctorDto {
    private final Long id;
    private final String email;
    private final String specialization;
    private final String firstName;
    private final String lastName;
    private final List<FacilityDto> facilities;
}