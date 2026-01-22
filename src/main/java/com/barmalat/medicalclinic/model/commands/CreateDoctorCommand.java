package com.barmalat.medicalclinic.model.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateDoctorCommand {
    private final Long id;
    private final String email;
    private final String password;
    private final String specialization;
    private final String firstName;
    private final String lastName;
}