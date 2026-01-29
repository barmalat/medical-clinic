package com.barmalat.medicalclinic.model.entities;

import com.barmalat.medicalclinic.model.dtos.PatientDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "PATIENT")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String idCardNo;
    private String phoneNumber;
    private String birthday;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public void updatePublicData(PatientDto patient) {
        email = patient.email();
        idCardNo = patient.idCardNo();
        phoneNumber = patient.phoneNumber();
        birthday = patient.birthday();
        if (patient.firstName() != null || patient.lastName() != null) {
            user.setFirstName(patient.firstName());
            user.setLastName(patient.lastName());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Patient other = (Patient) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", idCardNo='" + idCardNo + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", birthday='" + birthday + '\'' +
                ", user first name=" + user.getFirstName() +
                ", user last name=" + user.getLastName() +
                '}';
    }
}