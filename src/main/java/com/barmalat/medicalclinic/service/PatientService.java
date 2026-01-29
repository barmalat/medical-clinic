package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.exception.PatientNotFoundException;
import com.barmalat.medicalclinic.mapper.PatientMapper;
import com.barmalat.medicalclinic.model.commands.ChangePatientDataCommand;
import com.barmalat.medicalclinic.model.commands.CreatePatientCommand;
import com.barmalat.medicalclinic.model.entities.Patient;
import com.barmalat.medicalclinic.model.dtos.PatientDto;
import com.barmalat.medicalclinic.model.entities.User;
import com.barmalat.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public Page<Patient> findAll(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    public Patient findByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Nie znaleziono pacjenta o wskazanym adresie email."));
    }

    public Patient addPatient(CreatePatientCommand createPatientCommand) {
        User user = new User(null, createPatientCommand.firstName(), createPatientCommand.lastName(), null, null);
        Patient patient = patientMapper.toEntity(createPatientCommand);
        patient.setUser(user);
        return patientRepository.save(patient);
    }

    public Patient deleteByEmail(String email) {
        Patient patient = findByEmail(email);
        patientRepository.delete(patient);
        return patient;
    }

    public Patient updateByEmail(String email, PatientDto patientDto) {
        Patient patient = findByEmail(email);
        patient.updatePublicData(patientDto);
        return patientRepository.save(patient);
    }

    public void updatePasswordByEmail(String email, ChangePatientDataCommand changePasswordCommand) {
        Patient patient = findByEmail(email);
        patient.setPassword(changePasswordCommand.password());
        patientRepository.save(patient);
    }
}