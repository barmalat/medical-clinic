package com.barmalat.medicalclinic.mapper;

import com.barmalat.medicalclinic.model.commands.CreateDoctorCommand;
import com.barmalat.medicalclinic.model.entities.Doctor;
import com.barmalat.medicalclinic.model.dtos.DoctorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    DoctorDto toDto(Doctor doctor);

    Doctor toEntity(CreateDoctorCommand createDoctorCommand);
}