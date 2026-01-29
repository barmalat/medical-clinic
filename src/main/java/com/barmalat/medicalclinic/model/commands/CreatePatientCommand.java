package com.barmalat.medicalclinic.model.commands;

public record CreatePatientCommand(String email, String password, String idCardNo, String firstName, String lastName,
                                   String phoneNumber, String birthday) {
}