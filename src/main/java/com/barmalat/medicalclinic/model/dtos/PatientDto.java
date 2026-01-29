package com.barmalat.medicalclinic.model.dtos;

public record PatientDto(Long id, String email, String idCardNo, String firstName, String lastName, String phoneNumber,
                         String birthday) {
}