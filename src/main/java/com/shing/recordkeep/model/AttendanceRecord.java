package com.shing.recordkeep.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String lrn;
    private LocalDateTime attendanceTime;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;
    
    @Transient private String surname;
    @Transient private String firstName;
    @Transient private String sex;

    public AttendanceRecord(String lrn, LocalDateTime attendanceTime) {
        this.lrn = lrn;
        this.attendanceTime = attendanceTime;
    }
}
