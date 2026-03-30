package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.exception.DoctorNotFoundException;
import com.barmalat.medicalclinic.exception.MedicalClinicException;
import com.barmalat.medicalclinic.exception.PatientNotFoundException;
import com.barmalat.medicalclinic.model.commands.CreateVisitCommand;
import com.barmalat.medicalclinic.model.entities.Doctor;
import com.barmalat.medicalclinic.model.entities.Patient;
import com.barmalat.medicalclinic.model.entities.Visit;
import com.barmalat.medicalclinic.model.entities.VisitStatus;
import com.barmalat.medicalclinic.repository.DoctorRepository;
import com.barmalat.medicalclinic.repository.PatientRepository;
import com.barmalat.medicalclinic.repository.VisitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public Page<Visit> find(Long patientId, Long doctorId, String specialization, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        if (patientId != null) {
            log.info("process of finding visits by patientId:{} started", patientId);
            Page<Visit> result = visitRepository.findByPatientId(patientId, pageable);
            log.info("process of finding visits by patientId:{} finished", patientId);
            return result;
        }
        if (doctorId != null) {
            log.info("process of finding visits by doctorId:{} started", doctorId);
            if (!doctorRepository.existsById(doctorId)) {
                throw new DoctorNotFoundException("Nie znaleziono doktora o wskazanym ID.");
            }
            Page<Visit> result = visitRepository.findByDoctorId(doctorId, pageable);
            log.info("process of finding visits by doctorId:{} finished", doctorId);
            return result;
        }
        if (specialization != null && from != null && to != null) {
            log.info("process of finding visits by specialization:{} between:{} and:{} started", specialization, from, to);
            Page<Visit> result = visitRepository.findByDoctorSpecializationAndStartTimeBetween(specialization, from, to, pageable);
            log.info("process of finding visits by specialization:{} between:{} and:{} finished", specialization, from, to);
            return result;
        }
        log.info("process of finding all visits started");
        Page<Visit> result = visitRepository.findAll(pageable);
        log.info("process of finding all visits finished");
        return result;
    }


    @Transactional
    public Visit addVisit(CreateVisitCommand command) {
        log.info("process of creating new visit started");
        Doctor doctor = doctorRepository.findById(command.doctorId())
                .orElseThrow(() -> new DoctorNotFoundException("Nie znaleziono doktora o wskazanym ID."));
        validateDates(command);
        Visit visit = new Visit(null, doctor, null, command.startTime(), command.endTime(), VisitStatus.AVAILABLE);
        Visit result = visitRepository.save(visit);
        log.info("process of creating new visit finished");
        return result;
    }

    @Transactional
    public Visit addPatientToVisit(Long visitId, Long patientId) {
        log.info("process of adding patient to visit started");
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new MedicalClinicException("Nie znaleziono wizyty o wskazanym ID.", HttpStatus.NOT_FOUND));
        if (visit.getStatus() != VisitStatus.AVAILABLE) {
            throw new MedicalClinicException("Wybrana wizyta nie jest dostępna", HttpStatus.CONFLICT);
        }
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Nie znaleziono pacjenta o wskazanym ID."));
        if (visit.getStartTime().isBefore(LocalDateTime.now())) {
            throw new MedicalClinicException("Wizyta nie może zacząć się w przeszłości!", HttpStatus.BAD_REQUEST);
        }
        visit.setPatient(patient);
        visit.setStatus(VisitStatus.RESERVED);
        Visit result = visitRepository.save(visit);
        log.info("process of adding patient to visit finished");
        return result;
    }

    public Page<Visit> findAvailableByDoctorId(Long doctorId, Pageable pageable) {
        log.info("process of finding available visits for doctorId:{} started", doctorId);
        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException("Nie znaleziono doktora o wskazanym ID.");
        }
        Page<Visit> result = visitRepository.findByDoctorIdAndStatus(doctorId, VisitStatus.AVAILABLE, pageable);
        log.info("process of finding available visits for doctorId:{} finished", doctorId);
        return result;
    }

    public Page<Visit> findAvailableByDateAndSpecialization(LocalDate date, String specialization, Pageable pageable) {
        log.info("process of finding available visits for date:{} and specialization:{} started", date, specialization);
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.atTime(23, 59, 59);
        Page<Visit> result = visitRepository.findByStatusAndDoctorSpecializationAndStartTimeBetween(
                VisitStatus.AVAILABLE, specialization, from, to, pageable);
        log.info("process of finding available visits for date:{} and specialization:{} finished", date, specialization);
        return result;
    }

    @Transactional
    public Visit cancelVisit(Long visitId) {
        log.info("process of cancelling visit:{} started", visitId);
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new MedicalClinicException("Nie znaleziono wizyty o wskazanym ID.", HttpStatus.NOT_FOUND));
        if (visit.getStatus() == VisitStatus.CANCELLED) {
            throw new MedicalClinicException("Wizyta jest już odwołana.", HttpStatus.CONFLICT);
        }
        visit.setStatus(VisitStatus.CANCELLED);
        Visit result = visitRepository.save(visit);
        log.info("process of cancelling visit:{} finished", visitId);
        return result;
    }

    public Page<Visit> findAvailableByTimeRangeAndSpecialization(
            LocalDateTime from, LocalDateTime to, String specialization, Pageable pageable) {
        if (specialization != null) {
            log.info("process of finding available visits between:{} and:{} with specialization:{} started", from, to, specialization);
            Page<Visit> result = visitRepository.findByStatusAndDoctorSpecializationAndStartTimeBetween(
                    VisitStatus.AVAILABLE, specialization, from, to, pageable);
            log.info("process of finding available visits between:{} and:{} with specialization:{} finished", from, to, specialization);
            return result;
        }
        log.info("process of finding available visits between:{} and:{} started", from, to);
        Page<Visit> result = visitRepository.findByStatusAndStartTimeBetween(VisitStatus.AVAILABLE, from, to, pageable);
        log.info("process of finding available visits between:{} and:{} finished", from, to);
        return result;
    }

    private void validateDates(CreateVisitCommand command) {
        log.info("process of validating dates started");
        if (!command.startTime().isBefore(command.endTime())) {
            throw new MedicalClinicException("Wizyta musi zacząć się zanim się skończy!", HttpStatus.BAD_REQUEST);
        }
        if (command.startTime().getMinute() % 15 != 0 || command.endTime().getMinute() % 15 != 0) {
            throw new MedicalClinicException("Wizyta musi zaczynać i kończyć się w równych kwadransach!", HttpStatus.BAD_REQUEST);
        }
        if (command.startTime().isBefore(LocalDateTime.now())) {
            throw new MedicalClinicException("Wizyta nie może zacząć się w przeszłości!", HttpStatus.BAD_REQUEST);
        }
        if (visitRepository.existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(command.doctorId(), command.endTime(), command.startTime())) {
            throw new MedicalClinicException("Wizyta jest w kolizji z inną wizytą doctora", HttpStatus.CONFLICT);
        }
        log.info("process of validating dates finished");
    }
}