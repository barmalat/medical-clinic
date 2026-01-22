package com.barmalat.medicalclinic.mapper;

import com.barmalat.medicalclinic.model.commands.CreatePatientCommand;
import com.barmalat.medicalclinic.model.entities.Patient;
import com.barmalat.medicalclinic.model.dtos.PatientDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    Patient dtoToEntity(PatientDto patientDto);
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    PatientDto entityToDto(Patient patient);
    Patient createPatientCommandToEntity(CreatePatientCommand createPatientCommand);
}