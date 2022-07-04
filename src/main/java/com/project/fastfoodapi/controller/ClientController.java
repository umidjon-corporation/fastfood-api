package com.project.fastfoodapi.controller;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.HumanDto;
import com.project.fastfoodapi.dto.front.HumanFrontDto;
import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.enums.HumanStatus;
import com.project.fastfoodapi.entity.enums.UserType;
import com.project.fastfoodapi.mapper.HumanMapper;
import com.project.fastfoodapi.repository.HumanRepository;
import com.project.fastfoodapi.service.ClientService;
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
                        humanRepository.findByUserTypeEqualsAndStatusIsNot(UserType.CLIENT, HumanStatus.DELETED)
                )
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public HttpEntity<?> getOne(@PathVariable Long id) {
        Optional<Human> optionalHuman = humanRepository.findByStatusIsNotAndId(HumanStatus.DELETED, id);
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
        ApiResponse<HumanFrontDto> apiResponse = clientService.block(id);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/unblock")
    public HttpEntity<?> unblock(@PathVariable Long id) {
        ApiResponse<HumanFrontDto> apiResponse = clientService.unblock(id);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

}
