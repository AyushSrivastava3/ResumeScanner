package com.example.job_desc_backend.controller;

import com.example.job_desc_backend.model.Client;
import com.example.job_desc_backend.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client){
        Client savedclient= clientRepository.save(client);
        return ResponseEntity.ok(savedclient);
    }


}