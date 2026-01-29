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

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "FACILITY")
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "FACILITY_NAME", unique = true)
    private String name;
    private String city;
    private String postalCode;
    private String street;
    private String streetNumber;
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

    @Override
    public String toString() {
        return "Facility{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", street='" + street + '\'' +
                ", streetNumber='" + streetNumber + '\'' +
                ", doctors=" + doctorsToString() +
                '}';
    }

    private String doctorsToString() {
        return doctors.stream()
                .map(d -> "Doctor[" +
                        "id=" + d.getId() +
                        ", email=" + d.getEmail() +
                        ", specialization=" + d.getSpecialization() +
                        ", user first name=" + d.getUser().getFirstName() +
                        ", user last name=" + d.getUser().getLastName())
                .collect(Collectors.joining(", ", "[", "]"));
    }

    public void updatePublicData(FacilityDto facility) {
        name = facility.name();
        city = facility.city();
        postalCode = facility.postalCode();
        street = facility.street();
        streetNumber = facility.streetNumber();
    }
}