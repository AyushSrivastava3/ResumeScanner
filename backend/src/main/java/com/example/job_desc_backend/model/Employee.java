package com.example.job_desc_backend.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "employees")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PermanentEmployee.class, name = "permanent"),
        @JsonSubTypes.Type(value = ContractEmployee.class, name = "contract")
})
public abstract class Employee {
    @Id
    private String id;
    private String name;
    private String email;
    private String phone;
    private LocalDate joinDate;
    private String employeeType;
    private String jobTitle;


}
