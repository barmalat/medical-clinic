package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.exception.PatientNotFoundException;
import com.barmalat.medicalclinic.mapper.PatientMapper;
import com.barmalat.medicalclinic.model.commands.ChangePatientDataCommand;
import com.barmalat.medicalclinic.model.commands.CreatePatientCommand;
import com.barmalat.medicalclinic.model.dtos.PatientDto;
import com.barmalat.medicalclinic.model.entities.Patient;
import com.barmalat.medicalclinic.model.entities.User;
import com.barmalat.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PatientServiceTest {
    PatientService patientService;
    PatientRepository patientRepository;
    PatientMapper patientMapper;

    @BeforeEach
    void setup() {
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.patientMapper = Mappers.getMapper(PatientMapper.class);
        this.patientService = new PatientService(patientRepository, patientMapper);
    }

    @Test
    void findAll_DataCorrect_PagePatientsReturned() {
        //given
        Pageable pageable = PageRequest.of(0, 5);
        List<Patient> patients = List.of(
                new Patient(1L, "e", "p", "i", "p", "b", null),
                new Patient(2L, "e", "p", "i", "p", "b", null));
        when(patientRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(patients, pageable, patients.size()));
        //when
        Page<Patient> result = patientService.findAll(pageable);
        //then
        Assertions.assertAll(
                () -> assertEquals(2, result.getTotalElements()),
                () -> assertEquals(patients, result.getContent())
        );
        verify(patientRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void findByEmail_DataCorrect_PatientReturned() {
        //given
        String email = "email";
        Patient patient = new Patient(1L, "email", "567", "345", "123", "1.1.2001", null);
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.of(patient));
        //when
        Patient result = patientService.findByEmail(email);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("email", result.getEmail()),
                () -> assertEquals("567", result.getPassword()),
                () -> assertEquals("345", result.getIdCardNo()),
                () -> assertEquals("123", result.getPhoneNumber()),
                () -> assertEquals("1.1.2001", result.getBirthday()),
                () -> assertNull(result.getUser())
        );
        verify(patientRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void findByEmail_PatientNotFound_PatientNotFoundExceptionThrown() {
        //given
        String email = "ema";
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
        //when+then
        PatientNotFoundException result = Assertions.assertThrows(PatientNotFoundException.class,
                () -> patientService.findByEmail(email));
        assertEquals("Nie znaleziono pacjenta o wskazanym adresie email.", result.getMessage());
        verify(patientRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void addPatient_DataCorrect_PatientReturn() {
        //given
        CreatePatientCommand command = new CreatePatientCommand("email", "567", "345", "bar", "malat", "123", "1.1.2001");
        User user = new User(1L, "bar", "malat", null, null);
        Patient patient = patientMapper.toEntity(command);
        patient.setUser(user);
        patient.setId(1L);
        when(patientRepository.save(any())).thenReturn(patient);
        //when
        Patient result = patientService.addPatient(command);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("email", result.getEmail()),
                () -> assertEquals("567", result.getPassword()),
                () -> assertEquals("345", result.getIdCardNo()),
                () -> assertEquals("bar", result.getUser().getFirstName()),
                () -> assertEquals("malat", result.getUser().getLastName()),
                () -> assertEquals("123", result.getPhoneNumber()),
                () -> assertEquals("1.1.2001", result.getBirthday())
        );
        verify(patientRepository, times(1)).save(argThat(new NewPatientArgumentMatcher(null,
                "email", "567", "345", "123", "1.1.2001", "bar", "malat")));
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void deleteByEmail_DataCorrect_PatientReturn() {
        //given
        String email = "email";
        Patient patient = new Patient(1L, "email", "567", "345", "123", "1.1.2001", null);
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.of(patient));
        doNothing().when(patientRepository).delete(patient);
        //when
        Patient result = patientService.deleteByEmail(email);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("email", result.getEmail()),
                () -> assertEquals("567", result.getPassword()),
                () -> assertEquals("345", result.getIdCardNo()),
                () -> assertEquals("123", result.getPhoneNumber()),
                () -> assertEquals("1.1.2001", result.getBirthday()),
                () -> assertNull(result.getUser())
        );
        verify(patientRepository, times(1)).findByEmail(email);
        verify(patientRepository, times(1)).delete(patient);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void deleteByEmail_PatientNotFound_PatientNotFoundExceptionThrown() {
        //given
        String email = "ema";
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
        //when+then
        PatientNotFoundException result = Assertions.assertThrows(PatientNotFoundException.class,
                () -> patientService.deleteByEmail(email));
        assertEquals("Nie znaleziono pacjenta o wskazanym adresie email.", result.getMessage());
        verify(patientRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void updateByEmail_DataCorrect_PatientReturn() {
        //given
        String email = "email";
        PatientDto newData = new PatientDto(1L, "email", "new", "new", "new", "new", "new");
        User user = new User(1L, "old", "old", null, null);
        Patient patient = new Patient(1L, "email", "old", "old", "old", "old", user);
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.of(patient));
        when(patientRepository.save(any())).thenReturn(patient);
        //when
        Patient result = patientService.updateByEmail(email, newData);
        //when
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("email", result.getEmail()),
                () -> assertEquals("old", result.getPassword()),
                () -> assertEquals("new", result.getIdCardNo()),
                () -> assertEquals("new", result.getPhoneNumber()),
                () -> assertEquals("new", result.getBirthday()),
                () -> assertEquals("new", result.getUser().getFirstName()),
                () -> assertEquals("new", result.getUser().getLastName())
        );
        verify(patientRepository, times(1)).findByEmail(email);
        verify(patientRepository, times(1)).save(argThat(new NewPatientArgumentMatcher(1L,
                "email", "old", "new", "new", "new", "new", "new")));
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void updateByEmail_PatientNotFound_PatientNotFoundExceptionThrown() {
        //given
        String email = "ema";
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
        //when+then
        PatientNotFoundException result = Assertions.assertThrows(PatientNotFoundException.class,
                () -> patientService.updateByEmail(email, null));
        assertEquals("Nie znaleziono pacjenta o wskazanym adresie email.", result.getMessage());
        verify(patientRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void updatePasswordByEmail_DataCorrect_PatientUpdated() {
        //given
        String email = "email";
        ChangePatientDataCommand command = new ChangePatientDataCommand("newPass");
        Patient patient = new Patient(1L, "email", "567", "345", "123", "1.1.2001", null);
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any())).thenReturn(patient);
        //when
        patientService.updatePasswordByEmail(email, command);
        //then
        assertEquals("newPass", patient.getPassword());
        verify(patientRepository, times(1)).findByEmail(email);
        verify(patientRepository, times(1)).save(argThat(new NewPatientPasswordArgumentMatcher("newPass")));
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void updatePasswordByEmail_PatientNotFound_PatientNotFoundExceptionThrown() {
        //given
        String email = "ema";
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
        //when + then
        PatientNotFoundException result = Assertions.assertThrows(PatientNotFoundException.class,
                () -> patientService.updatePasswordByEmail(email, null));
        assertEquals("Nie znaleziono pacjenta o wskazanym adresie email.", result.getMessage());
        verify(patientRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(patientRepository);
    }

    @RequiredArgsConstructor
    public static class NewPatientArgumentMatcher implements ArgumentMatcher<Patient> {
        private final Long id;
        private final String email;
        private final String password;
        private final String idCardNo;
        private final String phoneNumber;
        private final String birthday;
        private final String userFirstName;
        private final String userLastName;

        @Override
        public boolean matches(Patient patient) {
            return nonNull(patient) &&
                    Objects.equals(patient.getId(), id) &&
                    patient.getEmail().equals(email) &&
                    patient.getPassword().equals(password) &&
                    patient.getIdCardNo().equals(idCardNo) &&
                    patient.getPhoneNumber().equals(phoneNumber) &&
                    patient.getBirthday().equals(birthday) &&
                    patient.getUser().getFirstName().equals(userFirstName) &&
                    patient.getUser().getLastName().equals(userLastName);
        }
    }

    @RequiredArgsConstructor
    public static class NewPatientPasswordArgumentMatcher implements ArgumentMatcher<Patient> {
        private final String password;

        @Override
        public boolean matches(Patient patient) {
            return nonNull(patient) && patient.getPassword().equals(password);
        }
    }
}