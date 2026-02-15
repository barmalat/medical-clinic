package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.model.commands.CreateVisitCommand;
import com.barmalat.medicalclinic.model.entities.Doctor;
import com.barmalat.medicalclinic.model.entities.Patient;
import com.barmalat.medicalclinic.model.entities.Visit;
import com.barmalat.medicalclinic.service.VisitService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VisitControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private VisitService visitService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void find_DataCorrectWithoutPatientId_PageVisitDtoReturn() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        List<Visit> visits = List.of(
                new Visit(1L, doctor, null, LocalDateTime.of(2026, 2, 15, 10, 0), LocalDateTime.of(2025, 2, 15, 10, 30)),
                new Visit(2L, doctor, null, LocalDateTime.of(2026, 2, 16, 10, 0), LocalDateTime.of(2025, 2, 16, 10, 30))
        );
        Page<Visit> page = new PageImpl<>(visits);
        when(visitService.find(null, pageable)).thenReturn(page);
        mockMvc.perform(MockMvcRequestBuilders.get("/visits?page=0&size=5"))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[0].patient").isEmpty());
    }

    @Test
    void find_DataCorrectWithPatientId_PageVisitDtoReturn() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Long patientId = 8L;
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        Patient patient = new Patient(8L, "e", "p", "i", "ph", "b", null);
        List<Visit> visits = List.of(
                new Visit(1L, doctor, patient, LocalDateTime.of(2026, 2, 15, 10, 0), LocalDateTime.of(2025, 2, 15, 10, 30)),
                new Visit(2L, doctor, patient, LocalDateTime.of(2026, 2, 16, 10, 0), LocalDateTime.of(2025, 2, 16, 10, 30))
        );
        Page<Visit> page = new PageImpl<>(visits);
        when(visitService.find(patientId, pageable)).thenReturn(page);
        mockMvc.perform(MockMvcRequestBuilders.get("/visits?page=0&size=5&patientId=8"))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[0].patient.id").value(8L));
    }

    @Test
    void addVisit_DataCorrect_VisitDtoReturn() throws Exception {
        CreateVisitCommand command = new CreateVisitCommand(null, 1L, LocalDateTime.of(2026, 2, 15, 10, 0),
                LocalDateTime.of(2025, 2, 15, 10, 30));
        Visit visit = new Visit(1L, new Doctor(1L, "ema", "pas", "spe", null, null, null), null,
                LocalDateTime.of(2026, 2, 15, 10, 0), LocalDateTime.of(2025, 2, 15, 10, 30));
        when(visitService.addVisit(command)).thenReturn(visit);
        mockMvc.perform(MockMvcRequestBuilders.post("/visits")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.doctor.id").value(1L))
                .andExpect(jsonPath("$.doctor.email").value("ema"))
                .andExpect(jsonPath("$.doctor.specialization").value("spe"))
                .andExpect(jsonPath("$.startTime").isNotEmpty())
                .andExpect(jsonPath("$.endTime").isNotEmpty())
                .andExpect(jsonPath("$.doctor.firstName").isEmpty())
                .andExpect(jsonPath("$.doctor.lastName").isEmpty())
                .andExpect(jsonPath("$.doctor.facilities").isEmpty())
                .andExpect(jsonPath("$.patient").isEmpty());
    }

    @Test
    void addPatientToVisit_DataCorrect_VisitDtoReturn() throws Exception {
        Long visitId = 1L;
        Long patientId = 8L;
        Visit visit = new Visit(1L, new Doctor(1L, "ema", "pas", "spe", null, null, null),
                new Patient(8L, "e", "p", "i", "ph", "b", null),
                LocalDateTime.of(2026, 2, 15, 10, 0), LocalDateTime.of(2025, 2, 15, 10, 30));
        when(visitService.addPatientToVisit(visitId,patientId)).thenReturn(visit);
        mockMvc.perform(MockMvcRequestBuilders.patch("/visits/1/patient/8"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.doctor.id").value(1L))
                .andExpect(jsonPath("$.doctor.email").value("ema"))
                .andExpect(jsonPath("$.doctor.specialization").value("spe"))
                .andExpect(jsonPath("$.startTime").isNotEmpty())
                .andExpect(jsonPath("$.endTime").isNotEmpty())
                .andExpect(jsonPath("$.doctor.firstName").isEmpty())
                .andExpect(jsonPath("$.doctor.lastName").isEmpty())
                .andExpect(jsonPath("$.doctor.facilities").isEmpty())
                .andExpect(jsonPath("$.patient").isNotEmpty());
    }
}