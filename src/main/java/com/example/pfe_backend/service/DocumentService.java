package com.example.pfe_backend.service;


import com.example.pfe_backend.controller.DocumentNotFoundException;
import com.example.pfe_backend.model.Contrat;
import com.example.pfe_backend.model.Document;
import com.example.pfe_backend.repository.ContratRepository;
import com.example.pfe_backend.repository.DocumentRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentService {
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private DocumentRepository documentRepository;

    public DocumentService(ContratRepository contratRepository, DocumentRepository documentRepository) {
        this.contratRepository = contratRepository;
        this.documentRepository = documentRepository;
    }

    public String storeFile(Long contratId, MultipartFile file) throws IOException {
        Contrat contrat = contratRepository.findById(contratId)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = FilenameUtils.getExtension(fileName);
        String newFileName = UUID.randomUUID().toString() + "." + fileExtension;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(newFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Document document = new Document();
        document.setNom(fileName);
        document.setType(file.getContentType());
        document.setCheminStockage(filePath.toString());
        document.setDateUpload(LocalDate.now());
        document.setContrat(contrat);

        documentRepository.save(document);

        return filePath.toString();
    }

    public List<Document> getDocumentsByContrat(Long contratId) {
        return documentRepository.findByContratId(contratId);
    }

    public Optional<Document> getDocument(Long id) {
        return documentRepository.findById(id);
    }


    public void deleteDocument(Long documentId) throws IOException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document non trouvé"));

        // Supprimer le fichier physique
        Path filePath = Paths.get(document.getCheminStockage());
        Files.deleteIfExists(filePath);

        // Supprimer l'entrée en base
        documentRepository.delete(document);
    }
}
