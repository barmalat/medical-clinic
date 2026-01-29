package com.barmalat.medicalclinic.mapper;

import com.barmalat.medicalclinic.model.commands.CreateFacilityCommand;
import com.barmalat.medicalclinic.model.dtos.FacilityDto;
import com.barmalat.medicalclinic.model.entities.Facility;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    FacilityDto toDto(Facility facility);
    Facility toEntity(CreateFacilityCommand createFacilityCommand);
}