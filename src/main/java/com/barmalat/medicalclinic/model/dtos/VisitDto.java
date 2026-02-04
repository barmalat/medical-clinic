package com.barmalat.medicalclinic.model.dtos;

import java.time.LocalDateTime;

public record VisitDto(Long id, DoctorDto doctor, PatientDto patient, LocalDateTime startTime, LocalDateTime endTime) {
}