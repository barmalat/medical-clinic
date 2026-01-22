package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.mapper.FacilityMapper;
import com.barmalat.medicalclinic.model.commands.CreateFacilityCommand;
import com.barmalat.medicalclinic.model.dtos.FacilityDto;
import com.barmalat.medicalclinic.service.FacilityService;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RestController
@RequestMapping("/facilities")
@RequiredArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;
    private final FacilityMapper facilityMapper;

    @GetMapping
    public List<FacilityDto> findAll() {
        return facilityService.findAll().stream()
                .map(facilityMapper::entityToDto)
                .toList();
    }

    @GetMapping("/{facilityId}")
    public FacilityDto findFacilityById(@PathVariable Long facilityId) {
        return facilityMapper.entityToDto(facilityService.findById(facilityId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacilityDto addFacility(@RequestBody CreateFacilityCommand createFacilityCommand) {
        return facilityMapper.entityToDto(facilityService.addFacility(createFacilityCommand));
    }

    @DeleteMapping("/{facilityId}")
    public FacilityDto deleteFacilityById(@PathVariable Long facilityId) {
        return facilityMapper.entityToDto(facilityService.deleteById(facilityId));
    }

    @PutMapping("/{facilityId}")
    public FacilityDto updateFacilityById(@PathVariable Long facilityId, @RequestBody FacilityDto facilityDto) {
        return facilityMapper.entityToDto(facilityService.updateById(facilityId, facilityDto));
    }
}