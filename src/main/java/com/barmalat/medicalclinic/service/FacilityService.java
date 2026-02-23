package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.exception.FacilityNotFoundException;
import com.barmalat.medicalclinic.mapper.FacilityMapper;
import com.barmalat.medicalclinic.model.commands.CreateFacilityCommand;
import com.barmalat.medicalclinic.model.dtos.FacilityDto;
import com.barmalat.medicalclinic.model.entities.Facility;
import com.barmalat.medicalclinic.repository.FacilityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;

    public Page<Facility> findAll(Pageable pageable) {
        log.info("process of finding all facilities started");
        Page<Facility> result = facilityRepository.findAll(pageable);
        log.info("process of finding all facilities finished");
        return result;
    }

    public Facility findById(Long facilityId) {
        log.info("process of finding facility by facilityId:{} started", facilityId);
        Facility result = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new FacilityNotFoundException("Nie znaleziono placówki o wskazanym ID."));
        log.info("process of finding facility by facilityId:{} finished", facilityId);
        return result;
    }

    @Transactional
    public Facility addFacility(CreateFacilityCommand createFacilityCommand) {
        log.info("process of creating new facility started");
        Facility result = facilityRepository.save(facilityMapper.toEntity(createFacilityCommand));
        log.info("process of creating new facility finished");
        return result;
    }

    @Transactional
    public Facility deleteById(Long facilityId) {
        log.info("process of deleting facility by facilityId:{} started", facilityId);
        Facility facility = findById(facilityId);
        facilityRepository.delete(facility);
        log.info("process of deleting facility by facilityId:{} finished", facilityId);
        return facility;
    }

    @Transactional
    public Facility updateById(Long facilityId, FacilityDto facilityDto) {
        log.info("process of updating facility by facilityId:{} started", facilityId);
        Facility facility = findById(facilityId);
        facility.updatePublicData(facilityDto);
        Facility result = facilityRepository.save(facility);
        log.info("process of updating facility by facilityId:{} finished", facilityId);
        return result;
    }
}