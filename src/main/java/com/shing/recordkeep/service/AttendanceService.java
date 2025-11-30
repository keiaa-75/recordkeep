package com.shing.recordkeep.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shing.recordkeep.model.AttendanceRecord;
import com.shing.recordkeep.model.Student;
import com.shing.recordkeep.repository.AttendanceRepository;
import com.shing.recordkeep.repository.StudentRepository;

@Service
public class AttendanceService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;
    
    public Map<String, Object> recordAttendance(String lrn) {
        Map<String, Object> result = new HashMap<>();

        Optional<Student> studentOpt = studentRepository.findById(lrn); 
        if (studentOpt.isEmpty()) {
            result.put("message", "Error: Student with LRN " + lrn + " not found.");
            result.put("attendanceRecord", null);
            result.put("student", null);
            return result;
        }
        
        Student student = studentOpt.get();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<AttendanceRecord> todaysRecords = attendanceRepository.findByLrnAndAttendanceTimeBetween(
                lrn, startOfDay, endOfDay);
        
        if (!todaysRecords.isEmpty()) {
            result.put("message", "Warning: " + student.getFirstName() + " " + student.getSurname() + " has already scanned today.");
            result.put("attendanceRecord", todaysRecords.get(0));
            result.put("student", student);
            return result;
        }
        
        AttendanceRecord newRecord = new AttendanceRecord(lrn, LocalDateTime.now());
        newRecord.setSection(student.getSection());
        attendanceRepository.save(newRecord);
        
        result.put("message", "Success: Attendance recorded for " 
               + student.getFirstName() + " " 
               + student.getSurname() + ".");
        result.put("attendanceRecord", newRecord);
        result.put("student", student);
        return result;
    }

    public List<AttendanceRecord> getFilteredAttendanceRecords(LocalDate date, String searchStrand, Integer searchGrade, String searchSection, String searchSex) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        
        List<AttendanceRecord> records = attendanceRepository.findByAttendanceTimeBetween(startOfDay, endOfDay);
        
        List<Student> allStudents = studentRepository.findAll();
        Map<String, Student> studentMap = allStudents.stream()
                .collect(Collectors.toMap(Student::getLrn, student -> student));

        return records.stream()
            .map(record -> {
                Student student = studentMap.get(record.getLrn());
                if (student != null) {
                    record.setSurname(student.getSurname());
                    record.setFirstName(student.getFirstName());
                    record.setSex(student.getSex());
                }
                return record;
            })
            .filter(record -> searchStrand == null || searchStrand.isEmpty() || (record.getSection() != null && record.getSection().getStrand().equalsIgnoreCase(searchStrand)))
            .filter(record -> searchGrade == null || (record.getSection() != null && record.getSection().getGradeLevel().equals(searchGrade)))
            .filter(record -> searchSection == null || searchSection.isEmpty() || (record.getSection() != null && record.getSection().getSectionName().equalsIgnoreCase(searchSection)))
            .filter(record -> searchSex == null || searchSex.isEmpty() || (record.getSex() != null && record.getSex().equalsIgnoreCase(searchSex)))
            .collect(Collectors.toList());
    }

    public List<String> getStrandOptions() {
        return List.of("STEM", "TVL", "HUMSS", "ABM", "GAS");
    }

    public List<Integer> getGradeOptions() {
        return List.of(11, 12);
    }

    public List<String> getSectionOptions() {
        return studentRepository.findAll().stream()
            .map(student -> student.getSection().getSectionName())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}
