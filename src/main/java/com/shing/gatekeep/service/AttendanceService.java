package com.shing.gatekeep.service;

import com.shing.gatekeep.model.AttendanceRecord;
import com.shing.gatekeep.model.Student;
import com.shing.gatekeep.repository.AttendanceRepository;
import com.shing.gatekeep.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private StudentRepository studentRepository; // Student Info Repo

    @Autowired
    private AttendanceRepository attendanceRepository; // Attendance Log Repo
    
    public String recordAttendance(String lrn) {
        
        Optional<Student> studentOpt = studentRepository.findById(lrn); 
        if (studentOpt.isEmpty()) {
            return "Error: Student with LRN " + lrn + " not found.";
        }

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<AttendanceRecord> todaysRecords = attendanceRepository.findByLrnAndAttendanceTimeBetween(
                lrn, startOfDay, endOfDay);
        
        if (!todaysRecords.isEmpty()) {
            Student student = studentOpt.get();
            return "Warning: " + student.getFirstName() + " " + student.getSurname() + " has already scanned today.";
        }
        
        AttendanceRecord newRecord = new AttendanceRecord(lrn, LocalDateTime.now());
        attendanceRepository.save(newRecord);
        
        Student student = studentOpt.get();
        return "Success: Attendance recorded for " 
               + student.getFirstName() + " " 
               + student.getSurname() + ".";
    }
}