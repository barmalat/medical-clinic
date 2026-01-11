package com.barmalat.medicalclinic.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PatientDto {
    private final String email;
    private final String idCardNo;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String birthday;
}