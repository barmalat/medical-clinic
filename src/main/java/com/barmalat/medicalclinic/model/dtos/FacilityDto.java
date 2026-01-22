package com.barmalat.medicalclinic.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FacilityDto {
    private final Long id;
    private final String name;
    private final String city;
    private final String postalCode;
    private final String street;
    private final String streetNumber;
}