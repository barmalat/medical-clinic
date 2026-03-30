package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.exception.DoctorNotFoundException;
import com.barmalat.medicalclinic.exception.MedicalClinicException;
import com.barmalat.medicalclinic.model.commands.CreateVisitCommand;
import com.barmalat.medicalclinic.model.entities.Doctor;
import com.barmalat.medicalclinic.model.entities.Patient;
import com.barmalat.medicalclinic.model.entities.Visit;
import com.barmalat.medicalclinic.model.entities.VisitStatus;
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
                new Visit(1L, doctor, null, LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30), VisitStatus.AVAILABLE),
                new Visit(2L, doctor, null, LocalDateTime.of(2027, 2, 16, 10, 0), LocalDateTime.of(2027, 2, 16, 10, 30), VisitStatus.AVAILABLE)
        );
        when(visitRepository.findAll(pageable)).thenReturn(new PageImpl<>(visits, pageable, visits.size()));
        //when
        Page<Visit> result = visitService.find(null,null,null,null,null, pageable);
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
                new Visit(1L, doctor, null, LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30),VisitStatus.AVAILABLE),
                new Visit(2L, doctor, null, LocalDateTime.of(2027, 2, 16, 10, 0), LocalDateTime.of(2027, 2, 16, 10, 30),VisitStatus.AVAILABLE)
        );
        when(visitRepository.findByPatientId(1L, pageable)).thenReturn(new PageImpl<>(visits, pageable, visits.size()));
        //when
        Page<Visit> result = visitService.find(patientId,null,null,null,null, pageable);
        //then
        Assertions.assertAll(
                () -> assertEquals(2, result.getTotalElements()),
                () -> assertEquals(visits, result.getContent())
        );
        verify(visitRepository, times(1)).findByPatientId(patientId, pageable);
        verifyNoMoreInteractions(visitRepository);
    }

    @Test
    void find_DataCorrectWithDoctorId_PageVisitsReturn() {
        //given
        Long doctorId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        List<Visit> visits = List.of(
                new Visit(1L, doctor, null, LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30), VisitStatus.AVAILABLE),
                new Visit(2L, doctor, null, LocalDateTime.of(2027, 2, 16, 10, 0), LocalDateTime.of(2027, 2, 16, 10, 30), VisitStatus.RESERVED)
        );
        when(doctorRepository.existsById(doctorId)).thenReturn(true);
        when(visitRepository.findByDoctorId(doctorId, pageable)).thenReturn(new PageImpl<>(visits, pageable, visits.size()));
        //when
        Page<Visit> result = visitService.find(null, doctorId, null, null, null, pageable);
        //then
        Assertions.assertAll(
                () -> assertEquals(2, result.getTotalElements()),
                () -> assertEquals(visits, result.getContent())
        );
        verify(doctorRepository, times(1)).existsById(doctorId);
        verify(visitRepository, times(1)).findByDoctorId(doctorId, pageable);
        verifyNoMoreInteractions(visitRepository);
    }

    @Test
    void find_DoctorIdDoctorNotFound_DoctorNotFoundExceptionThrown() {
        //given
        Long doctorId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        when(doctorRepository.existsById(doctorId)).thenReturn(false);
        //when
        DoctorNotFoundException result = Assertions.assertThrows(DoctorNotFoundException.class,
                () -> visitService.find(null, doctorId, null, null, null, pageable));
        //then
        assertEquals("Nie znaleziono doktora o wskazanym ID.", result.getMessage());
        verify(doctorRepository, times(1)).existsById(doctorId);
        verifyNoMoreInteractions(visitRepository);
    }

    @Test
    void addVisit_DataCorrect_VisitReturn() {
        //given
        CreateVisitCommand command = new CreateVisitCommand(null, 1L, LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30));
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        Visit visit = new Visit(1L, doctor, null, LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30), VisitStatus.AVAILABLE);
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        when(visitRepository.existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(anyLong(), any(), any())).thenReturn(false);
        when(visitRepository.save(any())).thenReturn(visit);
        //when
        Visit result = visitService.addVisit(command);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(doctor, result.getDoctor()),
                () -> assertEquals(LocalDateTime.of(2027, 2, 15, 10, 0), result.getStartTime()),
                () -> assertEquals(LocalDateTime.of(2027, 2, 15, 10, 30), result.getEndTime()),
                () -> assertNull(result.getPatient())
        );
        verify(doctorRepository, times(1)).findById(command.doctorId());
        verify(visitRepository, times(1)).
                existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(command.doctorId(), command.endTime(), command.startTime());
        verify(visitRepository, times(1)).save(argThat(new NewVisitArgumentMatcher(
                null, doctor, null, LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30), VisitStatus.AVAILABLE)));
        verifyNoMoreInteractions(doctorRepository);
        verifyNoMoreInteractions(visitRepository);
    }

    @Test
    void addVisit_DoctorNotFound_DoctorNotFoundExceptionThrown() {
        //given
        CreateVisitCommand command = new CreateVisitCommand(null, 1L, LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30));
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.empty());
        //when
        DoctorNotFoundException result = Assertions.assertThrows(DoctorNotFoundException.class,
                () -> visitService.addVisit(command));
        //then
        assertEquals("Nie znaleziono doktora o wskazanym ID.", result.getMessage());
        verify(doctorRepository, times(1)).findById(command.doctorId());
        verifyNoMoreInteractions(doctorRepository);
        verifyNoMoreInteractions(visitRepository);
    }

    @Test
    void addVisit_StartTimeIsNotBeforeEndTime_MedicalClinicExceptionThrown() {
        //given
        CreateVisitCommand command = new CreateVisitCommand(null, 1L, LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 9, 30));
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        //when
        MedicalClinicException result = Assertions.assertThrows(MedicalClinicException.class,
                () -> visitService.addVisit(command));
        //then
        assertEquals("Wizyta musi zacząć się zanim się skończy!", result.getMessage());
        verify(doctorRepository, times(1)).findById(command.doctorId());
        verifyNoMoreInteractions(doctorRepository);
        verifyNoMoreInteractions(visitRepository);
    }

    @Test
    void addVisit_TimesAreNotDivisibleBy15_MedicalClinicExceptionThrown() {
        //given
        CreateVisitCommand command = new CreateVisitCommand(null, 1L, LocalDateTime.of(2027, 2, 15, 10, 5), LocalDateTime.of(2027, 2, 15, 10, 30));
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        //when
        MedicalClinicException result = Assertions.assertThrows(MedicalClinicException.class,
                () -> visitService.addVisit(command));
        //then
        assertEquals("Wizyta musi zaczynać i kończyć się w równych kwadransach!", result.getMessage());
        verify(doctorRepository, times(1)).findById(command.doctorId());
        verifyNoMoreInteractions(doctorRepository);
        verifyNoMoreInteractions(visitRepository);
    }

    @Test
    void addVisit_StartTimeIsBeforeNow_MedicalClinicExceptionThrown() {
        //given
        CreateVisitCommand command = new CreateVisitCommand(null, 1L, LocalDateTime.of(2026, 2, 15, 10, 0), LocalDateTime.of(2026, 2, 15, 10, 30));
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        //when
        MedicalClinicException result = Assertions.assertThrows(MedicalClinicException.class,
                () -> visitService.addVisit(command));
        //then
        assertEquals("Wizyta nie może zacząć się w przeszłości!", result.getMessage());
        verify(doctorRepository, times(1)).findById(command.doctorId());
        verifyNoMoreInteractions(doctorRepository);
        verifyNoMoreInteractions(visitRepository);
    }

    @Test
    void addVisit_existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThanReturnTrue_MedicalClinicExceptionThrown() {
        //given
        CreateVisitCommand command = new CreateVisitCommand(null, 1L, LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30));
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        when(visitRepository.existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(anyLong(), any(), any())).thenReturn(true);
        //when
        MedicalClinicException result = Assertions.assertThrows(MedicalClinicException.class,
                () -> visitService.addVisit(command));
        //then
        assertEquals("Wizyta jest w kolizji z inną wizytą doctora", result.getMessage());
        verify(doctorRepository, times(1)).findById(command.doctorId());
        verify(visitRepository,times(1)).existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(command.doctorId(), command.endTime(), command.startTime());
        verifyNoMoreInteractions(doctorRepository);
        verifyNoMoreInteractions(visitRepository);
    }

    @Test
    void addPatientToVisit_DataCorrect_VisitReturn() {
        //given
        Long visitId = 1L;
        Long patientId = 1L;
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        Visit visit = new Visit(1L, doctor, null, LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30), VisitStatus.AVAILABLE);
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
                () -> assertEquals(doctor, result.getDoctor()),
                () -> assertEquals(VisitStatus.RESERVED, result.getStatus())
        );
        verify(visitRepository, times(1)).findById(visitId);
        verify(patientRepository, times(1)).findById(patientId);
        verify(visitRepository, times(1)).save(argThat(new NewVisitWithPatientArgumentMatcher(patient, VisitStatus.RESERVED)));
        verifyNoMoreInteractions(visitRepository);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void addPatientToVisit_VisitNotAvailable_MedicalClinicExceptionThrown() {
        //given
        Long visitId = 1L;
        Long patientId = 1L;
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        Visit visit = new Visit(1L, doctor, null, LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30), VisitStatus.RESERVED);
        when(visitRepository.findById(anyLong())).thenReturn(Optional.of(visit));
        //when
        MedicalClinicException result = Assertions.assertThrows(MedicalClinicException.class,
                () -> visitService.addPatientToVisit(visitId, patientId));
        //then
        assertEquals("Wybrana wizyta nie jest dostępna", result.getMessage());
        verify(visitRepository, times(1)).findById(visitId);
        verifyNoMoreInteractions(visitRepository);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void cancelVisit_DataCorrect_VisitReturn() {
        //given
        Long visitId = 1L;
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        Patient patient = new Patient(1L, "ema", "pas", "idc", "pho", "bir", null);
        Visit visit = new Visit(1L, doctor, patient, LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30), VisitStatus.RESERVED);
        when(visitRepository.findById(anyLong())).thenReturn(Optional.of(visit));
        when(visitRepository.save(any())).thenReturn(visit);
        //when
        Visit result = visitService.cancelVisit(visitId);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(patient, result.getPatient()),
                () -> assertEquals(VisitStatus.CANCELLED, result.getStatus())
        );
        verify(visitRepository, times(1)).findById(visitId);
        verify(visitRepository, times(1)).save(visit);
        verifyNoMoreInteractions(visitRepository);
    }

    @Test
    void cancelVisit_VisitAlreadyCancelled_MedicalClinicExceptionThrown() {
        //given
        Long visitId = 1L;
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        Visit visit = new Visit(1L, doctor, null, LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30), VisitStatus.CANCELLED);
        when(visitRepository.findById(anyLong())).thenReturn(Optional.of(visit));
        //when
        MedicalClinicException result = Assertions.assertThrows(MedicalClinicException.class,
                () -> visitService.cancelVisit(visitId));
        //then
        assertEquals("Wizyta jest już odwołana.", result.getMessage());
        verify(visitRepository, times(1)).findById(visitId);
        verifyNoMoreInteractions(visitRepository);
    }


    @RequiredArgsConstructor
    public static class NewVisitArgumentMatcher implements ArgumentMatcher<Visit> {
        private final Long id;
        private final Doctor doctor;
        private final Patient patient;
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;
        private final VisitStatus status;

        @Override
        public boolean matches(Visit visit) {
            return nonNull(visit) &&
                    Objects.equals(visit.getId(), id) &&
                    Objects.equals(visit.getDoctor(), (doctor)) &&
                    Objects.equals(visit.getPatient(), (patient)) &&
                    visit.getStartTime().equals(startTime) &&
                    visit.getEndTime().equals(endTime) &&
                    visit.getStatus() == status;
        }
    }

    @RequiredArgsConstructor
    public static class NewVisitWithPatientArgumentMatcher implements ArgumentMatcher<Visit> {
        private final Patient patient;
        private final VisitStatus status;

        @Override
        public boolean matches(Visit visit) {
            return nonNull(visit) &&
                    Objects.equals(visit.getPatient(), patient) &&
                    visit.getStatus() == status;
        }
    }
}