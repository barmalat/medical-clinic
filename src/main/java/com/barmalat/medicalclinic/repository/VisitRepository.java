package com.barmalat.medicalclinic.repository;

import com.barmalat.medicalclinic.model.entities.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    boolean existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorId,
            LocalDateTime newEnd,
            LocalDateTime newStart
    );

    Page<Visit> findByPatientId(Long patientId, Pageable pageable);
}