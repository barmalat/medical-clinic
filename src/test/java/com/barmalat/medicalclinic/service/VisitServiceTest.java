package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.model.commands.CreateVisitCommand;
import com.barmalat.medicalclinic.model.entities.Doctor;
import com.barmalat.medicalclinic.model.entities.Patient;
import com.barmalat.medicalclinic.model.entities.Visit;
import com.barmalat.medicalclinic.repository.DoctorRepository;
import com.barmalat.medicalclinic.repository.PatientRepository;
import com.barmalat.medicalclinic.repository.VisitRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class VisitServiceTest {
    VisitService visitService;
    VisitRepository visitRepository;
    DoctorRepository doctorRepository;
    PatientRepository patientRepository;

    @BeforeEach
    void setup() {
        this.visitRepository = Mockito.mock(VisitRepository.class);
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.visitService = new VisitService(visitRepository, doctorRepository, patientRepository);
    }

    @Test
    void find_DataCorrectWithoutPatientId_PageVisitsReturn() {
        //given
        Pageable pageable = PageRequest.of(0, 5);
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        List<Visit> visits = List.of(
                new Visit(1L, doctor, null, LocalDateTime.of(2026, 2, 15, 10, 0), LocalDateTime.of(2025, 2, 15, 10, 30)),
                new Visit(2L, doctor, null, LocalDateTime.of(2026, 2, 16, 10, 0), LocalDateTime.of(2025, 2, 16, 10, 30))
        );
        when(visitRepository.findAll(pageable)).thenReturn(new PageImpl<>(visits, pageable, visits.size()));
        //when
        Page<Visit> result = visitService.find(null, pageable);
        //then
        Assertions.assertAll(
                () -> assertEquals(2, result.getTotalElements()),
                () -> assertEquals(visits, result.getContent())
        );
    }

    @Test
    void find_DataCorrectWithPatientId_PageVisitsReturn() {
        //given
        Long patientId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        List<Visit> visits = List.of(
                new Visit(1L, doctor, null, LocalDateTime.of(2026, 2, 15, 10, 0), LocalDateTime.of(2026, 2, 15, 10, 30)),
                new Visit(2L, doctor, null, LocalDateTime.of(2026, 2, 16, 10, 0), LocalDateTime.of(2026, 2, 16, 10, 30))
        );
        when(visitRepository.findByPatientId(1L, pageable)).thenReturn(new PageImpl<>(visits, pageable, visits.size()));
        //when
        Page<Visit> result = visitService.find(patientId, pageable);
        //then
        Assertions.assertAll(
                () -> assertEquals(2, result.getTotalElements()),
                () -> assertEquals(visits, result.getContent())
        );
    }

    @Test
    void addVisit_DataCorrect_VisitReturn() {
        //given
        CreateVisitCommand command = new CreateVisitCommand(null, 1L, LocalDateTime.of(2026, 2, 15, 10, 0), LocalDateTime.of(2026, 2, 15, 10, 30));
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        Visit visit = new Visit(1L, doctor, null, LocalDateTime.of(2026, 2, 15, 10, 0), LocalDateTime.of(2026, 2, 15, 10, 30));
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        when(visitRepository.save(any())).thenReturn(visit);
        //when
        Visit result = visitService.addVisit(command);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(doctor, result.getDoctor()),
                () -> assertEquals(LocalDateTime.of(2026, 2, 15, 10, 0), result.getStartTime()),
                () -> assertEquals(LocalDateTime.of(2026, 2, 15, 10, 30), result.getEndTime()),
                () -> assertNull(result.getPatient())
        );
    }

    @Test
    void addPatientToVisit_DataCorrect_VisitReturn() {
        //given
        Long visitId = 1L;
        Long patientId = 1L;
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        Visit visit = new Visit(1L, doctor, null, LocalDateTime.of(2026, 2, 15, 10, 0), LocalDateTime.of(2026, 2, 15, 10, 30));
        Patient patient = new Patient(1L, "ema", "pas", "idc", "pho", "bir", null);
        when(visitRepository.findById(anyLong())).thenReturn(Optional.of(visit));
        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(patient));
        when(visitRepository.save(any())).thenReturn(visit);
        //when
        Visit result = visitService.addPatientToVisit(visitId,patientId);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(patient, result.getPatient()),
                () -> assertEquals(doctor, result.getDoctor())
        );
    }
}