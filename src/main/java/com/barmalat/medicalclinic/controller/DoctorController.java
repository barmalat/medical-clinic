package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.mapper.DoctorMapper;
import com.barmalat.medicalclinic.model.commands.CreateDoctorCommand;
import com.barmalat.medicalclinic.model.dtos.DoctorDto;
import com.barmalat.medicalclinic.service.DoctorService;
import lombok.RequiredArgsConstructor;
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
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;

    @GetMapping
    public Page<DoctorDto> findAll(Pageable pageable) {
        return doctorService.findAll(pageable)
                .map(doctorMapper::toDto);
    }

    @GetMapping("/{doctorId}")
    public DoctorDto findById(@PathVariable Long doctorId) {
        return doctorMapper.toDto(doctorService.findById(doctorId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto addDoctor(@RequestBody CreateDoctorCommand createDoctorCommand) {
        return doctorMapper.toDto(doctorService.addDoctor(createDoctorCommand));
    }

    @DeleteMapping("/{doctorId}")
    public DoctorDto deleteById(@PathVariable Long doctorId) {
        return doctorMapper.toDto(doctorService.deleteById(doctorId));
    }

    @PutMapping("/{doctorId}")
    public DoctorDto updateById(@PathVariable Long doctorId, @RequestBody DoctorDto doctorDto) {
        return doctorMapper.toDto(doctorService.updateById(doctorId, doctorDto));
    }

    @PatchMapping("/{doctorId}/facility/{facilityId}")
    public DoctorDto addFacilityById(@PathVariable Long doctorId, @PathVariable Long facilityId) {
        return doctorMapper.toDto(doctorService.addFacilityById(doctorId, facilityId));
    }
}