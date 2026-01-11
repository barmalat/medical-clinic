package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.exception.PatientNotFoundException;
import com.barmalat.medicalclinic.model.ChangePatientDataCommand;
import com.barmalat.medicalclinic.model.Patient;
import com.barmalat.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient findPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Nie znaleziono pacjenta o wskazanym adresie email."));
    }

    public Patient addPatient(Patient patient) {
        return patientRepository.addPatient(patient);
    }

    public Patient deletePatientByEmail(String email) {
        return patientRepository.deletePatientByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Nie znaleziono pacjenta o wskazanym adresie email."));
    }

    public Patient updatePatientByEmail(String email, Patient patient) {
        return patientRepository.updatePatientByEmail(email, patient)
                .orElseThrow(() -> new PatientNotFoundException("Nie znaleziono pacjenta o wskazanym adresie email."));
    }

    public String updatePasswordByEmail(String email, ChangePatientDataCommand changePasswordCommand) {
        return patientRepository.updatePasswordByEmail(email, changePasswordCommand)
                .map(patient -> "Zmieniono hasło na: " + patient.getPassword())
                .orElseThrow(() -> new PatientNotFoundException("Nie znaleziono pacjenta o wskazanym adresie email."));
    }
}