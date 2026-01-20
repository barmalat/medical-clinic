package com.barmalat.medicalclinic.model;

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
    private long id;
    private String email;
    private String password;
    private String idCardNo;
    private String phoneNumber;
    private String birthday;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;

    public void updatePatientPublicData(PatientDto patient) {
        email = patient.getEmail();
        idCardNo = patient.getIdCardNo();
        phoneNumber = patient.getPhoneNumber();
        birthday = patient.getBirthday();
        if (patient.getFirstName() != null || patient.getLastName() != null) {
            user.setFirstName(patient.getFirstName());
            user.setLastName(patient.getLastName());
        }
    }
}