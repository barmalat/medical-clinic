package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.mapper.FacilityMapper;
import com.barmalat.medicalclinic.model.commands.CreateFacilityCommand;
import com.barmalat.medicalclinic.model.dtos.FacilityDto;
import com.barmalat.medicalclinic.service.FacilityService;
import lombok.RequiredArgsConstructor;
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
public class FacilityController {
    private final FacilityService facilityService;
    private final FacilityMapper facilityMapper;

    @GetMapping
    public Page<FacilityDto> findAll(Pageable pageable) {
        return facilityService.findAll(pageable)
                .map(facilityMapper::toDto);
    }

    @GetMapping("/{facilityId}")
    public FacilityDto findById(@PathVariable Long facilityId) {
        return facilityMapper.toDto(facilityService.findById(facilityId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacilityDto addFacility(@RequestBody CreateFacilityCommand createFacilityCommand) {
        return facilityMapper.toDto(facilityService.addFacility(createFacilityCommand));
    }

    @DeleteMapping("/{facilityId}")
    public FacilityDto deleteById(@PathVariable Long facilityId) {
        return facilityMapper.toDto(facilityService.deleteById(facilityId));
    }

    @PutMapping("/{facilityId}")
    public FacilityDto updateById(@PathVariable Long facilityId, @RequestBody FacilityDto facilityDto) {
        return facilityMapper.toDto(facilityService.updateById(facilityId, facilityDto));
    }
}