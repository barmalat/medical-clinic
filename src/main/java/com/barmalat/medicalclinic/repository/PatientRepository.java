package com.barmalat.medicalclinic.repository;

import com.barmalat.medicalclinic.model.ChangePatientDataCommand;
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

    public Optional<Patient> deletePatientByEmail(String email) {
        Optional<Patient> patient = findByEmail(email);
        patient.ifPresent(patients::remove);
        return patient;
    }

    public Optional<Patient> updatePatientByEmail(String email, Patient updatePatient) {
        Optional<Patient> patient = findByEmail(email);
        patient.ifPresent(p -> p.updatePatientPublicData(updatePatient));
        return patient;
    }

    public Optional<Patient> updatePasswordByEmail(String email, ChangePatientDataCommand changePasswordCommand) {
        Optional<Patient> patient = findByEmail(email);
        patient.ifPresent(p -> p.setPassword(changePasswordCommand.getPassword()));
        return patient;
    }
}