package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.exception.ErrorMessageDto;
import com.barmalat.medicalclinic.exception.ValidationErrorMessageDto;
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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @Operation(summary = "read all patients", description = "opcjonalny Request Param, np. /patients?page=0&size=3&sort=id")
    @GetMapping
    public Page<PatientDto> findAll(@ParameterObject Pageable pageable) {
        log.info("Received GET /patients request with pageable:{}", pageable);
        Page<PatientDto> result = patientService.findAll(pageable)
                .map(patientMapper::toDto);
        log.info("Returned response for GET /patients with page with total elements:{}", result.getTotalElements());
        return result;
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
        log.info("Received GET /patients/{} request with Path Variable email:{}", email, email);
        PatientDto result = patientMapper.toDto(patientService.findByEmail(email));
        log.info("Returned response for GET /patients/{} with body:{}", email, result);
        return result;
    }

    @Operation(summary = "create (add) patient by creating command")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))}),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorMessageDto.class))})
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto addPatient(@RequestBody @Valid CreatePatientCommand createPatientCommand) {
        log.info("Received POST /patients request with body:{}", createPatientCommand);
        PatientDto result = patientMapper.toDto(patientService.addPatient(createPatientCommand));
        log.info("Returned response for POST /patients with body:{}", result);
        return result;
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
        log.info("Received DELETE /patients/{} request with Path Variable email:{}", email, email);
        PatientDto result = patientMapper.toDto(patientService.deleteByEmail(email));
        log.info("Returned response for DELETE /patients/{} with body:{}", email, result);
        return result;
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
        log.info("Received PUT /patients/{} with Path Variable email:{} and body:{}", email, email, patientDto);
        PatientDto result = patientMapper.toDto(patientService.updateByEmail(email, patientDto));
        log.info("Returned response for PUT /patients/{} with body:{}", email, result);
        return result;
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
        log.info("Received PATCH /patients/{}/password with Path Variable email:{} and body:{}", email, email, changePasswordCommand);
        patientService.updatePasswordByEmail(email, changePasswordCommand);
        log.info("Returned response for PATCH /patients/{}/password without body (NO CONTENT)", email);
    }
}