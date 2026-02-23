package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.exception.ErrorMessageDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
            """)
    @GetMapping
    public Page<VisitDto> find(@RequestParam(required = false) Long patientId, @ParameterObject Pageable pageable) {
        log.info("Received GET /visits request with pageable:{}, and with RequestParam patientId:{}", pageable, patientId);
        Page<VisitDto> result = visitService.find(patientId, pageable)
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
}