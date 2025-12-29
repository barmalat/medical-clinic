package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.model.Patient;
import com.barmalat.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public Optional<Patient> findPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient addPatient(Patient patient) {
        return patientRepository.addPatient(patient);
    }

    public Patient deletePatientByEmail(String email) {
        return patientRepository.deletePatientByEmail(email);
    }

    public Patient upadatePatientByEmail(String email, Patient patient) {
        return patientRepository.updatePatientByEmail(email, patient);
    }
}
