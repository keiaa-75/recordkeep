package com.shing.recordkeep.model.dto;

import com.shing.recordkeep.model.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentWithAttendance {
    private Student student;
    private boolean attendedToday;
}
