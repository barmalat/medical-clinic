package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.exception.ErrorMessageDto;
import com.barmalat.medicalclinic.mapper.DoctorMapper;
import com.barmalat.medicalclinic.model.commands.CreateDoctorCommand;
import com.barmalat.medicalclinic.model.dtos.DoctorDto;
import com.barmalat.medicalclinic.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/doctors")
@RequiredArgsConstructor
@Tag(name = "/doctors", description = "all end points from DoctorController")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;

    @Operation(summary = "read all doctors", description = "opcjonalny RequestParam, np. /doctors?page=0&size=3&sort=id")
    @GetMapping
    public Page<DoctorDto> findAll(@ParameterObject Pageable pageable) {
        return doctorService.findAll(pageable)
                .map(doctorMapper::toDto);
    }

    @Operation(summary = "read (find) doctor by doctor.id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDto.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))})})
    @GetMapping("/{doctorId}")
    public DoctorDto findById(@PathVariable Long doctorId) {
        return doctorMapper.toDto(doctorService.findById(doctorId));
    }


    @Operation(summary = "create (add) doctor by creating command")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto addDoctor(@RequestBody CreateDoctorCommand createDoctorCommand) {
        return doctorMapper.toDto(doctorService.addDoctor(createDoctorCommand));
    }

    @Operation(summary = "delete doctor by doctor.id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found and deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDto.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))})})
    @DeleteMapping("/{doctorId}")
    public DoctorDto deleteById(@PathVariable Long doctorId) {
        return doctorMapper.toDto(doctorService.deleteById(doctorId));
    }

    @Operation(summary = "update public data of doctor by doctor.id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found and public data updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDto.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))})})
    @PutMapping("/{doctorId}")
    public DoctorDto updateById(@PathVariable Long doctorId, @RequestBody DoctorDto doctorDto) {
        return doctorMapper.toDto(doctorService.updateById(doctorId, doctorDto));
    }

    @Operation(summary = "update doctor facilities with new facility by doctor.id and facility.id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found and public data updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDto.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))}),
            @ApiResponse(responseCode = "404", description = "Facility not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))})})
    @PatchMapping("/{doctorId}/facility/{facilityId}")
    public DoctorDto addFacilityById(@PathVariable Long doctorId, @PathVariable Long facilityId) {
        return doctorMapper.toDto(doctorService.addFacilityById(doctorId, facilityId));
    }
}