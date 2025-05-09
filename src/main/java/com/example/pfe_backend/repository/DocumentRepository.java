package com.example.pfe_backend.repository;

import com.example.pfe_backend.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByContratId(Long contratId);
}
