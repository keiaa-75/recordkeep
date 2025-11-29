package com.shing.recordkeep.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Student {
    @Id
    @NotBlank(message = "LRN is required")
    @Pattern(regexp = "^\\d{12}$", message = "LRN must be exactly 12 digits")
    private String lrn;

    @NotBlank(message = "Surname is required")
    private String surname;

    @NotBlank(message = "First Name is required")
    private String firstName;
    
    @Size(max = 10)
    private String middleInitial;

    @NotBlank(message = "Sex is required")
    @Pattern(regexp = "^(Male|Female)$", message = "Sex must be either 'Male' or 'Female'")
    private String sex;

    @ManyToOne
    @JoinColumn(name = "section_id")
    @NotNull(message = "Section is required")
    private Section section;
}
