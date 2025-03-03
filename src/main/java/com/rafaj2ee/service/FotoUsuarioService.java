package com.rafaj2ee.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.rafaj2ee.model.FotoUsuario;
import com.rafaj2ee.repository.FotoUsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FotoUsuarioService {

    private final FotoUsuarioRepository repository;
    private final BiometricService biometricService;

    public FotoUsuario create(FotoUsuario foto) {
        return repository.save(foto);
    }

    public FotoUsuario getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto não encontrada"));
    }

    public List<FotoUsuario> getAllByUsuario(Long idUsuario) {
        return repository.findByIdUsuario(idUsuario);
    }

    public FotoUsuario update(Long id, FotoUsuario fotoAtualizada) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setFoto(fotoAtualizada.getFoto());
                    existing.setAtivo(fotoAtualizada.getAtivo());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public boolean compararBiometria(Long idFotoRegistrada, byte[] novaFoto) {
        byte[] fotoRegistrada = getById(idFotoRegistrada).getFoto();
        return biometricService.compararFaces(fotoRegistrada, novaFoto);
    }
}
