package com.example.job_desc_backend.controller;

import com.example.job_desc_backend.model.Client;
import com.example.job_desc_backend.model.ResourceNotFoundException;
import com.example.job_desc_backend.repository.ClientRepository;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping
    public List<Client> getAllClients(){
        return clientRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable String id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + id));
        return ResponseEntity.ok(client);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable String id, @RequestBody Client clientDetails) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + id));

        client.setClientName(clientDetails.getClientName());
        client.setClientAddress(clientDetails.getClientAddress());
        client.setGstNumber(clientDetails.getGstNumber());
        client.setAccountNumber(clientDetails.getAccountNumber());
        client.setIfscCode(clientDetails.getIfscCode());
        client.setCurrency(clientDetails.getCurrency());

        Client updatedClient = clientRepository.save(client);
        return ResponseEntity.ok(updatedClient);
    }





    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client){
        Client savedclient= clientRepository.save(client);
        return ResponseEntity.ok(savedclient);
    }


}