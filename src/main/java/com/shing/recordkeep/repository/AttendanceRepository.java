package com.shing.recordkeep.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shing.recordkeep.model.AttendanceRecord;

@Repository
// This manages AttendanceRecord (Key: Long ID)
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {

    // For Report filtering (by time)
    List<AttendanceRecord> findByAttendanceTimeBetween(LocalDateTime start, LocalDateTime end);
        
    // For AttendanceService "One Scan Per Day" logic
    List<AttendanceRecord> findByLrnAndAttendanceTimeBetween(String lrn, LocalDateTime start, LocalDateTime end);
}
