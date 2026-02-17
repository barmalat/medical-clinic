package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.exception.ErrorMessageDto;
import com.barmalat.medicalclinic.mapper.PatientMapper;
import com.barmalat.medicalclinic.model.commands.ChangePatientDataCommand;
import com.barmalat.medicalclinic.model.commands.CreatePatientCommand;
import com.barmalat.medicalclinic.model.dtos.PatientDto;
import com.barmalat.medicalclinic.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Tag(name = "/patients", description = "all end points from PatientController")
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @Operation(summary = "read all patients", description = "opcjonalny Request Param, np. /patients?page=0&size=3&sort=id")
    @GetMapping
    public Page<PatientDto> findAll(@ParameterObject Pageable pageable) {
        return patientService.findAll(pageable)
                .map(patientMapper::toDto);
    }

    @Operation(summary = "read (find) patient by patient.email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))}),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))})})
    @GetMapping("/{email}")
    public PatientDto findByEmail(@PathVariable String email) {
        return patientMapper.toDto(patientService.findByEmail(email));
    }

    @Operation(summary = "create (add) patient by creating command")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto addPatient(@RequestBody @Valid CreatePatientCommand createPatientCommand) {
        return patientMapper.toDto(patientService.addPatient(createPatientCommand));
    }

    @Operation(summary = "delete patient by patient.email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found and deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))}),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))})})
    @DeleteMapping("/{email}")
    public PatientDto deleteByEmail(@PathVariable String email) {
        return patientMapper.toDto(patientService.deleteByEmail(email));
    }

    @Operation(summary = "update public data of patient by patient.email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found and public data updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))}),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))})})
    @PutMapping("/{email}")
    public PatientDto updateByEmail(@PathVariable String email, @RequestBody @Valid PatientDto patientDto) {
        return patientMapper.toDto(patientService.updateByEmail(email, patientDto));
    }

    @Operation(summary = "update password of patient by patient.mail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient found and password updated"),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))})})
    @PatchMapping("/{email}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePasswordByEmail(@PathVariable String email, @RequestBody @Valid ChangePatientDataCommand changePasswordCommand) {
        patientService.updatePasswordByEmail(email, changePasswordCommand);
    }
}