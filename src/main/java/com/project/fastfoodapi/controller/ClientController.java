package com.project.fastfoodapi.controller;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.HumanDto;
import com.project.fastfoodapi.dto.front.HumanFrontDto;
import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.enums.ClientStatus;
import com.project.fastfoodapi.entity.enums.UserType;
import com.project.fastfoodapi.mapper.HumanMapper;
import com.project.fastfoodapi.repository.HumanRepository;
import com.project.fastfoodapi.service.ClientService;
import com.project.fastfoodapi.service.HumanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/client")
public class ClientController {
    final HumanMapper humanMapper;
    final HumanRepository humanRepository;
    final ClientService clientService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public HttpEntity<?> getAll() {
        return ResponseEntity.ok().body(
                humanMapper.humanToHumanFrontDto(
                        humanRepository.findByUserTypeEqualsAndStatusIsNot(UserType.CLIENT, ClientStatus.DELETED)
                )
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public HttpEntity<?> getOne(@PathVariable Long id) {
        Optional<Human> optionalHuman = humanRepository.findByStatusIsNotAndId(ClientStatus.DELETED, id);
        if (optionalHuman.isEmpty() || optionalHuman.get().getUserType() != UserType.CLIENT) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .message("Client with id=(" + id + ") not found")
                    .build());
        }
        return ResponseEntity.ok().body(humanMapper.humanToHumanFrontDto(optionalHuman.get()));
    }

    @PostMapping
    public HttpEntity<?> add(@ModelAttribute HumanDto dto) {
        ApiResponse<?> apiResponse = clientService.add(dto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PutMapping("/{id}")
    public HttpEntity<?> edit(@ModelAttribute HumanDto dto, @PathVariable Long id) {
        ApiResponse<?> apiResponse = clientService.edit(id, dto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

//    TODO client delete kerakmi?

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/block")
    public HttpEntity<?> block(@PathVariable Long id) {
        ApiResponse<Object> apiResponse = clientService.block(id);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @GetMapping("/me")
    public HttpEntity<?> getMe(@AuthenticationPrincipal Human human) {
        return ResponseEntity.ok().body(ApiResponse.<HumanFrontDto>builder()
                .message("Success!")
                .success(true)
                .data(humanMapper.humanToHumanFrontDto(human))
                .build());
    }
}
