package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.exception.DoctorNotFoundException;
import com.barmalat.medicalclinic.exception.FacilityNotFoundException;
import com.barmalat.medicalclinic.mapper.DoctorMapper;
import com.barmalat.medicalclinic.model.commands.CreateDoctorCommand;
import com.barmalat.medicalclinic.model.dtos.DoctorDto;
import com.barmalat.medicalclinic.model.entities.*;
import com.barmalat.medicalclinic.repository.DoctorRepository;
import com.barmalat.medicalclinic.repository.FacilityRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class DoctorServiceTest {
    DoctorService doctorService;
    DoctorRepository doctorRepository;
    DoctorMapper doctorMapper;
    FacilityRepository facilityRepository;

    @BeforeEach
    void setup() {
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.facilityRepository = Mockito.mock(FacilityRepository.class);
        this.doctorMapper = Mappers.getMapper(DoctorMapper.class);
        this.doctorService = new DoctorService(doctorRepository, doctorMapper, facilityRepository);
    }

    @Test
    void findAll_DataCorrect_PageDoctorsReturn() {
        //given
        Pageable pageable = PageRequest.of(0, 5);
        List<Doctor> doctors = List.of(
                new Doctor(1L, "e", "p", "s", null, null, null),
                new Doctor(2L, "e", "p", "s", null, null, null)
        );
        when(doctorRepository.findAll(pageable)).thenReturn(new PageImpl<>(doctors, pageable, doctors.size()));
        //when
        Page<Doctor> result = doctorService.findAll(pageable);
        //then
        Assertions.assertAll(
                () -> assertEquals(2, result.getTotalElements()),
                () -> assertEquals(doctors, result.getContent())
        );
        verify(doctorRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void findById_DataCorrect_DoctorReturn() {
        //given
        Long doctorId = 1L;
        Doctor doctor = new Doctor(1L, "e", "p", "s", null, null, null);
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        //when
        Doctor result = doctorService.findById(doctorId);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("e", result.getEmail()),
                () -> assertEquals("p", result.getPassword()),
                () -> assertEquals("s", result.getSpecialization()),
                () -> assertNull(result.getUser()),
                () -> assertNull(result.getFacilities()),
                () -> assertNull(result.getVisits())
        );
        verify(doctorRepository, times(1)).findById(doctorId);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void findById_DoctorNotFound_DoctorNotFoundExceptionThrown() {
        //given
        Long doctorId = 1L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
        //when+then
        DoctorNotFoundException result = Assertions.assertThrows(DoctorNotFoundException.class,
                () -> doctorService.findById(doctorId));
        assertEquals("Nie znaleziono doktora o wskazanym ID.", result.getMessage());
        verify(doctorRepository, times(1)).findById(doctorId);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void addDoctor_DataCorrect_DoctorReturn() {
        //given
        CreateDoctorCommand command = new CreateDoctorCommand(null, "e", "p", "s", "f", "l");
        User user = new User(1L, "f", "l", null, null);
        Doctor doctor = doctorMapper.toEntity(command);
        doctor.setUser(user);
        doctor.setId(1L);
        List<Facility> facilities = new ArrayList<>();
        List<Visit> visits = new ArrayList<>();
        doctor.setFacilities(facilities);
        doctor.setVisits(visits);
        when(doctorRepository.save(any())).thenReturn(doctor);
        //when
        Doctor result = doctorService.addDoctor(command);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("e", result.getEmail()),
                () -> assertEquals("p", result.getPassword()),
                () -> assertEquals("s", result.getSpecialization()),
                () -> assertEquals("f", result.getUser().getFirstName()),
                () -> assertEquals("l", result.getUser().getLastName()),
                () -> assertEquals(facilities, result.getFacilities()),
                () -> assertEquals(visits, result.getVisits())
        );
        verify(doctorRepository, times(1)).save(argThat(new NewDoctorArgumentMatcher(
                null, "e", "p", "s", "f", "l")));
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void deleteById_DataCorrect_DoctorReturn() {
        //given
        Long doctorId = 1L;
        Doctor doctor = new Doctor(1L, "e", "p", "s", null, null, null);
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        doNothing().when(doctorRepository).delete(doctor);
        //when
        Doctor result = doctorService.deleteById(doctorId);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("e", result.getEmail()),
                () -> assertEquals("p", result.getPassword()),
                () -> assertEquals("s", result.getSpecialization()),
                () -> assertNull(result.getUser()),
                () -> assertNull(result.getFacilities()),
                () -> assertNull(result.getVisits())
        );
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(doctorRepository, times(1)).delete(doctor);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void deleteById_DoctorNotFound_DoctorNotFoundExceptionThrown() {
        //given
        Long doctorId = 1L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
        //when+then
        DoctorNotFoundException result = Assertions.assertThrows(DoctorNotFoundException.class,
                () -> doctorService.deleteById(doctorId));
        assertEquals("Nie znaleziono doktora o wskazanym ID.", result.getMessage());
        verify(doctorRepository, times(1)).findById(doctorId);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void updateById_DataCorrect_DoctorReturn() {
        //given
        Long doctorId = 1L;
        DoctorDto newData = new DoctorDto(1L, "new", "new", "new", "new", null);
        User user = new User(1L, "old", "old", null, null);
        List<Facility> facilities = new ArrayList<>();
        List<Visit> visits = new ArrayList<>();
        Doctor doctor = new Doctor(1L, "old", "old", "old", user, facilities, visits);
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any())).thenReturn(doctor);
        //when
        Doctor result = doctorService.updateById(doctorId, newData);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("new", result.getEmail()),
                () -> assertEquals("old", result.getPassword()),
                () -> assertEquals("new", result.getSpecialization()),
                () -> assertEquals("new", result.getUser().getFirstName()),
                () -> assertEquals("new", result.getUser().getLastName()),
                () -> assertEquals(facilities, result.getFacilities()),
                () -> assertEquals(visits, result.getVisits())
        );
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(doctorRepository, times(1)).save(argThat(new NewDoctorArgumentMatcher(
                1L, "new", "old", "new", "new", "new")));
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void updateById_DoctorNotFound_DoctorNotFoundExceptionThrown() {
        //given
        Long doctorId = 1L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
        //when+then
        DoctorNotFoundException result = Assertions.assertThrows(DoctorNotFoundException.class,
                () -> doctorService.updateById(doctorId, null));
        assertEquals("Nie znaleziono doktora o wskazanym ID.", result.getMessage());
        verify(doctorRepository, times(1)).findById(doctorId);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void addFacilityById_DataCorrect_DoctorReturn() {
        //given
        Long doctorId = 1L;
        Long facilityId = 1L;
        User user = new User(1L, "fir", "las", null, null);
        List<Facility> facilities = new ArrayList<>();
        List<Visit> visits = new ArrayList<>();
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", user, facilities, visits);
        List<Doctor> doctors = new ArrayList<>();
        Facility facility = new Facility(1L, "nam", "cit", "pos", "str", "strNo", doctors);
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        when(facilityRepository.findById(anyLong())).thenReturn(Optional.of(facility));
        when(doctorRepository.save(any())).thenReturn(doctor);
        //when
        Doctor result = doctorService.addFacilityById(doctorId, facilityId);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("ema", result.getEmail()),
                () -> assertEquals("pas", result.getPassword()),
                () -> assertEquals("spe", result.getSpecialization()),
                () -> assertEquals("fir", result.getUser().getFirstName()),
                () -> assertEquals("las", result.getUser().getLastName()),
                () -> assertEquals(facilities, result.getFacilities()),
                () -> assertEquals(visits, result.getVisits()),
                () -> assertEquals(1L, result.getFacilities().getFirst().getId()),
                () -> assertEquals("nam", result.getFacilities().getFirst().getName()),
                () -> assertEquals("cit", result.getFacilities().getFirst().getCity()),
                () -> assertEquals("pos", result.getFacilities().getFirst().getPostalCode()),
                () -> assertEquals("str", result.getFacilities().getFirst().getStreet()),
                () -> assertEquals("strNo", result.getFacilities().getFirst().getStreetNumber()),
                () -> assertEquals(doctors, result.getFacilities().getFirst().getDoctors())
        );
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(facilityRepository, times(1)).findById(facilityId);
        verify(doctorRepository, times(1)).save(doctor);
        verifyNoMoreInteractions(doctorRepository);
        verifyNoMoreInteractions(facilityRepository);
    }

    @Test
    void addFacilityById_DoctorNotFound_DoctorNotFoundExceptionThrown() {
        //given
        Long doctorId = 1L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
        //when+then
        DoctorNotFoundException result = Assertions.assertThrows(DoctorNotFoundException.class,
                () -> doctorService.addFacilityById(doctorId, null));
        assertEquals("Nie znaleziono doktora o wskazanym ID.", result.getMessage());
        verify(doctorRepository, times(1)).findById(doctorId);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void addFacilityById_FacilityNotFound_FacilityNotFoundExceptionThrown() {
        //given
        Long doctorId = 1L;
        Long facilityId = 1L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(new Doctor()));
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());
        //when+then
        FacilityNotFoundException result = Assertions.assertThrows(FacilityNotFoundException.class,
                () -> doctorService.addFacilityById(doctorId, facilityId));
        assertEquals("Nie znaleziono placówki o podanym ID.", result.getMessage());
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(facilityRepository, times(1)).findById(facilityId);
        verifyNoMoreInteractions(doctorRepository);
        verifyNoMoreInteractions(facilityRepository);
    }

    @RequiredArgsConstructor
    public static class NewDoctorArgumentMatcher implements ArgumentMatcher<Doctor> {
        private final Long id;
        private final String email;
        private final String password;
        private final String specialization;
        private final String userFirstName;
        private final String userLastName;

        @Override
        public boolean matches(Doctor doctor) {
            return nonNull(doctor) &&
                    Objects.equals(doctor.getId(), id) &&
                    doctor.getEmail().equals(email) &&
                    doctor.getPassword().equals(password) &&
                    doctor.getSpecialization().equals(specialization) &&
                    doctor.getUser().getFirstName().equals(userFirstName) &&
                    doctor.getUser().getLastName().equals(userLastName);
        }
    }
}