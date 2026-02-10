package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.mapper.FacilityMapper;
import com.barmalat.medicalclinic.model.commands.CreateFacilityCommand;
import com.barmalat.medicalclinic.model.dtos.FacilityDto;
import com.barmalat.medicalclinic.model.entities.Facility;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class FacilityServiceTest {
    FacilityService facilityService;
    FacilityRepository facilityRepository;
    FacilityMapper facilityMapper;

    @BeforeEach
    void setup() {
        this.facilityRepository = Mockito.mock(FacilityRepository.class);
        this.facilityMapper = Mappers.getMapper(FacilityMapper.class);
        this.facilityService = new FacilityService(facilityRepository, facilityMapper);
    }

    @Test
    void findAll_DataCorrect_PageFacilityReturn() {
        //given
        Pageable pageable = PageRequest.of(0, 5);
        List<Facility> facilities = List.of(
                new Facility(1L, "nam", "cit", "pos", "str", "strNo", null),
                new Facility(2L, "nam", "cit", "pos", "str", "strNo", null)
        );
        when(facilityRepository.findAll(pageable)).thenReturn(new PageImpl<>(facilities, pageable, facilities.size()));
        //when
        Page<Facility> result = facilityService.findAll(pageable);
        //then
        Assertions.assertAll(
                () -> assertEquals(2, result.getTotalElements()),
                () -> assertEquals(facilities, result.getContent())
        );
    }

    @Test
    void findById_Data_correct_FacilityReturn() {
        //given
        Long facilityId = 1L;
        Facility facility = new Facility(1L, "nam", "cit", "pos", "str", "strNo", null);
        when(facilityRepository.findById(anyLong())).thenReturn(Optional.of(facility));
        //when
        Facility result = facilityService.findById(facilityId);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("nam", result.getName()),
                () -> assertEquals("cit", result.getCity()),
                () -> assertEquals("pos", result.getPostalCode()),
                () -> assertEquals("str", result.getStreet()),
                () -> assertEquals("strNo", result.getStreetNumber()),
                () -> assertNull(result.getDoctors())
        );
    }

    @Test
    void addFacility_DataCorrect_FacilityReturn() {
        //given
        CreateFacilityCommand command = new CreateFacilityCommand("nam", "cit", "pos", "str", "strNo");
        Facility facility = facilityMapper.toEntity(command);
        facility.setId(1L);
        when(facilityRepository.save(any())).thenReturn(facility);
        //when
        Facility result = facilityService.addFacility(command);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("nam", result.getName()),
                () -> assertEquals("cit", result.getCity()),
                () -> assertEquals("pos", result.getPostalCode()),
                () -> assertEquals("str", result.getStreet()),
                () -> assertEquals("strNo", result.getStreetNumber()),
                () -> assertNull(result.getDoctors())
        );
    }

    @Test
    void deleteById_DataCorrect_FacilityReturn() {
        //given
        Long facilityId = 1L;
        Facility facility = new Facility(1L, "nam", "cit", "pos", "str", "strNo", null);
        when(facilityRepository.findById(anyLong())).thenReturn(Optional.of(facility));
        doNothing().when(facilityRepository).delete(any());
        //when
        Facility result = facilityService.deleteById(facilityId);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("nam", result.getName()),
                () -> assertEquals("cit", result.getCity()),
                () -> assertEquals("pos", result.getPostalCode()),
                () -> assertEquals("str", result.getStreet()),
                () -> assertEquals("strNo", result.getStreetNumber()),
                () -> assertNull(result.getDoctors())
        );
    }

    @Test
    void updateById() {
        //given
        Long facilityId = 1L;
        FacilityDto newData = new FacilityDto(1L, "new", "new", "new", "new", "new");
        Facility facility = new Facility(1L, "nam", "cit", "pos", "str", "strNo", null);
        when(facilityRepository.findById(anyLong())).thenReturn(Optional.of(facility));
        when(facilityRepository.save(any())).thenReturn(facility);
        //when
        Facility result = facilityService.updateById(facilityId, newData);
        //then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("new", result.getName()),
                () -> assertEquals("new", result.getCity()),
                () -> assertEquals("new", result.getPostalCode()),
                () -> assertEquals("new", result.getStreet()),
                () -> assertEquals("new", result.getStreetNumber()),
                () -> assertNull(result.getDoctors())
        );
    }
}