package com.barmalat.medicalclinic.model.entities;

import com.barmalat.medicalclinic.model.dtos.DoctorDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String specialization;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @ManyToMany
    @JoinTable(
            name = "doctors_facilities",
            joinColumns = @JoinColumn(name = "doctor_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id", referencedColumnName = "id"))
    private List<Facility> facilities = new ArrayList<>();
    @OneToMany(mappedBy = "doctor")
    private List<Visit> visits = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor)) return false;
        Doctor other = (Doctor) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", specialization='" + specialization + '\'' +
                ", user first name=" + user.getFirstName() +
                ", user last name=" + user.getLastName() +
                ", facilities=" + facilitiesToString() +
                '}';
    }

    private String facilitiesToString() {
        return facilities.stream()
                .map(f -> "Facility[" +
                        "id=" + f.getId() +
                        ", name=" + f.getName() +
                        ", city=" + f.getCity() +
                        ", postalCode=" + f.getPostalCode() +
                        ", street=" + f.getStreet() +
                        ", streetNumber=" + f.getStreetNumber() +
                        "]")
                .collect(Collectors.joining(", ", "[", "]"));
    }

    public void updatePublicData(DoctorDto doctor) {
        email = doctor.email();
        specialization = doctor.specialization();
        if (doctor.firstName() != null || doctor.lastName() != null) {
            user.setFirstName(doctor.firstName());
            user.setLastName(doctor.lastName());
        }
    }
}