package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.exception.ErrorMessageDto;
import com.barmalat.medicalclinic.mapper.FacilityMapper;
import com.barmalat.medicalclinic.model.commands.CreateFacilityCommand;
import com.barmalat.medicalclinic.model.dtos.FacilityDto;
import com.barmalat.medicalclinic.service.FacilityService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/facilities")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "/facilities", description = "all end points from FacilityController")
public class FacilityController {
    private final FacilityService facilityService;
    private final FacilityMapper facilityMapper;

    @Operation(summary = "read all facilities", description = "opcjonalny Request Param, np. /facilities?page=0&size=3&sort=id")
    @GetMapping
    public Page<FacilityDto> findAll(@ParameterObject Pageable pageable) {
        log.info("Received GET /facilities request with pageable:{}", pageable);
        Page<FacilityDto> result = facilityService.findAll(pageable)
                .map(facilityMapper::toDto);
        log.info("Returned response for GET /facilities with page with total elements:{}", result.getTotalElements());
        return result;
    }

    @Operation(summary = "read (find) facility by facility.id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FacilityDto.class))}),
            @ApiResponse(responseCode = "404", description = "Facility not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))})})
    @GetMapping("/{facilityId}")
    public FacilityDto findById(@PathVariable Long facilityId) {
        log.info("Received GET /facilities/{} request with Path Variable facilityId:{}", facilityId, facilityId);
        FacilityDto result = facilityMapper.toDto(facilityService.findById(facilityId));
        log.info("Returned response for GET /facilities/{} with body:{}", facilityId, result);
        return result;
    }

    @Operation(summary = "create (add) facility by creating command")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacilityDto addFacility(@RequestBody @Valid CreateFacilityCommand createFacilityCommand) {
        log.info("Received POST /facilities request with body:{}", createFacilityCommand);
        FacilityDto result = facilityMapper.toDto(facilityService.addFacility(createFacilityCommand));
        log.info("Returned response for POST /facilities with body:{}", result);
        return result;
    }

    @Operation(summary = "delete facility by facility.id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility found and deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FacilityDto.class))}),
            @ApiResponse(responseCode = "404", description = "Facility not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))})})
    @DeleteMapping("/{facilityId}")
    public FacilityDto deleteById(@PathVariable Long facilityId) {
        log.info("Received DELETE /facilities/{} request with Path Variable facilityId:{}", facilityId, facilityId);
        FacilityDto result = facilityMapper.toDto(facilityService.deleteById(facilityId));
        log.info("Returned response for DELETE /facilities/{} with body:{}", facilityId, result);
        return result;
    }

    @Operation(summary = "update facility by facility.id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility found and updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FacilityDto.class))}),
            @ApiResponse(responseCode = "404", description = "Facility not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))})})
    @PutMapping("/{facilityId}")
    public FacilityDto updateById(@PathVariable Long facilityId, @RequestBody @Valid FacilityDto facilityDto) {
        log.info("Received PUT /facilities/{} request with Path Variable doctorId:{} and body:{}", facilityId, facilityId, facilityDto);
        FacilityDto result = facilityMapper.toDto(facilityService.updateById(facilityId, facilityDto));
        log.info("Returned response for PUT /facilities/{} with body:{}", facilityId, result);
        return result;
    }
}