package com.barmalat.medicalclinic.model.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreatePatientCommand {
    private final String email;
    private final String password;
    private final String idCardNo;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String birthday;
}