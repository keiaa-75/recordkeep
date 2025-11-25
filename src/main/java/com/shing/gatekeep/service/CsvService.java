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
     * LRN, Surname, FirstName, MiddleInitial, Strand, GradeLevel, Section, Sex
     */
    public int importStudentsFromCsv(MultipartFile file) throws IOException, CsvValidationException, NumberFormatException, Exception {
        
        List<Student> studentsToSave = new ArrayList<>();
        int rowCount = 0;
        
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) { // Skip header row
            
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                rowCount++;
                System.out.println("Processing row " + rowCount + ": " + java.util.Arrays.toString(line));
                
                // Check if row has at least 8 columns
                if (line.length < 8) {
                    System.err.println("Skipping row " + rowCount + " - insufficient columns: " + line.length);
                    continue; // Skip malformed row
                }
                
                try {
                    Student student = new Student();
                    student.setLrn(line[0].trim());
                    student.setSurname(line[1].trim());
                    student.setFirstName(line[2].trim());
                    student.setMiddleInitial(line[3].trim());
                    student.setStrand(line[4].trim());
                    
                    try {
                        student.setGradeLevel(Integer.parseInt(line[5].trim()));
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping row " + rowCount + " with invalid Grade Level: [" + line[5] + "]");
                        continue; 
                    }
                    
                    student.setSection(line[6].trim());
                    student.setSex(line[7].trim());
                    
                    System.out.println("Successfully created student: " + student.getLrn());
                    studentsToSave.add(student);
                    
                } catch (Exception e) {
                    System.err.println("Error processing row " + rowCount + ": " + e.getMessage());
                    e.printStackTrace();
                    continue;
                }
            }
        }
        
        System.out.println("Total rows processed: " + rowCount + ", Valid students: " + studentsToSave.size());
        
        // If all rows were skipped (e.g., all had "Grade 11"), this list will be empty
        if (studentsToSave.isEmpty()) {
            throw new Exception("No valid student records found in the file. Processed " + rowCount + " rows.");
        }
        
        studentRepository.saveAll(studentsToSave);
        return studentsToSave.size();
    }
}
