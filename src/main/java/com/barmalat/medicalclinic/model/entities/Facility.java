package com.barmalat.medicalclinic.model.entities;

import com.barmalat.medicalclinic.model.dtos.FacilityDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "FACILITY")
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="FACILITY_NAME", unique=true)
    private String name;
    private String city;
    private String postalCode;
    private String street;
    private String streetNumber;
    @ToString.Exclude
    @ManyToMany(mappedBy = "facilities")
    private List<Doctor> doctors;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Facility)) return false;
        Facility other = (Facility) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void updateFacilityPublicData(FacilityDto facility) {
        name = facility.getName();
        city = facility.getCity();
        postalCode = facility.getPostalCode();
        street = facility.getStreet();
        streetNumber = facility.getStreetNumber();
    }
}