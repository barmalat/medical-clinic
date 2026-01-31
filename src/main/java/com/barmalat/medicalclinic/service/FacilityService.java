package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.exception.FacilityNotFoundException;
import com.barmalat.medicalclinic.mapper.FacilityMapper;
import com.barmalat.medicalclinic.model.commands.CreateFacilityCommand;
import com.barmalat.medicalclinic.model.dtos.FacilityDto;
import com.barmalat.medicalclinic.model.entities.Facility;
import com.barmalat.medicalclinic.repository.FacilityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;

    public Page<Facility> findAll(Pageable pageable) {
        return facilityRepository.findAll(pageable);
    }

    public Facility findById(Long facilityId) {
        return facilityRepository.findById(facilityId)
                .orElseThrow(() -> new FacilityNotFoundException("Nie znaleziono placówki o wskazanym ID."));
    }

    @Transactional
    public Facility addFacility(CreateFacilityCommand createFacilityCommand) {
    return facilityRepository.save(facilityMapper.toEntity(createFacilityCommand));
    }

    @Transactional
    public Facility deleteById(Long facilityId) {
        Facility facility = findById(facilityId);
        facilityRepository.delete(facility);
        return facility;
    }

    @Transactional
    public Facility updateById(Long facilityId, FacilityDto facilityDto) {
        Facility facility = findById(facilityId);
        facility.updatePublicData(facilityDto);
        return facilityRepository.save(facility);
    }
}