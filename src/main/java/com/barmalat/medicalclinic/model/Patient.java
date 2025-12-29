package com.barmalat.medicalclinic.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Patient {
    private String email;
    private String password;
    private String idCardNo;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String birthday;

    public void updateAll(Patient patient){
        email = patient.email;
        password = patient.password;
        idCardNo = patient.idCardNo;
        firstName = patient.firstName;
        lastName = patient.lastName;
        phoneNumber = patient.phoneNumber;
        birthday = patient.birthday;
    }
}