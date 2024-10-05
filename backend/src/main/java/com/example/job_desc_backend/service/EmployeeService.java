package com.example.job_desc_backend.service;

import com.example.job_desc_backend.model.Employee;
import com.example.job_desc_backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(String id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(String id, Employee updatedEmployee) {
        if (employeeRepository.existsById(id)) {
            updatedEmployee.setId(id);
            return employeeRepository.save(updatedEmployee);
        } else {
            return null;
        }
    }

    public void deleteEmployee(String id) {
        employeeRepository.deleteById(id);
    }

    public List<Employee> getAllPermanentEmployee() {
        List<Employee> allEmployee=employeeRepository.findAll();
        List<Employee> allPermanentEmployee=new ArrayList<>();
        for (Employee employee:allEmployee){
            if (employee.getEmployeeType().equals("Permanent"))
                allPermanentEmployee.add(employee);
        }
        return allPermanentEmployee;
    }

    public List<Employee> getAllContractEmployee() {
        List<Employee> allEmployee=employeeRepository.findAll();
        List<Employee> allPermanentEmployee=new ArrayList<>();
        for (Employee employee:allEmployee){
            if (employee.getEmployeeType().equals("Contract"))
                allPermanentEmployee.add(employee);
        }
        return allPermanentEmployee;
    }
}
