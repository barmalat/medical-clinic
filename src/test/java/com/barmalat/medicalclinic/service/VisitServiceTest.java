package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.model.commands.CreateVisitCommand;
import com.barmalat.medicalclinic.model.entities.Doctor;
import com.barmalat.medicalclinic.model.entities.Patient;
import com.barmalat.medicalclinic.model.entities.Visit;
import com.barmalat.medicalclinic.repository.DoctorRepository;
import com.barmalat.medicalclinic.repository.PatientRepository;
import com.barmalat.medicalclinic.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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
        verify(visitRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(visitRepository);
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
        verify(visitRepository, times(1)).findByPatientId(patientId, pageable);
        verifyNoMoreInteractions(visitRepository);
    }

    @Test
    void addVisit_DataCorrect_VisitReturn() {
        //given
        CreateVisitCommand command = new CreateVisitCommand(null, 1L, LocalDateTime.of(2026, 2, 15, 10, 0), LocalDateTime.of(2026, 2, 15, 10, 30));
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        Visit visit = new Visit(1L, doctor, null, LocalDateTime.of(2026, 2, 15, 10, 0), LocalDateTime.of(2026, 2, 15, 10, 30));
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        when(visitRepository.existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(anyLong(), any(), any())).thenReturn(false);
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
        verify(doctorRepository, times(1)).findById(command.doctorId());
        verify(visitRepository, times(1)).
                existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(command.doctorId(), command.endTime(), command.startTime());
        verify(visitRepository, times(1)).save(argThat(new NewVisitArgumentMatcher(null, doctor, null, LocalDateTime.of(2026, 2, 15, 10, 0), LocalDateTime.of(2026, 2, 15, 10, 30))));
        verifyNoMoreInteractions(doctorRepository);
        verifyNoMoreInteractions(visitRepository);
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
        Visit result = visitService.addPatientToVisit(visitId, patientId);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(patient, result.getPatient()),
                () -> assertEquals(doctor, result.getDoctor())
        );
        verify(visitRepository,times(1)).findById(visitId);
        verify(patientRepository,times(1)).findById(patientId);
        verify(visitRepository,times(1)).save(argThat(new NewVisitWithPatientArgumentMatcher(patient)));
        verifyNoMoreInteractions(visitRepository);
        verifyNoMoreInteractions(patientRepository);
    }

    @RequiredArgsConstructor
    public static class NewVisitArgumentMatcher implements ArgumentMatcher<Visit> {
        private final Long id;
        private final Doctor doctor;
        private final Patient patient;
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;

        @Override
        public boolean matches(Visit visit) {
            return nonNull(visit) &&
                    Objects.equals(visit.getId(), id) &&
                    Objects.equals(visit.getDoctor(),(doctor)) &&
                    Objects.equals(visit.getPatient(),(patient)) &&
                    visit.getStartTime().equals(startTime) &&
                    visit.getEndTime().equals(endTime);
        }
    }

    @RequiredArgsConstructor
    public static class NewVisitWithPatientArgumentMatcher implements ArgumentMatcher<Visit> {
        private final Patient patient;

        @Override
        public boolean matches(Visit visit) {
            return nonNull(visit) &&
                    Objects.equals(visit.getPatient(), patient);
        }
    }
}