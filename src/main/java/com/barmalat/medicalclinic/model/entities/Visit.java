package com.barmalat.medicalclinic.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "VISIT")
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private Doctor doctor;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private Patient patient;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Visit)) return false;
        Visit other = (Visit) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Visit{" +
                "id=" + id +
                ", doctor firstName=" + doctor.getUser().getFirstName() +
                ", doctor lastName=" + doctor.getUser().getLastName() +
                ", patient firstName=" + patient.getUser().getFirstName() +
                ", patient lastName=" + patient.getUser().getLastName() +
                ", startVisitTime=" + startTime +
                ", endVisitTime=" + endTime +
                '}';
    }
}