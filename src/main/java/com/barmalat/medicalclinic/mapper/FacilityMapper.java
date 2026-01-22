package com.barmalat.medicalclinic.mapper;

import com.barmalat.medicalclinic.model.commands.CreateFacilityCommand;
import com.barmalat.medicalclinic.model.dtos.FacilityDto;
import com.barmalat.medicalclinic.model.entities.Facility;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    Facility dtoToEntity(FacilityDto facilityDto);
    FacilityDto entityToDto(Facility facility);
    Facility createFacilityCommandToEntity(CreateFacilityCommand createFacilityCommand);
}