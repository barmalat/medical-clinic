package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.exception.PatientNotFoundException;
import com.barmalat.medicalclinic.mapper.PatientMapper;
import com.barmalat.medicalclinic.model.commands.ChangePatientDataCommand;
import com.barmalat.medicalclinic.model.commands.CreatePatientCommand;
import com.barmalat.medicalclinic.model.entities.Patient;
import com.barmalat.medicalclinic.model.dtos.PatientDto;
import com.barmalat.medicalclinic.model.entities.User;
import com.barmalat.medicalclinic.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public Page<Patient> findAll(Pageable pageable) {
        log.info("process of finding all patients started");
        Page<Patient> result = patientRepository.findAll(pageable);
        log.info("process of finding all patients finished");
        return result;
    }

    public Patient findByEmail(String email) {
        log.info("process of finding patient by email:{} started", email);
        Patient result = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Nie znaleziono pacjenta o wskazanym adresie email."));
        log.info("process of finding patient by email:{} finished", email);
        return result;
    }

    @Transactional
    public Patient addPatient(CreatePatientCommand createPatientCommand) {
        log.info("process of creating new patient started");
        User user = new User(null, createPatientCommand.firstName(), createPatientCommand.lastName(), null, null);
        Patient patient = patientMapper.toEntity(createPatientCommand);
        patient.setUser(user);
        Patient result = patientRepository.save(patient);
        log.info("process of creating new patient finished");
        return result;
    }

    @Transactional
    public Patient deleteByEmail(String email) {
        log.info("process of deleting patient by email:{} started", email);
        Patient patient = findByEmail(email);
        patientRepository.delete(patient);
        log.info("process of deleting patient by email:{} finished", email);
        return patient;
    }

    @Transactional
    public Patient updateByEmail(String email, PatientDto patientDto) {
        log.info("process of updating patient by email:{} started", email);
        Patient patient = findByEmail(email);
        patient.updatePublicData(patientDto);
        Patient result = patientRepository.save(patient);
        log.info("process of updating patient by email:{} finished", email);
        return result;
    }

    @Transactional
    public void updatePasswordByEmail(String email, ChangePatientDataCommand changePasswordCommand) {
        log.info("process of updating patient password by email:{} started", email);
        Patient patient = findByEmail(email);
        patient.setPassword(changePasswordCommand.password());
        patientRepository.save(patient);
        log.info("process of updating patient password by email:{} finished", email);
    }
}