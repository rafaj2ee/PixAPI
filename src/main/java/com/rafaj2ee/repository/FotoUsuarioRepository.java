package com.rafaj2ee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rafaj2ee.model.FotoUsuario;

public interface FotoUsuarioRepository extends JpaRepository<FotoUsuario, Long> {
    List<FotoUsuario> findByIdUsuario(Long idUsuario);
}
