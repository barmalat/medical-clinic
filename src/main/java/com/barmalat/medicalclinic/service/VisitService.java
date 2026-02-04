package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.exception.DoctorNotFoundException;
import com.barmalat.medicalclinic.exception.MedicalClinicException;
import com.barmalat.medicalclinic.exception.PatientNotFoundException;
import com.barmalat.medicalclinic.model.commands.CreateVisitCommand;
import com.barmalat.medicalclinic.model.entities.Doctor;
import com.barmalat.medicalclinic.model.entities.Patient;
import com.barmalat.medicalclinic.model.entities.Visit;
import com.barmalat.medicalclinic.repository.DoctorRepository;
import com.barmalat.medicalclinic.repository.PatientRepository;
import com.barmalat.medicalclinic.repository.VisitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public Page<Visit> findAll(Pageable pageable) {
        return visitRepository.findAll(pageable);
    }

    private void correctDates(CreateVisitCommand command) {
        if (command.startTime().isAfter(command.endTime())) {
            throw new MedicalClinicException("Wizyta musi zacząć się zanim się skończy!", HttpStatus.BAD_REQUEST);
        }
        if (command.startTime().getMinute() % 15 != 0 || command.endTime().getMinute() % 15 != 0) {
            throw new MedicalClinicException("Wizyta musi zaczynać i kończyć się w równych kwadransach!", HttpStatus.BAD_REQUEST);
        }
        if (command.startTime().isBefore(LocalDateTime.now())) {
            throw new MedicalClinicException("Wizyta nie może zacząć się w przeszłości!", HttpStatus.BAD_REQUEST);
        }
        if (visitRepository.existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(command.doctorId(), command.endTime(), command.startTime())) {
            throw new MedicalClinicException("Wizyta jest w kolizji z inną wizytą doctora", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public Visit addVisit(CreateVisitCommand command) {
        Doctor doctor = doctorRepository.findById(command.doctorId())
                .orElseThrow(() -> new DoctorNotFoundException("Nie znaleziono doktora o wskazanym ID."));
        correctDates(command);
        Visit visit = new Visit(null, doctor, null, command.startTime(), command.endTime());
        return visitRepository.save(visit);
    }

    @Transactional
    public Visit addPatientToVisit(Long visitId, Long patientId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new MedicalClinicException("Nie znaleziono wizyty o wskazanym ID.", HttpStatus.NOT_FOUND));
        if (visit.getPatient() != null) {
            throw new MedicalClinicException("Wybrana wizyta nie jest wolna", HttpStatus.NOT_ACCEPTABLE);
        }
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Nie znaleziono pacjenta o wskazanym ID."));
        if (visit.getStartTime().isBefore(LocalDateTime.now())) {
            throw new MedicalClinicException("Wizyta nie może zacząć się w przeszłości!", HttpStatus.BAD_REQUEST);
        }
        visit.setPatient(patient);
        return visitRepository.save(visit);
    }

    public Page<Visit> findVisitsByPatientId(Long patientId, Pageable pageable) {
        return visitRepository.findByPatientId(patientId, pageable);
    }
}