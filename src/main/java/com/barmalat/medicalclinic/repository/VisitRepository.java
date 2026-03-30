package com.barmalat.medicalclinic.repository;

import com.barmalat.medicalclinic.model.entities.Visit;
import com.barmalat.medicalclinic.model.entities.VisitStatus;
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

    Page<Visit> findByDoctorIdAndStatus(Long doctorId, VisitStatus status, Pageable pageable);

    Page<Visit> findByStatusAndDoctorSpecializationAndStartTimeBetween(
            VisitStatus status,
            String specialization,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    Page<Visit> findByDoctorSpecializationAndStartTimeBetween(
            String specialization,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    Page<Visit> findByDoctorId(Long doctorId, Pageable pageable);

    Page<Visit> findByStatusAndStartTimeBetween(
            VisitStatus status,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );
}