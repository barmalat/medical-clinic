package com.barmalat.medicalclinic.service;

import com.barmalat.medicalclinic.exception.DoctorNotFoundException;
import com.barmalat.medicalclinic.exception.FacilityNotFoundException;
import com.barmalat.medicalclinic.mapper.DoctorMapper;
import com.barmalat.medicalclinic.model.commands.CreateDoctorCommand;
import com.barmalat.medicalclinic.model.entities.Doctor;
import com.barmalat.medicalclinic.model.dtos.DoctorDto;
import com.barmalat.medicalclinic.model.entities.Facility;
import com.barmalat.medicalclinic.model.entities.User;
import com.barmalat.medicalclinic.repository.DoctorRepository;
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
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final FacilityRepository facilityRepository;

    public Page<Doctor> findAll(String specialization, Pageable pageable) {
        if (specialization != null) {
            log.info("process of finding doctors by specialization:{} started", specialization);
            Page<Doctor> result = doctorRepository.findBySpecialization(specialization, pageable);
            log.info("process of finding doctors by specialization:{} finished", specialization);
            return result;
        }
        log.info("process of finding all doctors started");
        Page<Doctor> result = doctorRepository.findAll(pageable);
        log.info("process of finding all doctors finished");
        return result;
    }

    @Transactional
    public Doctor addDoctor(CreateDoctorCommand createDoctorCommand) {
        log.info("process of creating new doctor started");
        User user = new User(null, createDoctorCommand.firstName(), createDoctorCommand.lastName(), null, null);
        Doctor doctor = doctorMapper.toEntity(createDoctorCommand);
        doctor.setUser(user);
        Doctor result = doctorRepository.save(doctor);
        log.info("process of creating new doctor finished");
        return result;
    }

    public Doctor findById(Long doctorId) {
        log.info("process of finding doctor by doctorId:{} started", doctorId);
        Doctor result = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Nie znaleziono doktora o wskazanym ID."));
        log.info("process of finding doctor by doctorId:{} finished", doctorId);
        return result;
    }

    @Transactional
    public Doctor deleteById(Long doctorId) {
        log.info("process of deleting doctor by doctorId:{} started", doctorId);
        Doctor doctor = findById(doctorId);
        doctorRepository.delete(doctor);
        log.info("process of deleting doctor by doctorId:{} finished", doctorId);
        return doctor;
    }

    @Transactional
    public Doctor updateById(Long doctorId, DoctorDto doctorDto) {
        log.info("process of updating doctor by doctorId:{} started", doctorId);
        Doctor doctor = findById(doctorId);
        doctor.updatePublicData(doctorDto);
        Doctor result = doctorRepository.save(doctor);
        log.info("process of updating doctor by doctorId:{} finished", doctorId);
        return result;
    }

    @Transactional
    public Doctor addFacilityById(Long doctorId, Long facilityId) {
        log.info("process of updating doctor facilities by doctorId:{} started", doctorId);
        Doctor doctor = findById(doctorId);
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new FacilityNotFoundException("Nie znaleziono placówki o podanym ID."));
        doctor.getFacilities().add(facility);
        Doctor result = doctorRepository.save(doctor);
        log.info("process of updating doctor facilities by doctorId:{} finished", doctorId);
        return result;
    }
}