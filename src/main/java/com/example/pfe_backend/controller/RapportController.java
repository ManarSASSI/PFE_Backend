package com.example.pfe_backend.controller;

import com.example.pfe_backend.service.RapportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/rapports")
public class RapportController {
    @Autowired
    private RapportService rapportService;

    @GetMapping(value = "/contrats/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> genererRapportContrat(@PathVariable Long id) {
        try {
            byte[] pdf = rapportService.genererRapportContrat(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport-contrat-" + id + ".pdf")
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erreur : " + e.getMessage()).getBytes(StandardCharsets.UTF_8));

//            return ResponseEntity.internalServerError().build();
        }
    }
}
