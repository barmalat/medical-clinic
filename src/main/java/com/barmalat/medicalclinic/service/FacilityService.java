package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.exception.FacilityNotFoundException;
import com.barmalat.medicalclinic.mapper.FacilityMapper;
import com.barmalat.medicalclinic.model.commands.CreateFacilityCommand;
import com.barmalat.medicalclinic.model.dtos.FacilityDto;
import com.barmalat.medicalclinic.model.entities.Facility;
import com.barmalat.medicalclinic.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;

    public List<Facility> findAll() {
        return facilityRepository.findAll();
    }

    public Facility findById(Long facilityId) {
        return facilityRepository.findById(facilityId)
                .orElseThrow(() -> new FacilityNotFoundException("Nie znaleziono placówki o wskazanym ID."));
    }

    public Facility addFacility(CreateFacilityCommand createFacilityCommand) {
    return facilityRepository.save(facilityMapper.createFacilityCommandToEntity(createFacilityCommand));
    }

    public Facility deleteById(Long facilityId) {
        Facility facilityToDelete = findById(facilityId);
        facilityRepository.delete(facilityToDelete);
        return facilityToDelete;
    }

    public Facility updateById(Long facilityId, FacilityDto facilityDto) {
        Facility facilityToUpdate = findById(facilityId);
        facilityToUpdate.updateFacilityPublicData(facilityDto);
        return facilityRepository.save(facilityToUpdate);
    }
}