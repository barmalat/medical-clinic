package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.exception.PatientNotFoundException;
import com.barmalat.medicalclinic.mapper.PatientMapper;
import com.barmalat.medicalclinic.model.ChangePatientDataCommand;
import com.barmalat.medicalclinic.model.CreatePatientCommand;
import com.barmalat.medicalclinic.model.Patient;
import com.barmalat.medicalclinic.model.PatientDto;
import com.barmalat.medicalclinic.model.User;
import com.barmalat.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient findPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Nie znaleziono pacjenta o wskazanym adresie email."));
    }

    public Patient addPatient(CreatePatientCommand createPatientCommand) {
        User user = new User(null, createPatientCommand.getFirstName(), createPatientCommand.getLastName(), null);
        Patient patient = patientMapper.createPatientCommandToEntity(createPatientCommand);
        patient.setUser(user);
        return patientRepository.save(patient);
    }

    public Patient deletePatientByEmail(String email) {
        Patient patientToDelete = findPatientByEmail(email);
        patientRepository.delete(patientToDelete);
        return patientToDelete;
    }

    public Patient updatePatientByEmail(String email, PatientDto patient) {
        Patient patientToUpdate = findPatientByEmail(email);
        patientToUpdate.updatePatientPublicData(patient);
        return patientRepository.save(patientToUpdate);
    }

    public void updatePasswordByEmail(String email, ChangePatientDataCommand changePasswordCommand) {
        Patient patientToUpdatePassword = findPatientByEmail(email);
        patientToUpdatePassword.setPassword(changePasswordCommand.getPassword());
        patientRepository.save(patientToUpdatePassword);
    }
}