package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.model.Patient;
import com.barmalat.medicalclinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<Patient> addPatient(Patient patient) {
        return ResponseEntity.status(201).body(patientService.addPatient(patient));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Patient> deletePatientByEmail(@PathVariable String email) {
        return ResponseEntity.ok().body(patientService.deletePatientByEmail(email));
    }

    @PutMapping("/{email}")
    public ResponseEntity<Patient> updatePatientByEmail(@PathVariable String email, Patient patient) {
        return ResponseEntity.ok().body(patientService.upadatePatientByEmail(email, patient));
    }
}