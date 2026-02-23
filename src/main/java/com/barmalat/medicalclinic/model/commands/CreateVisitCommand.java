package com.barmalat.medicalclinic.model.commands;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateVisitCommand(Long id, @NotNull(message = "doctorId is mandatory") Long doctorId,
                                 LocalDateTime startTime, LocalDateTime endTime) {
}