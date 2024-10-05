package com.example.job_desc_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "employees")
@TypeAlias("PermanentEmployee")
public class PermanentEmployee extends Employee {
    private double salary;
    private String department;


}
