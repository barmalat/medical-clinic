package com.barmalat.medicalclinic.repository;

import com.barmalat.medicalclinic.model.entities.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
}