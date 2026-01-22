package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.mapper.PatientMapper;
import com.barmalat.medicalclinic.model.commands.ChangePatientDataCommand;
import com.barmalat.medicalclinic.model.commands.CreatePatientCommand;
import com.barmalat.medicalclinic.model.dtos.PatientDto;
import com.barmalat.medicalclinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @GetMapping
    public ResponseEntity<List<PatientDto>> findAll() {
        return ResponseEntity.ok().body(patientService.findAll().stream()
                .map(patientMapper::entityToDto)
                .toList());
    }

    @GetMapping("/{email}")
    public PatientDto findPatientByEmail(@PathVariable String email) {
        return patientMapper.entityToDto(patientService.findPatientByEmail(email));
    }

    @PostMapping
    public ResponseEntity<PatientDto> addPatient(@RequestBody CreatePatientCommand createPatientCommand) {
        return ResponseEntity.status(201).body(patientMapper.entityToDto(patientService.addPatient(createPatientCommand)));
    }

    @DeleteMapping("/{email}")
    public PatientDto deletePatientByEmail(@PathVariable String email) {
        return patientMapper.entityToDto(patientService.deletePatientByEmail(email));
    }

    @PutMapping("/{email}")
    public PatientDto updatePatientByEmail(@PathVariable String email, @RequestBody PatientDto patientDto) {
        return patientMapper.entityToDto(patientService.updatePatientByEmail(email, patientDto));
    }

    @PatchMapping("/{email}/password")
    public void updatePasswordByEmail(@PathVariable String email, @RequestBody ChangePatientDataCommand changePasswordCommand) {
        patientService.updatePasswordByEmail(email, changePasswordCommand);
    }
}