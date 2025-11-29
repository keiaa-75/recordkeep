package com.shing.recordkeep.repository;

import com.shing.recordkeep.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
// This manages AttendanceRecord (Key: Long ID)
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {

    // For Report filtering (by time)
    List<AttendanceRecord> findByAttendanceTimeBetween(LocalDateTime start, LocalDateTime end);
        
    // For AttendanceService "One Scan Per Day" logic
    List<AttendanceRecord> findByLrnAndAttendanceTimeBetween(String lrn, LocalDateTime start, LocalDateTime end);
}