package com.shing.gatekeep.service;

import com.shing.gatekeep.model.Student;
import com.shing.gatekeep.repository.StudentRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvService {

    @Autowired
    private StudentRepository studentRepository; // Your Student Info Repo

    /**
     * Reads a CSV file, parses it, and saves the students to the database.
     * Assumes CSV columns are in this exact order:
     * LRN, Surname, FirstName, MiddleInitial, Strand, GradeLevel, Section, ContactNumber
     */
    public int importStudentsFromCsv(MultipartFile file) throws IOException, CsvValidationException, NumberFormatException, Exception {
        
        List<Student> studentsToSave = new ArrayList<>();
        
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) { // Skip header row
            
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                // Check if row has at least 8 columns
                if (line.length < 8) {
                    continue; // Skip malformed row
                }
                
                Student student = new Student();
                student.setLrn(line[0]);
                student.setSurname(line[1]);
                student.setFirstName(line[2]);
                student.setMiddleInitial(line[3]);
                student.setStrand(line[4]);
                
                // This is the most likely failure point:
                try {
                    // Try to convert the text "11" or "12" into a number
                    student.setGradeLevel(Integer.parseInt(line[5]));
                } catch (NumberFormatException e) {
                    // If line[5] is "STEM" or "Grade 11", this fails and skips the row
                    System.err.println("Skipping row with invalid Grade Level: " + line[5]);
                    continue; 
                }
                
                student.setSection(line[6]);
                student.setContactNumber(line[7]);
                
                studentsToSave.add(student);
            }
        }
        
        // If all rows were skipped (e.g., all had "Grade 11"), this list will be empty
        if (studentsToSave.isEmpty()) {
            throw new Exception("No valid student records found in the file.");
        }
        
        studentRepository.saveAll(studentsToSave);
        return studentsToSave.size();
    }
}