package com.example.job_desc_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "employees")
@TypeAlias("ContractEmployee")
public class ContractEmployee extends Employee {
    private double hourlyRate;
    private LocalDate contractEndDate;

    // Constructors, Getters, Setters
}
