package com.shing.recordkeep.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.shing.recordkeep.model.Section;
import com.shing.recordkeep.model.Student;
import com.shing.recordkeep.repository.SectionRepository;
import com.shing.recordkeep.repository.StudentRepository;

@Service
public class CsvService {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private SectionRepository sectionRepository;

    public int importStudentsFromCsv(MultipartFile file) throws IOException, CsvValidationException, NumberFormatException, Exception {
        
        List<Student> studentsToSave = new ArrayList<>();
        int rowCount = 0;
        
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
            
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                rowCount++;
                System.out.println("Processing row " + rowCount + ": " + java.util.Arrays.toString(line));
                
                if (line.length < 8) {
                    System.err.println("Skipping row " + rowCount + " - insufficient columns: " + line.length);
                    continue;
                }
                
                try {
                    Student student = new Student();
                    student.setLrn(line[0].trim());
                    student.setSurname(line[1].trim());
                    student.setFirstName(line[2].trim());
                    student.setMiddleInitial(line[3].trim());
                    student.setSex(line[7].trim());
                    
                    Section section = new Section();
                    section.setStrand(line[4].trim());
                    
                    try {
                        section.setGradeLevel(Integer.parseInt(line[5].trim()));
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping row " + rowCount + " with invalid Grade Level: [" + line[5] + "]");
                        continue; 
                    }
                    
                    section.setSectionName(line[6].trim());
                    section = sectionRepository.save(section);
                    student.setSection(section);
                    
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
        
        if (studentsToSave.isEmpty()) {
            throw new Exception("No valid student records found in the file. Processed " + rowCount + " rows.");
        }
        
        studentRepository.saveAll(studentsToSave);
        return studentsToSave.size();
    }
}
