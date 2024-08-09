package com.example.job_desc_backend.controller;

import com.example.job_desc_backend.model.Client;

import com.example.job_desc_backend.repository.ClientRepository;
import com.example.job_desc_backend.service.ExportImportService;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                .orElseThrow(() -> new RuntimeException("Client not found with id " + id));
        return ResponseEntity.ok(client);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable String id, @RequestBody Client clientDetails) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id " + id));

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

    @GetMapping("/clients/export")
    public ResponseEntity<byte[]> exportClientsToExcel() {
        try {
            List<Client> clients = clientRepository.findAll();
            ExportImportService<Client> exporter = new ExportImportService<>();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            exporter.exportToExcel(clients, baos);

            byte[] excelBytes = baos.toByteArray();
            System.out.println("Excel file size: " + excelBytes.length);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=profiles_export.xlsx");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(("Failed to create Excel file: " + e.getMessage()).getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/clients/import")
    public ResponseEntity<?> importClientsFromCSV(@RequestParam("file") MultipartFile file) {
        try {
            ExportImportService<Client> importer = new ExportImportService<>();
            List<Client> profiles = importer.importFromCSV(file, Client.class);
            clientRepository.saveAll(profiles);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to import CSV file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }


}