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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final FacilityRepository facilityRepository;

    public Page<Doctor> findAll(Pageable pageable) {
        return doctorRepository.findAll(pageable);
    }

    @Transactional
    public Doctor addDoctor(CreateDoctorCommand createDoctorCommand) {
        User user = new User(null, createDoctorCommand.firstName(), createDoctorCommand.lastName(), null, null);
        Doctor doctor = doctorMapper.toEntity(createDoctorCommand);
        doctor.setUser(user);
        return doctorRepository.save(doctor);
    }

    public Doctor findById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Nie znaleziono doktora o wskazanym ID."));
    }

    @Transactional
    public Doctor deleteById(Long doctorId) {
        Doctor doctor = findById(doctorId);
        doctorRepository.delete(doctor);
        return doctor;
    }

    @Transactional
    public Doctor updateById(Long doctorId, DoctorDto doctorDto) {
        Doctor doctor = findById(doctorId);
        doctor.updatePublicData(doctorDto);
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor addFacilityById(Long doctorId, Long facilityId) {
        Doctor doctor = findById(doctorId);
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new FacilityNotFoundException("Nie znaleziono placówki o podanym ID."));
        doctor.getFacilities().add(facility);
        return doctorRepository.save(doctor);
    }
}