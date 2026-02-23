package com.barmalat.medicalclinic.mapper;

import com.barmalat.medicalclinic.model.commands.CreateVisitCommand;
import com.barmalat.medicalclinic.model.dtos.VisitDto;
import com.barmalat.medicalclinic.model.entities.Visit;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {DoctorMapper.class, PatientMapper.class}
)

public interface VisitMapper {
    VisitDto toDto(Visit visit);
}