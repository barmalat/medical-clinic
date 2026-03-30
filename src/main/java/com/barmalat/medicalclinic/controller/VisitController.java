package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.exception.ErrorMessageDto;
import com.barmalat.medicalclinic.exception.MedicalClinicException;
import com.barmalat.medicalclinic.mapper.VisitMapper;
import com.barmalat.medicalclinic.model.commands.CreateVisitCommand;
import com.barmalat.medicalclinic.model.dtos.VisitDto;
import com.barmalat.medicalclinic.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
@Tag(name = "/visits", description = "all end points from VisitController")
@Slf4j
public class VisitController {
    private final VisitService visitService;
    private final VisitMapper visitMapper;

    @Operation(summary = "read all visits", description = """
            Opcjonalny Request Param dot. paginacji, np. /visits?page=0&size=3&sort=id
            
            Opcjonalny Request Param dot. wyszukania wizyt dla konkretnego pacjenta, np. /visits?patientId=1
            
            Opcjonalny Request Param dot. wyszukania wizyt dla konkretnego doktora, np. /visits?doctorId=1
            
            Opcjonalny Request Param dot. wyszukania wizyt dla danej specjalizacji w danym przedziale czasu, np. /visits?specialization=chirurg&from=2026-04-01T00:00&to=2026-04-30T23:59
            """)
    @GetMapping
    public Page<VisitDto> find(@RequestParam(required = false) Long patientId,
                               @RequestParam(required = false) Long doctorId,
                               @RequestParam(required = false) String specialization,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                               @ParameterObject Pageable pageable) {
        log.info("Received GET /visits request with pageable:{}, and with RequestParam patientId:{}, doctorId:{}, specialization:{}, from:{}, to:{}",
                pageable, patientId, doctorId, specialization, from, to);
        Page<VisitDto> result = visitService.find(patientId, doctorId, specialization, from, to, pageable)
                .map(visitMapper::toDto);
        log.info("Returned response for GET /visits with page with total elements:{}", result.getTotalElements());
        return result;
    }

    @Operation(summary = "create (add) visit by creating command")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "visit created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = VisitDto.class))}),
            @ApiResponse(responseCode = "404", description = "doctor not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))}),
            @ApiResponse(responseCode = "400", description = """
                    start time is not before end time
                    
                    or
                    
                    start time or/and end time is/are not at full quarter of minutes
                    
                    or
                    
                    start time is before now
                    """,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))}),
            @ApiResponse(responseCode = "409", description = "visit you tried to created is in conflict with existing visit",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))})
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitDto addVisit(@RequestBody @Valid CreateVisitCommand createVisitCommand) {
        log.info("Received POST /visits request with body:{}", createVisitCommand);
        VisitDto result = visitMapper.toDto(visitService.addVisit(createVisitCommand));
        log.info("Returned response for POST /visits with body:{}", result);
        return result;
    }

    @Operation(summary = "update visit with patient by visitId and patientId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "visit is updated with patient",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = VisitDto.class))}),
            @ApiResponse(responseCode = "404", description = """
                    visit not found
                    
                    or
                    
                    patient not found
                    """,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))}),
            @ApiResponse(responseCode = "400", description = "start time is before now ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))}),
            @ApiResponse(responseCode = "409", description = "visit you tried to updated is not available, it is already booked",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))})
    })
    @PatchMapping("/{visitId}/patient/{patientId}")
    public VisitDto addPatientToVisit(@PathVariable Long visitId, @PathVariable Long patientId) {
        log.info("Received PATCH /visits/{}/patient/{} request with Path Variable visitId:{} and patientId:{}", visitId, patientId, visitId, patientId);
        VisitDto result = visitMapper.toDto(visitService.addPatientToVisit(visitId, patientId));
        log.info("Returned response for PATCH /visits/{}/patient/{} with body:{}", visitId, patientId, result.toString());
        return result;
    }

    @Operation(summary = "get available visits for a specific doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "available visits returned",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VisitDto.class))}),
            @ApiResponse(responseCode = "404", description = "doctor not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDto.class))})
    })
    @GetMapping("/doctor/{doctorId}/available")
    public Page<VisitDto> findAvailableByDoctorId(@PathVariable Long doctorId, @ParameterObject Pageable pageable) {
        log.info("Received GET /visits/doctor/{}/available", doctorId);
        Page<VisitDto> result = visitService.findAvailableByDoctorId(doctorId, pageable)
                .map(visitMapper::toDto);
        log.info("Returned response for GET /visits/doctor/{}/available with total elements:{}", doctorId, result.getTotalElements());
        return result;
    }

    @Operation(summary = "get available visits for a given day and specialization (P-001)",
            description = """
            W zależności od oczekiwań, wymagane zachowanie jednego z trzech schematów request param:
            
            1. jeśli chcemy wyswietlić dostępne wizyty dla danej specjalizacji w konkretnym dniu: /visits/available?date=2026-04-01&specialization=chirurg
            
            2. jeśli chcemy wyświetlić dostępne wizyty dla danej specjalizacji w danym przedziale czasu: /visits/available?specialization=chirurg&from=2026-04-01T00:00&to=2026-04-30T23:59
            
            3. jeśli chcemy wyświetlić dostępne wizyty bez danej specjalizacji w danym przedziale czasu: /visits/available?from=2026-04-01T00:00&to=2026-04-30T23:59
            
            Opcjonalny Request Param dot. paginacji, np. /visits/available?page=0&size=3&sort=id
            """)
    @GetMapping("/available")
    public Page<VisitDto> findAvailable(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @ParameterObject Pageable pageable) {
        log.info("Received GET /visits/available with date:{}, specialization:{}, from:{}, to:{}", date, specialization, from, to);
        Page<VisitDto> result;
        if (date != null && specialization != null) {
            result = visitService.findAvailableByDateAndSpecialization(date, specialization, pageable)
                    .map(visitMapper::toDto);
        } else if (from != null && to != null) {
            result = visitService.findAvailableByTimeRangeAndSpecialization(from, to, specialization, pageable)
                    .map(visitMapper::toDto);
        } else {
            throw new MedicalClinicException("Podaj (date + specialization) lub (specialization + from + to).", HttpStatus.BAD_REQUEST);
        }
        log.info("Returned response for GET /visits/available with total elements:{}", result.getTotalElements());
        return result;
    }

    @Operation(summary = "cancel a visit by doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "visit cancelled",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VisitDto.class))}),
            @ApiResponse(responseCode = "404", description = "visit not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDto.class))}),
            @ApiResponse(responseCode = "409", description = "visit is already cancelled",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDto.class))})
    })
    @PatchMapping("/{visitId}/cancel")
    public VisitDto cancelVisit(@PathVariable Long visitId) {
        log.info("Received PATCH /visits/{}/cancel", visitId);
        VisitDto result = visitMapper.toDto(visitService.cancelVisit(visitId));
        log.info("Returned PATCH /visits/{}/cancel with body:{}", visitId, result);
        return result;
    }
}