package com.barmalat.medicalclinic.repository;

import com.barmalat.medicalclinic.model.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PatientRepository {
    private final List<Patient> patients;

    public List<Patient> findAll() {
        return new ArrayList<>(patients);
    }

    public Optional<Patient> findByEmail(String email) {
        return patients.stream()
                .filter(patient -> patient.getEmail().equals(email))
                .findFirst();
    }

    public Patient addPatient(Patient patient) {
        patients.add(patient);
        return patient;
    }

    public Patient deletePatientByEmail(String email) {
        Optional<Patient> patient = findByEmail(email);
        patients.remove(patient.get());
        return patient.get();
    }

    public Patient updatePatientByEmail(String email, Patient updatePatient) {
        Patient patient = findByEmail(email).get();
        patient.updateAll(updatePatient);
        return patient;
    }

    public String updatePasswordByEmail(String email, String newPassword) {
        Patient patient = findByEmail(email).get();
        patient.setPassword(newPassword);
        return "Zmieniono hasło na:" + newPassword;
    }
}