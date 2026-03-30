package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.model.commands.CreateDoctorCommand;
import com.barmalat.medicalclinic.model.dtos.DoctorDto;
import com.barmalat.medicalclinic.model.entities.Doctor;
import com.barmalat.medicalclinic.model.entities.Facility;
import com.barmalat.medicalclinic.model.entities.User;
import com.barmalat.medicalclinic.service.DoctorService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    DoctorService doctorService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void findAll_DataCorrectWithoutSpecialization_PageDoctorDtoReturn() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Doctor> page = new PageImpl<>(List.of(
                new Doctor(1L, "ema@pl", "pas", "spe", new User(1L, "fis", "las", null, null), new ArrayList<>(), new ArrayList<>()),
                new Doctor(2L, "ema@pl", "pas", "spe", new User(2L, "fis", "las", null, null), new ArrayList<>(), new ArrayList<>())
        ));
        when(doctorService.findAll(null, pageable)).thenReturn(page);
        mockMvc.perform(MockMvcRequestBuilders.get("/doctors?page=0&size=5"))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L));
    }

    @Test
    void findAll_DataCorrectWithSpecialization_PageDoctorDtoReturn() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        String specialization = "chirurg";
        Page<Doctor> page = new PageImpl<>(List.of(
                new Doctor(1L, "ema@pl", "pas", "chirurg", new User(1L, "fis", "las", null, null), new ArrayList<>(), new ArrayList<>()),
                new Doctor(2L, "ema@pl", "pas", "chirurg", new User(2L, "fis", "las", null, null), new ArrayList<>(), new ArrayList<>())
        ));
        when(doctorService.findAll(specialization, pageable)).thenReturn(page);
        mockMvc.perform(MockMvcRequestBuilders.get("/doctors?page=0&size=5&specialization=chirurg"))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].specialization").value("chirurg"))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[1].specialization").value("chirurg"));
    }


    @Test
    void findById_DataCorrect_DoctorDtoReturn() throws Exception {
        Long doctorId = 1L;
        Doctor doctor = new Doctor(1L, "ema@pl", "pas", "spe", new User(1L, "fis", "las", null, null), new ArrayList<>(), new ArrayList<>());
        when(doctorService.findById(doctorId)).thenReturn(doctor);
        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("ema@pl"))
                .andExpect(jsonPath("$.specialization").value("spe"))
                .andExpect(jsonPath("$.firstName").value("fis"))
                .andExpect(jsonPath("$.lastName").value("las"))
                .andExpect(jsonPath("$.facilities").isEmpty());
    }

    @Test
    void addDoctor_DataCorrect_DoctorDtoReturn() throws Exception {
        CreateDoctorCommand command = new CreateDoctorCommand(null, "ema@pl", "pas", "spe", "fir", "las");
        Doctor doctor = new Doctor(1L, "ema@pl", "pas", "spe", new User(1L, "fis", "las", null, null), new ArrayList<>(), new ArrayList<>());
        when(doctorService.addDoctor(command)).thenReturn(doctor);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/doctors")
                                .content(objectMapper.writeValueAsString(command))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("ema@pl"))
                .andExpect(jsonPath("$.specialization").value("spe"))
                .andExpect(jsonPath("$.firstName").value("fis"))
                .andExpect(jsonPath("$.lastName").value("las"))
                .andExpect(jsonPath("$.facilities").isEmpty());
    }

    @Test
    void addDoctor_BlankInvalidEmailAndBlankPassword_MethodArgumentNotValidExceptionThrown() throws Exception {
        CreateDoctorCommand command = new CreateDoctorCommand(null, "   ", "   ", "spe", "fir", "las");
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/doctors")
                                .content(objectMapper.writeValueAsString(command))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.errors.email").value(hasItem("email is mandatory")))
                .andExpect(jsonPath("$.errors.email").value(hasItem("invalid email format")))
                .andExpect(jsonPath("$.errors.password").value("password is mandatory"));
        verifyNoInteractions(doctorService);
    }

    @Test
    void deleteById_DataCorrect_DoctorDtoReturn() throws Exception {
        Long doctorId = 1L;
        Doctor doctor = new Doctor(1L, "ema@pl", "pas", "spe", new User(1L, "fis", "las", null, null), new ArrayList<>(), new ArrayList<>());
        when(doctorService.deleteById(doctorId)).thenReturn(doctor);
        mockMvc.perform(MockMvcRequestBuilders.delete("/doctors/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("ema@pl"))
                .andExpect(jsonPath("$.specialization").value("spe"))
                .andExpect(jsonPath("$.firstName").value("fis"))
                .andExpect(jsonPath("$.lastName").value("las"))
                .andExpect(jsonPath("$.facilities").isEmpty());
    }

    @Test
    void updateById_DataCorrect_DoctorDtoReturn() throws Exception {
        Long doctorId = 1L;
        DoctorDto doctorDto = new DoctorDto(1L, "ema@pl", "spe", "fir", "las", null);
        Doctor doctor = new Doctor(1L, "ema@pl", "pas", "spe", new User(1L, "fis", "las", null, null), new ArrayList<>(), new ArrayList<>());
        when(doctorService.updateById(doctorId, doctorDto)).thenReturn(doctor);
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/doctors/1")
                                .content(objectMapper.writeValueAsString(doctorDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("ema@pl"))
                .andExpect(jsonPath("$.specialization").value("spe"))
                .andExpect(jsonPath("$.firstName").value("fis"))
                .andExpect(jsonPath("$.lastName").value("las"))
                .andExpect(jsonPath("$.facilities").isEmpty());
    }

    @Test
    void updateById_BlankInvalidEmail_MethodArgumentNotValidExceptionThrown() throws Exception {
        DoctorDto doctorDto = new DoctorDto(1L, "  ", "spe", "fir", "las", null);
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/doctors/1")
                                .content(objectMapper.writeValueAsString(doctorDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.errors.email").value(hasItem("email is mandatory")))
                .andExpect(jsonPath("$.errors.email").value(hasItem("invalid email format")));
        verifyNoInteractions(doctorService);
    }

    @Test
    void addFacilityById_DataCorrect_DoctorDtoReturn() throws Exception {
        Long doctorId = 1L;
        Long facilityId = 1L;
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", new User(1L, "fis", "las", null, null),
                List.of(new Facility(1L, "salve", "lodz", "postalCode", "street", "streetNo", null)), new ArrayList<>());
        when(doctorService.addFacilityById(doctorId, facilityId)).thenReturn(doctor);
        mockMvc.perform(MockMvcRequestBuilders.patch("/doctors/1/facility/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("ema"))
                .andExpect(jsonPath("$.specialization").value("spe"))
                .andExpect(jsonPath("$.firstName").value("fis"))
                .andExpect(jsonPath("$.lastName").value("las"))
                .andExpect(jsonPath("$.facilities[0].id").value(1L))
                .andExpect(jsonPath("$.facilities[0].name").value("salve"))
                .andExpect(jsonPath("$.facilities[0].city").value("lodz"))
                .andExpect(jsonPath("$.facilities[0].postalCode").value("postalCode"))
                .andExpect(jsonPath("$.facilities[0].street").value("street"))
                .andExpect(jsonPath("$.facilities[0].streetNumber").value("streetNo"));
    }
}