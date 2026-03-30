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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "/doctors", description = "all end points from DoctorController")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;

    @Operation(summary = "read all doctors", description = """
            Opcjonalny RequestParam dot. paginacji, np. /doctors?page=0&size=3&sort=id
            
            Opcjonalny RequestParam dot. filtrowania po specjalizacji, np. /doctors?specialization=chirurg
            """)
    @GetMapping
    public Page<DoctorDto> findAll(@RequestParam(required = false) String specialization,
                                   @ParameterObject Pageable pageable) {
        log.info("Received GET /doctors request with pageable:{} and specialization:{}", pageable, specialization);
        Page<DoctorDto> result = doctorService.findAll(specialization, pageable)
                .map(doctorMapper::toDto);
        log.info("Returned response for GET /doctors with page with total elements:{}", result.getTotalElements());
        return result;
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
        log.info("Received GET /doctors/{} request with Path Variable doctorId:{}", doctorId, doctorId);
        DoctorDto result = doctorMapper.toDto(doctorService.findById(doctorId));
        log.info("Returned response for GET /doctors/{} with body:{}", doctorId, result);
        return result;
    }


    @Operation(summary = "create (add) doctor by creating command")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto addDoctor(@RequestBody @Valid CreateDoctorCommand createDoctorCommand) {
        log.info("Received POST /doctors request with body:{}", createDoctorCommand);
        DoctorDto result = doctorMapper.toDto(doctorService.addDoctor(createDoctorCommand));
        log.info("Returned response for POST /doctors with body:{}", result);
        return result;
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
        log.info("Received DELETE /doctors/{} request with Path Variable doctorId:{}", doctorId, doctorId);
        DoctorDto result = doctorMapper.toDto(doctorService.deleteById(doctorId));
        log.info("Returned response for DELETE /doctors/{} with body:{}", doctorId, result);
        return result;
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
    public DoctorDto updateById(@PathVariable Long doctorId, @RequestBody @Valid DoctorDto doctorDto) {
        log.info("Received PUT /doctors/{} request with Path Variable doctorId:{} and body:{}", doctorId, doctorId, doctorDto);
        DoctorDto result = doctorMapper.toDto(doctorService.updateById(doctorId, doctorDto));
        log.info("Returned response for PUT /doctors/{} with body:{}", doctorId, result);
        return result;
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
        log.info("Received PATCH /doctors/{}/facility/{} request with Path Variable doctorId:{} and facilityId:{}", doctorId, facilityId, doctorId, facilityId);
        DoctorDto result = doctorMapper.toDto(doctorService.addFacilityById(doctorId, facilityId));
        log.info("Returned response for PATCH /doctors/{}/facility/{} with body:{}", doctorId, facilityId, result);
        return result;
    }
}