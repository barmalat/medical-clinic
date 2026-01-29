package com.barmalat.medicalclinic.model.commands;

public record CreateDoctorCommand(Long id, String email, String password, String specialization, String firstName,
                                  String lastName) {
}