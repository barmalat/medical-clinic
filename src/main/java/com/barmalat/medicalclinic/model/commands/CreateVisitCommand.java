package com.barmalat.medicalclinic.model.commands;

import java.time.LocalDateTime;

public record CreateVisitCommand(Long id, Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
}