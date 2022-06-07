package com.project.fastfoodapi.controller;

import com.project.fastfoodapi.entity.Attachment;
import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.enums.ClientStatus;
import com.project.fastfoodapi.repository.HumanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class HomeController {

    final HumanRepository humanRepository;

    @GetMapping("/assets/human/{id}/photo")
    public HttpEntity<?> getPhoto(@PathVariable Long id) {
        Optional<Human> optionalHuman = humanRepository.findByStatusIsNotAndId(ClientStatus.DELETED, id);
        if (optionalHuman.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (optionalHuman.get().getPhoto() == null) {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/api/assets/image-not-found.png")).build();
        }
        Attachment photo = optionalHuman.get().getPhoto();
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(photo.getType()))
                .contentLength(photo.getSize())
                .body(photo.getBytes());
    }
}
