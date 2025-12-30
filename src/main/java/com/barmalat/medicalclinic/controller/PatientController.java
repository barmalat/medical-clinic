package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.model.ChangePatientDataCommand;
import com.barmalat.medicalclinic.model.Patient;
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
import java.util.Optional;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<List<Patient>> findAll() {
        return ResponseEntity.ok().body(patientService.findAll());
    }

    @GetMapping("/{email}")
    public ResponseEntity<Patient> findPatientByEmail(@PathVariable String email) {
        Optional<Patient> patient = patientService.findPatientByEmail(email);
        if (patient.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().body(patient.get());
        }
    }

    @PostMapping
    public ResponseEntity<Patient> addPatient(@RequestBody Patient patient) {
        return ResponseEntity.status(201).body(patientService.addPatient(patient));
    }

    @DeleteMapping("/{email}")
    public Patient deletePatientByEmail(@PathVariable String email) {
        return patientService.deletePatientByEmail(email);
    }

    @PutMapping("/{email}")
    public Patient updatePatientByEmail(@PathVariable String email, @RequestBody Patient patient) {
        return patientService.upadatePatientByEmail(email, patient);
    }

    @PatchMapping("/changePassword/{email}")
    public String updatePasswordByEmail(@PathVariable String email, @RequestBody ChangePatientDataCommand changePasswordCommand) {
        return patientService.updatePasswordByEmail(email, changePasswordCommand);
    }
}