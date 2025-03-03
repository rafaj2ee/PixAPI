package com.rafaj2ee.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class FotoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long idUsuario;
    
    @Column(nullable = false)
    private Boolean ativo = true;
    
    @Column(nullable = false, columnDefinition = "BLOB")
    private byte[] foto;
    
    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    // Define a dataCriacao automaticamente antes de salvar no banco
    @PrePersist
    private void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }
}