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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final FacilityRepository facilityRepository;

    public Page<Doctor> findAll(Pageable pageable) {
        return doctorRepository.findAll(pageable);
    }

    public Doctor addDoctor(CreateDoctorCommand createDoctorCommand) {
        User user = new User(null, createDoctorCommand.getFirstName(), createDoctorCommand.getLastName(), null, null);
        Doctor doctor = doctorMapper.createDoctorCommandToEntity(createDoctorCommand);
        List<Facility> facilities = new ArrayList<>();
        doctor.setFacilities(facilities);
        doctor.setUser(user);
        return doctorRepository.save(doctor);
    }

    public Doctor findById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Nie znaleziono doktora o wskazanym ID."));
    }

    public Doctor deleteById(Long doctorId) {
        Doctor doctorToDelete = findById(doctorId);
        doctorRepository.delete(doctorToDelete);
        return doctorToDelete;
    }

    public Doctor updateById(Long doctorId, DoctorDto doctorDto) {
        Doctor doctorToUpdate = findById(doctorId);
        doctorToUpdate.updateDoctorPublicData(doctorDto);
        return doctorRepository.save(doctorToUpdate);
    }

    public Doctor addFacilityById(Long doctorId, Long facilityId) {
        Doctor doctor = findById(doctorId);
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new FacilityNotFoundException("Nie znaleziono placówki o podanym ID."));
        doctor.getFacilities().add(facility);
        return doctorRepository.save(doctor);
    }
}