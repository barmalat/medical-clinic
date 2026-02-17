package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.model.commands.ChangePatientDataCommand;
import com.barmalat.medicalclinic.model.commands.CreatePatientCommand;
import com.barmalat.medicalclinic.model.dtos.PatientDto;
import com.barmalat.medicalclinic.model.entities.Patient;
import com.barmalat.medicalclinic.model.entities.User;
import com.barmalat.medicalclinic.service.PatientService;
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

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    PatientService patientService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void findAll_DataCorrect_PagePatientDtoReturn() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Patient> page = new PageImpl<>(List.of(
                new Patient(1L, "ema", "pas", "idC", "pho", "bir", null),
                new Patient(2L, "ema", "pas", "idC", "pho", "bir", null),
                new Patient()));
        when(patientService.findAll(pageable)).thenReturn(page);
        mockMvc.perform(MockMvcRequestBuilders.get("/patients?page=0&size=5"))
                .andExpect(jsonPath("$.content[0].id").value(1L));
        verify(patientService, times(1)).findAll(pageable);
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void findByEmail_DataCorrect_PatientDtoReturn() throws Exception {
        String email = "ema";
        Patient patient = new Patient(1L, "ema", "pas", "idC", "pho", "bir", null);
        when(patientService.findByEmail(email)).thenReturn(patient);
        mockMvc.perform(MockMvcRequestBuilders.get("/patients/ema"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("ema"))
                .andExpect(jsonPath("$.idCardNo").value("idC"))
                .andExpect(jsonPath("$.phoneNumber").value("pho"))
                .andExpect(jsonPath("$.birthday").value("bir"))
                .andExpect(jsonPath("$.firstName").isEmpty())
                .andExpect(jsonPath("$.lastName").isEmpty());
        verify(patientService, times(1)).findByEmail(email);
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void addPatient_DataCorrect_PatientDtoReturn() throws Exception {
        Patient patient = new Patient(1L, "ema@pl", "pas", "idC", "pho", "bir", new User(1L, "bar", "malat", null, null));
        CreatePatientCommand command = new CreatePatientCommand("ema@pl", "pas", "idC", "bar", "malat", "pho", "bir");
        when(patientService.addPatient(any())).thenReturn(patient);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/patients")
                                .content(objectMapper.writeValueAsString(command))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("ema@pl"))
                .andExpect(jsonPath("$.idCardNo").value("idC"))
                .andExpect(jsonPath("$.phoneNumber").value("pho"))
                .andExpect(jsonPath("$.birthday").value("bir"))
                .andExpect(jsonPath("$.firstName").value("bar"))
                .andExpect(jsonPath("$.lastName").value("malat"));
        verify(patientService, times(1)).addPatient(command);
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void addPatient_BlankInvalidEmailAndBlankPassword_MethodArgumentNotValidExceptionThrown() throws Exception {
        CreatePatientCommand command = new CreatePatientCommand("   ", "   ", "idC", "bar", "malat", "pho", "bir");
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/patients")
                                .content(objectMapper.writeValueAsString(command))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value(hasItem("email is mandatory")))
                .andExpect(jsonPath("$.email").value(hasItem("invalid email format")))
                .andExpect(jsonPath("$.password").value("password is mandatory"));
        verifyNoInteractions(patientService);
    }

    @Test
    void deleteByEmail_DataCorrect_PatientDtoReturn() throws Exception {
        String email = "ema";
        Patient patient = new Patient(1L, "ema", "pas", "idC", "pho", "bir", new User(1L, "bar", "malat", null, null));
        when(patientService.deleteByEmail(email)).thenReturn(patient);
        mockMvc.perform(MockMvcRequestBuilders.delete("/patients/ema"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("ema"))
                .andExpect(jsonPath("$.idCardNo").value("idC"))
                .andExpect(jsonPath("$.phoneNumber").value("pho"))
                .andExpect(jsonPath("$.birthday").value("bir"))
                .andExpect(jsonPath("$.firstName").value("bar"))
                .andExpect(jsonPath("$.lastName").value("malat"));
        verify(patientService, times(1)).deleteByEmail(email);
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void updateByEmail_DataCorrect_PatientDtoReturn() throws Exception {
        String email = "ema";
        PatientDto patientDto = new PatientDto(1L, "ema", "idC", "fir", "las", "pho", "bir");
        Patient patient = new Patient(1L, "ema", "pas", "idC", "pho", "bir", new User(1L, "fir", "las", null, null));
        when(patientService.updateByEmail(email, patientDto)).thenReturn(patient);
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/patients/ema")
                                .content(objectMapper.writeValueAsString(patientDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("ema"))
                .andExpect(jsonPath("$.idCardNo").value("idC"))
                .andExpect(jsonPath("$.phoneNumber").value("pho"))
                .andExpect(jsonPath("$.birthday").value("bir"))
                .andExpect(jsonPath("$.firstName").value("fir"))
                .andExpect(jsonPath("$.lastName").value("las"));
        verify(patientService, times(1)).updateByEmail(email, patientDto);
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void updateByEmail_BlankEmail_MethodArgumentNotValidExceptionThrown() throws Exception {
        PatientDto patientDto = new PatientDto(1L, "   ", "idC", "fir", "las", "pho", "bir");
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/patients/ema")
                                .content(objectMapper.writeValueAsString(patientDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("email is mandatory"));
        verifyNoInteractions(patientService);
    }

    @Test
    void updatePasswordByEmail_DataCorrect_PatientUpdated() throws Exception {
        String email = "ema";
        ChangePatientDataCommand command = new ChangePatientDataCommand("pas");
        doNothing().when(patientService).updatePasswordByEmail(email, command);
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/patients/ema/password")
                                .content(objectMapper.writeValueAsString(command))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void updatePasswordByEmail_BlankEmail_MethodArgumentNotValidExceptionThrown() throws Exception {
        ChangePatientDataCommand command = new ChangePatientDataCommand("   ");
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/patients/ema/password")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("password is mandatory"));
        verifyNoInteractions(patientService);
    }
}