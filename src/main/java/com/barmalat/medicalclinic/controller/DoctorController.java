package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.mapper.DoctorMapper;
import com.barmalat.medicalclinic.model.commands.CreateDoctorCommand;
import com.barmalat.medicalclinic.model.dtos.DoctorDto;
import com.barmalat.medicalclinic.service.DoctorService;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;

    @GetMapping
    public List<DoctorDto> findAll() {
        return doctorService.findAll().stream()
                .map(doctorMapper::entityToDto)
                .toList();
    }

    @GetMapping("/{doctorId}")
    public DoctorDto findDoctorById(@PathVariable Long doctorId) {
        return doctorMapper.entityToDto(doctorService.findById(doctorId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto addDoctor(@RequestBody CreateDoctorCommand createDoctorCommand) {
        return doctorMapper.entityToDto(doctorService.addDoctor(createDoctorCommand));
    }

    @DeleteMapping("/{doctorId}")
    public DoctorDto deleteDoctorById(@PathVariable Long doctorId) {
        return doctorMapper.entityToDto(doctorService.deleteById(doctorId));
    }

    @PutMapping("/{doctorId}")
    public DoctorDto updateDoctorById(@PathVariable Long doctorId, @RequestBody DoctorDto doctorDto) {
        return doctorMapper.entityToDto(doctorService.updateById(doctorId, doctorDto));
    }

    @PatchMapping("/{doctorId}/facility/{facilityId}")
    public DoctorDto addFacilityByIdToDoctor(@PathVariable Long doctorId, @PathVariable Long facilityId) {
        return doctorMapper.entityToDto(doctorService.addFacilityById(doctorId, facilityId));
    }
}