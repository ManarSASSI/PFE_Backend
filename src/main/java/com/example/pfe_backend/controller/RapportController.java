package com.example.pfe_backend.controller;

import com.example.pfe_backend.service.RapportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/rapports")
public class RapportController {
    @Autowired
    private RapportService rapportService;
    private static final Logger logger = LoggerFactory.getLogger(RapportController.class);


    @GetMapping(value = "/contrats/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> genererRapportContrat(@PathVariable Long id) {
        try {
            byte[] pdf = rapportService.genererRapportContrat(id);

//            // Validation du PDF généré
//            if(pdf == null || pdf.length == 0) {
//                throw new RuntimeException("PDF vide généré");
//            }

            // Validation renforcée
            if (pdf.length < 512) {
                throw new IllegalStateException("PDF invalide ou corrompu");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=rapport-contrat-" + id + ".pdf")
//                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                    .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
                    .body(pdf);

        } catch (Exception e) {
            logger.error("Erreur génération PDF pour contrat ID {} : {}", id, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN) // Type correct pour les erreurs
                    .body("Erreur génération PDF: " + e.getMessage());
        }
    }


//    public ResponseEntity<byte[]> genererRapportContrat(@PathVariable Long id) {
//        try {
//            byte[] pdf = rapportService.genererRapportContrat(id);
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport-contrat-" + id + ".pdf")
//                    .body(pdf);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(("Erreur : " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
//
////            return ResponseEntity.internalServerError().build();
//        }
//    }
}
