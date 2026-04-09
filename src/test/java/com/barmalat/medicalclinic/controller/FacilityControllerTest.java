package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.model.commands.CreateFacilityCommand;
import com.barmalat.medicalclinic.model.dtos.FacilityDto;
import com.barmalat.medicalclinic.model.entities.Facility;
import com.barmalat.medicalclinic.service.FacilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FacilityControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    FacilityService facilityService;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    JwtDecoder jwtDecoder;

    @Test
    void findAll_DataCorrect_PageFacilityDtoReturn() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Facility> page = new PageImpl<>(List.of(
                new Facility(1L, "nam", "cit", "pos", "str", "strNo", null),
                new Facility(2L, "nam", "cit", "pos", "str", "strNo", null)
        ));
        when(facilityService.findAll(pageable)).thenReturn(page);
        mockMvc.perform(MockMvcRequestBuilders.get("/facilities?page=0&size=5")
                        .with(jwt().authorities(new SimpleGrantedAuthority("read:facility"))))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L));
    }

    @Test
    void findById_DataCorrect_FacilityDtoReturn() throws Exception {
        Long facilityId = 1L;
        Facility facility = new Facility(1L, "nam", "cit", "pos", "str", "strNo", null);
        when(facilityService.findById(facilityId)).thenReturn(facility);
        mockMvc.perform(MockMvcRequestBuilders.get("/facilities/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("read:facility"))))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("nam"))
                .andExpect(jsonPath("$.city").value("cit"))
                .andExpect(jsonPath("$.postalCode").value("pos"))
                .andExpect(jsonPath("$.street").value("str"))
                .andExpect(jsonPath("$.streetNumber").value("strNo"));
    }

    @Test
    void addFacility_DataCorrect_FacilityDtoReturn() throws Exception {
        CreateFacilityCommand command = new CreateFacilityCommand("nam", "cit", "pos", "str", "strNo");
        Facility facility = new Facility(1L, "nam", "cit", "pos", "str", "strNo", null);
        when(facilityService.addFacility(command)).thenReturn(facility);
        mockMvc.perform(MockMvcRequestBuilders.post("/facilities")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().authorities(new SimpleGrantedAuthority("create:facility"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("nam"))
                .andExpect(jsonPath("$.city").value("cit"))
                .andExpect(jsonPath("$.postalCode").value("pos"))
                .andExpect(jsonPath("$.street").value("str"))
                .andExpect(jsonPath("$.streetNumber").value("strNo"));
    }

    @Test
    void addFacility_BlankEmail_MethodArgumentNotValidExceptionThrown() throws Exception {
        CreateFacilityCommand command = new CreateFacilityCommand("   ", "cit", "pos", "str", "strNo");
        mockMvc.perform(MockMvcRequestBuilders.post("/facilities")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().authorities(new SimpleGrantedAuthority("create:facility"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.errors.name").value(hasItem("name is mandatory")));
        verifyNoInteractions(facilityService);
    }

    @Test
    void deleteById_DataCorrect_FacilityDtoReturn() throws Exception {
        Long facilityId = 1L;
        Facility facility = new Facility(1L, "nam", "cit", "pos", "str", "strNo", null);
        when(facilityService.deleteById(facilityId)).thenReturn(facility);
        mockMvc.perform(MockMvcRequestBuilders.delete("/facilities/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("delete:facility"))))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("nam"))
                .andExpect(jsonPath("$.city").value("cit"))
                .andExpect(jsonPath("$.postalCode").value("pos"))
                .andExpect(jsonPath("$.street").value("str"))
                .andExpect(jsonPath("$.streetNumber").value("strNo"));
    }

    @Test
    void updateById_DataCorrect_FacilityDtoReturn() throws Exception {
        Long facilityId = 1L;
        FacilityDto facilityDto = new FacilityDto(1L, "nam", "cit", "pos", "str", "strNo");
        Facility facility = new Facility(1L, "nam", "cit", "pos", "str", "strNo", null);
        when(facilityService.updateById(facilityId, facilityDto)).thenReturn(facility);
        mockMvc.perform(MockMvcRequestBuilders.put("/facilities/1")
                        .content(objectMapper.writeValueAsString(facilityDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().authorities(new SimpleGrantedAuthority("update:facility"))))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("nam"))
                .andExpect(jsonPath("$.city").value("cit"))
                .andExpect(jsonPath("$.postalCode").value("pos"))
                .andExpect(jsonPath("$.street").value("str"))
                .andExpect(jsonPath("$.streetNumber").value("strNo"));
    }

    @Test
    void updateById__BlankEmail_MethodArgumentNotValidExceptionThrown() throws Exception {
        FacilityDto facilityDto = new FacilityDto(1L, "   ", "cit", "pos", "str", "strNo");
        mockMvc.perform(MockMvcRequestBuilders.put("/facilities/1")
                        .content(objectMapper.writeValueAsString(facilityDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().authorities(new SimpleGrantedAuthority("update:facility"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.errors.name").value(hasItem("name is mandatory")));
        verifyNoInteractions(facilityService);
    }
}