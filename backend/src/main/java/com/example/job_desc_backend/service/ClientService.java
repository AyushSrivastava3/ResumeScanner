package com.example.job_desc_backend.service;

import com.example.job_desc_backend.model.Client;
import com.example.job_desc_backend.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getClientsCreatedToday() {
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);

        return clientRepository.findClientAddedToday(todayStart);

    }

    public List<Client> getClientsCreatedInWeek() {
        LocalDateTime weekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);
        LocalDateTime now = LocalDateTime.now();
        List<Client> clients= clientRepository.findClientWithinDateRange(weekAgo,now);
        return clients;
    }

    public void deleteClientById(String id){
        if(clientRepository.existsById(id)){
            clientRepository.deleteById(id);
        }else {
            // throw new ClientNotFoundException("Client with id " + id + " not found");
        }
    }
}
