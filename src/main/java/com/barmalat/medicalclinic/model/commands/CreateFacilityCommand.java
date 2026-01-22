package com.barmalat.medicalclinic.model.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateFacilityCommand {
    private final String name;
    private final String city;
    private final String postalCode;
    private final String street;
    private final String streetNumber;
}