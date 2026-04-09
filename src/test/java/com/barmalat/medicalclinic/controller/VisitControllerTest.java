package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.model.commands.CreateVisitCommand;
import com.barmalat.medicalclinic.model.entities.Doctor;
import com.barmalat.medicalclinic.model.entities.Patient;
import com.barmalat.medicalclinic.model.entities.Visit;
import com.barmalat.medicalclinic.model.entities.VisitStatus;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
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
    @MockitoBean
    JwtDecoder jwtDecoder;

    @Test
    void find_DataCorrectWithoutPatientId_PageVisitDtoReturn() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Doctor doctor = new Doctor(1L, "ema@pl", "pas", "spe", null, null, null);
        List<Visit> visits = List.of(
                new Visit(1L, doctor, null, LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30), VisitStatus.AVAILABLE),
                new Visit(2L, doctor, null, LocalDateTime.of(2027, 2, 16, 10, 0), LocalDateTime.of(2027, 2, 16, 10, 30), VisitStatus.AVAILABLE)
        );
        Page<Visit> page = new PageImpl<>(visits);
        when(visitService.find(null, null, null, null, null, pageable)).thenReturn(page);
        mockMvc.perform(MockMvcRequestBuilders.get("/visits?page=0&size=5")
                        .with(jwt().authorities(new SimpleGrantedAuthority("read:visit"))))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[0].patient").isEmpty());
    }

    @Test
    void find_DataCorrectWithPatientId_PageVisitDtoReturn() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Long patientId = 8L;
        Doctor doctor = new Doctor(1L, "ema@pl", "pas", "spe", null, null, null);
        Patient patient = new Patient(8L, "e", "p", "i", "ph", "b", null);
        List<Visit> visits = List.of(
                new Visit(1L, doctor, patient, LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30), VisitStatus.RESERVED),
                new Visit(2L, doctor, patient, LocalDateTime.of(2027, 2, 16, 10, 0), LocalDateTime.of(2027, 2, 16, 10, 30), VisitStatus.RESERVED)
        );
        Page<Visit> page = new PageImpl<>(visits);
        when(visitService.find(patientId, null, null, null, null, pageable)).thenReturn(page);
        mockMvc.perform(MockMvcRequestBuilders.get("/visits?page=0&size=5&patientId=8")
                        .with(jwt().authorities(new SimpleGrantedAuthority("read:visit"))))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[0].patient.id").value(8L));
    }

    @Test
    void find_DataCorrectWithDoctorId_PageVisitDtoReturn() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Long doctorId = 1L;
        Doctor doctor = new Doctor(1L, "ema", "pas", "spe", null, null, null);
        List<Visit> visits = List.of(
                new Visit(1L, doctor, null, LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30), VisitStatus.AVAILABLE),
                new Visit(2L, doctor, null, LocalDateTime.of(2027, 2, 16, 10, 0), LocalDateTime.of(2027, 2, 16, 10, 30), VisitStatus.RESERVED)
        );
        Page<Visit> page = new PageImpl<>(visits);
        when(visitService.find(null, doctorId, null, null, null, pageable)).thenReturn(page);
        mockMvc.perform(MockMvcRequestBuilders.get("/visits?page=0&size=5&doctorId=1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("read:visit"))))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$.content[1].status").value("RESERVED"));
    }

    @Test
    void addVisit_DataCorrect_VisitDtoReturn() throws Exception {
        CreateVisitCommand command = new CreateVisitCommand(null, 1L, LocalDateTime.of(2027, 2, 15, 10, 0),
                LocalDateTime.of(2027, 2, 15, 10, 30));
        Visit visit = new Visit(1L, new Doctor(1L, "ema@pl", "pas", "spe", null, null, null), null,
                LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30), VisitStatus.AVAILABLE);
        when(visitService.addVisit(command)).thenReturn(visit);
        mockMvc.perform(MockMvcRequestBuilders.post("/visits")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().authorities(new SimpleGrantedAuthority("create:visit"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.doctor.id").value(1L))
                .andExpect(jsonPath("$.doctor.email").value("ema@pl"))
                .andExpect(jsonPath("$.doctor.specialization").value("spe"))
                .andExpect(jsonPath("$.startTime").isNotEmpty())
                .andExpect(jsonPath("$.endTime").isNotEmpty())
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.doctor.firstName").isEmpty())
                .andExpect(jsonPath("$.doctor.lastName").isEmpty())
                .andExpect(jsonPath("$.doctor.facilities").isEmpty())
                .andExpect(jsonPath("$.patient").isEmpty());
    }

    @Test
    void addVisit_NullDoctorId_MethodArgumentNotValidExceptionThrown() throws Exception {
        CreateVisitCommand command = new CreateVisitCommand(null, null, LocalDateTime.of(2026, 2, 15, 10, 0),
                LocalDateTime.of(2025, 2, 15, 10, 30));
        mockMvc.perform(MockMvcRequestBuilders.post("/visits")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().authorities(new SimpleGrantedAuthority("create:visit"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.errors.doctorId").value(hasItem("doctorId is mandatory")));
        verifyNoInteractions(visitService);
    }

    @Test
    void addPatientToVisit_DataCorrect_VisitDtoReturn() throws Exception {
        Long visitId = 1L;
        Long patientId = 8L;
        Visit visit = new Visit(1L, new Doctor(1L, "ema", "pas", "spe", null, null, null),
                new Patient(8L, "e", "p", "i", "ph", "b", null),
                LocalDateTime.of(2026, 2, 15, 10, 0), LocalDateTime.of(2025, 2, 15, 10, 30), VisitStatus.RESERVED);
        when(visitService.addPatientToVisit(visitId, patientId)).thenReturn(visit);
        mockMvc.perform(MockMvcRequestBuilders.patch("/visits/1/patient/8")
                        .with(jwt().authorities(new SimpleGrantedAuthority("book:visit"))))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.doctor.id").value(1L))
                .andExpect(jsonPath("$.doctor.email").value("ema"))
                .andExpect(jsonPath("$.doctor.specialization").value("spe"))
                .andExpect(jsonPath("$.startTime").isNotEmpty())
                .andExpect(jsonPath("$.endTime").isNotEmpty())
                .andExpect(jsonPath("$.status").value("RESERVED"))
                .andExpect(jsonPath("$.doctor.firstName").isEmpty())
                .andExpect(jsonPath("$.doctor.lastName").isEmpty())
                .andExpect(jsonPath("$.doctor.facilities").isEmpty())
                .andExpect(jsonPath("$.patient").isNotEmpty());
    }

    @Test
    void cancelVisit_DataCorrect_VisitDtoReturn() throws Exception {
        Long visitId = 1L;
        Visit visit = new Visit(1L, new Doctor(1L, "ema", "pas", "spe", null, null, null),
                new Patient(8L, "e", "p", "i", "ph", "b", null),
                LocalDateTime.of(2027, 2, 15, 10, 0), LocalDateTime.of(2027, 2, 15, 10, 30), VisitStatus.CANCELLED);
        when(visitService.cancelVisit(visitId)).thenReturn(visit);
        mockMvc.perform(MockMvcRequestBuilders.patch("/visits/1/cancel")
                        .with(jwt().authorities(new SimpleGrantedAuthority("cancel:visit"))))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.patient").isNotEmpty());
    }
}