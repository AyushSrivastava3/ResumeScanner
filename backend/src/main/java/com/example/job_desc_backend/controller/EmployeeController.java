package com.example.job_desc_backend.controller;


import com.example.job_desc_backend.model.ContractEmployee;
import com.example.job_desc_backend.model.Employee;
import com.example.job_desc_backend.model.PermanentEmployee;
import com.example.job_desc_backend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
    @GetMapping("/permanent")
    public List<Employee> getAllPermanentEmployee(){
        return employeeService.getAllPermanentEmployee();
    }

    @GetMapping("/contract")
    public List<Employee> getAllContractEmployee(){
        return employeeService.getAllContractEmployee();
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable String id) {
        return employeeService.getEmployeeById(id);
    }

    @PostMapping
    public Employee createEmployee(@RequestBody Map<String, Object> employeeData) {
        String employeeType = (String) employeeData.get("type");

        if (employeeType.equalsIgnoreCase("permanent")) {
            PermanentEmployee employee = new PermanentEmployee();
            // Set fields from employeeData
            employee.setEmployeeType((String) employeeData.get("type"));
            employee.setName((String) employeeData.get("name"));
            employee.setEmail((String) employeeData.get("email"));
            employee.setPhone((String) employeeData.get("phone"));
            employee.setJoinDate(LocalDate.parse((String) employeeData.get("joinDate")));
            employee.setJobTitle((String) employeeData.get("jobTitle"));

            // Set permanent employee-specific fields
            employee.setSalary(Double.parseDouble(employeeData.get("salary").toString()));
            employee.setDepartment((String) employeeData.get("department"));

            return employeeService.addEmployee(employee);
        } else if (employeeType.equalsIgnoreCase("contract")) {
            ContractEmployee employee = new ContractEmployee();
            employee.setEmployeeType((String) employeeData.get("type"));
            // Set fields from employeeData
            // Set common fields
            employee.setName((String) employeeData.get("name"));
            employee.setEmail((String) employeeData.get("email"));
            employee.setPhone((String) employeeData.get("phone"));
            employee.setJoinDate(LocalDate.parse((String) employeeData.get("joinDate")));
            employee.setJobTitle((String) employeeData.get("jobTitle"));
            // Set contract employee-specific fields
            employee.setHourlyRate(Double.parseDouble(employeeData.get("hourlyRate").toString()));
            employee.setContractEndDate(LocalDate.parse((String) employeeData.get("contractEndDate")));

            return employeeService.addEmployee(employee);
        } else {
            throw new IllegalArgumentException("Invalid employee type");
        }
    }


    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable String id, @RequestBody Map<String, Object> employeeData) {
//        return employeeService.updateEmployee(id, employee);
        String employeeType = (String) employeeData.get("type");

        if (employeeType.equalsIgnoreCase("permanent")) {
            PermanentEmployee employee = new PermanentEmployee();
            // Set fields from employeeData
            employee.setId(id);
            employee.setEmployeeType((String) employeeData.get("type"));
            employee.setName((String) employeeData.get("name"));
            employee.setEmail((String) employeeData.get("email"));
            employee.setPhone((String) employeeData.get("phone"));
            employee.setJoinDate(LocalDate.parse((String) employeeData.get("joinDate")));
            employee.setJobTitle((String) employeeData.get("jobTitle"));

            // Set permanent employee-specific fields
            employee.setSalary(Double.parseDouble(employeeData.get("salary").toString()));
            employee.setDepartment((String) employeeData.get("department"));

            return employeeService.addEmployee(employee);
        } else if (employeeType.equalsIgnoreCase("contract")) {
            ContractEmployee employee = new ContractEmployee();
            employee.setId(id);
            employee.setEmployeeType((String) employeeData.get("type"));
            // Set fields from employeeData
            // Set common fields
            employee.setName((String) employeeData.get("name"));
            employee.setEmail((String) employeeData.get("email"));
            employee.setPhone((String) employeeData.get("phone"));
            employee.setJoinDate(LocalDate.parse((String) employeeData.get("joinDate")));
            employee.setJobTitle((String) employeeData.get("jobTitle"));
            // Set contract employee-specific fields
            employee.setHourlyRate(Double.parseDouble(employeeData.get("hourlyRate").toString()));
            employee.setContractEndDate(LocalDate.parse((String) employeeData.get("contractEndDate")));

            return employeeService.addEmployee(employee);
        } else {
            throw new IllegalArgumentException("Invalid employee type");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
    }
}
