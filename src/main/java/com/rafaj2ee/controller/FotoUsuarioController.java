package com.rafaj2ee.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rafaj2ee.model.FotoUsuario;
import com.rafaj2ee.service.FotoUsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/api/v1/fotos")
@RequiredArgsConstructor
public class FotoUsuarioController {

	@Autowired
    private final FotoUsuarioService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> criar(
            @RequestParam("file") MultipartFile novaImagem,
            @ModelAttribute FotoUsuario foto) {
        try {
        	log.info("criar FotoUsuario "+foto);
            foto.setFoto(novaImagem.getBytes());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Erro ao ler imagem"));
        }
        return ResponseEntity.ok().body(service.create(foto));
    }



    @GetMapping("/{id}")
    public FotoUsuario buscarPorId(@PathVariable Long id) {
    	log.info("buscarPorId "+id);
        return service.getById(id);
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<FotoUsuario> buscarPorUsuario(@PathVariable Long idUsuario) {
    	log.info("buscarPorUsuario "+idUsuario);
        return service.getAllByUsuario(idUsuario);
    }

    @GetMapping("/foto/usuario/{idUsuario}")
    public ResponseEntity<String> buscarFotoPorUsuario(@PathVariable Long idUsuario) {
    	log.info("buscarFotoPorUsuario "+idUsuario);
    	String foto = Base64.getEncoder().encodeToString(service.getAllByUsuario(idUsuario).get(0).getFoto());
        // Generate HTML response with copy button
        String htmlResponse = "<html><body>"
            + "<img src='data:image/png;base64," + foto + "' alt='Foto'/>"
            + "</body></html>"; 
    	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return ResponseEntity.ok().headers(headers).body(htmlResponse);
    }
    
    @PutMapping("/{id}")
    public FotoUsuario atualizar(@PathVariable Long id, @RequestBody FotoUsuario foto) {
       	log.info("atualizar id "+id+" foto "+foto);
        return service.update(id, foto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
       	log.info("deletar id "+id);
        service.delete(id);
    }

    @PostMapping("/comparar/{idFotoRegistrada}")
    public ResponseEntity<?> compararBiometria(
            @PathVariable Long idFotoRegistrada,
            @RequestParam("file") MultipartFile novaImagem
    ) {
        try {
           	log.info("compararBiometria idFotoRegistrada "+idFotoRegistrada);
            byte[] novaFoto = novaImagem.getBytes();
            boolean resultado = service.compararBiometria(idFotoRegistrada, novaFoto);
            return ResponseEntity.ok().body(Collections.singletonMap("similar", resultado));
        } catch (IOException e) {
        	log.error(e.getMessage()+e.getCause());
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Erro ao ler imagem"));
        }
    }
}