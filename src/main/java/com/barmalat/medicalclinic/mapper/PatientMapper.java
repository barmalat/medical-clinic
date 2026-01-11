package com.barmalat.medicalclinic.mapper;

import com.barmalat.medicalclinic.model.CreatePatientCommand;
import com.barmalat.medicalclinic.model.Patient;
import com.barmalat.medicalclinic.model.PatientDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    Patient dtoToEntity(PatientDto patientDto);
    PatientDto entityToDto(Patient patient);
    Patient createPatientCommandToEntity(CreatePatientCommand createPatientCommand);
}