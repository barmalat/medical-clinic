package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.mapper.DoctorMapper;
import com.barmalat.medicalclinic.model.commands.CreateDoctorCommand;
import com.barmalat.medicalclinic.model.dtos.DoctorDto;
import com.barmalat.medicalclinic.model.entities.Doctor;
import com.barmalat.medicalclinic.model.entities.Facility;
import com.barmalat.medicalclinic.model.entities.User;
import com.barmalat.medicalclinic.model.entities.Visit;
import com.barmalat.medicalclinic.repository.DoctorRepository;
import com.barmalat.medicalclinic.repository.FacilityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
    }

    @Test
    void addDoctor_DataCorrect_DoctorReturn() {
        //given
        CreateDoctorCommand command = new CreateDoctorCommand(1L, "e", "p", "s", "f", "l");
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
    }

    @Test
    void deleteById_DataCorrect_DoctorReturn() {
        //given
        Long doctorId = 1L;
        Doctor doctor = new Doctor(1L, "e", "p", "s", null, null, null);
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        doNothing().when(doctorRepository).deleteById(doctorId);
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
    }
}