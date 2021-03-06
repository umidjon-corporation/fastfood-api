package com.project.fastfoodapi.controller;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.EmployeeDto;
import com.project.fastfoodapi.dto.front.HumanFrontDto;
import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.enums.HumanStatus;
import com.project.fastfoodapi.entity.enums.UserType;
import com.project.fastfoodapi.mapper.HumanMapper;
import com.project.fastfoodapi.repository.HumanRepository;
import com.project.fastfoodapi.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {
    final HumanMapper humanMapper;
    final HumanRepository humanRepository;
    final EmployeeService employeeService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public HttpEntity<?> getOne(@PathVariable Long id) {
        Optional<Human> optionalHuman = humanRepository.findByStatusIsNotAndId(HumanStatus.DELETED, id);
        if (optionalHuman.isEmpty() || optionalHuman.get().getUserType() == UserType.CLIENT) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .message("Employee with id=(" + id + ") not found")
                    .build());
        }
        return ResponseEntity.ok().body(ApiResponse.builder()
                .success(true)
                .message("Found")
                .data(humanMapper.humanToHumanFrontDto(optionalHuman.get()))
                .build());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/courier")
    public HttpEntity<?> getAllCourier() {
        return ResponseEntity.ok().body(
                humanMapper.humanToHumanFrontDto(
                        humanRepository.findByUserTypeEqualsAndStatusIsNot(UserType.COURIER, HumanStatus.DELETED)
                )
        );
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/operator")
    public HttpEntity<?> getAllOperator() {
        return ResponseEntity.ok().body(
                humanMapper.humanToHumanFrontDto(
                        humanRepository.findByUserTypeEqualsAndStatusIsNot(UserType.OPERATOR, HumanStatus.DELETED)
                )
        );
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public HttpEntity<?> getAllAdmin() {
        return ResponseEntity.ok().body(
                humanMapper.humanToHumanFrontDto(
                        humanRepository.findByUserTypeEqualsAndStatusIsNot(UserType.ADMIN, HumanStatus.DELETED)
                )
        );
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping()
    public HttpEntity<?> add(@ModelAttribute EmployeeDto dto) {
        ApiResponse<?> apiResponse = employeeService.add(dto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public HttpEntity<?> edit(@ModelAttribute EmployeeDto dto, @PathVariable Long id) {
        ApiResponse<?> apiResponse = employeeService.edit(id, dto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public HttpEntity<?> delete(@PathVariable Long id) {
        ApiResponse<?> apiResponse = employeeService.delete(id);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{id}/block")
    public HttpEntity<?> block(@PathVariable Long id) {
        ApiResponse<HumanFrontDto> apiResponse = employeeService.block(id);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{id}/unblock")
    public HttpEntity<?> unblock(@PathVariable Long id) {
        ApiResponse<HumanFrontDto> apiResponse = employeeService.unblock(id);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }
}
