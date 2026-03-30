package com.barmalat.medicalclinic.model.dtos;

import com.barmalat.medicalclinic.model.entities.VisitStatus;

import java.time.LocalDateTime;

public record VisitDto(Long id, DoctorDto doctor, PatientDto patient, LocalDateTime startTime, LocalDateTime endTime, VisitStatus status) {
}