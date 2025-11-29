package com.shing.recordkeep.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    
    @Transient private String surname;
    @Transient private String firstName;
    @Transient private String strand;
    @Transient private Integer gradeLevel;
    @Transient private String section;
    @Transient private String sex;

    public AttendanceRecord(String lrn, LocalDateTime attendanceTime) {
        this.lrn = lrn;
        this.attendanceTime = attendanceTime;
    }
}