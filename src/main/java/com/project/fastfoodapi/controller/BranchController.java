package com.project.fastfoodapi.controller;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.BranchDto;
import com.project.fastfoodapi.entity.Branch;
import com.project.fastfoodapi.repository.BranchRepository;
import com.project.fastfoodapi.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/branch")
@RequiredArgsConstructor
public class BranchController {
    final BranchService branchService;
    final BranchRepository branchRepository;

    @GetMapping
    public HttpEntity<?> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "50") int size,
            @RequestParam(required = false, defaultValue = "false") boolean desc,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String q
    ) {
        return ResponseEntity.ok().body(branchService.getAll(page, size, q, sort, desc));
    }

    @GetMapping("/{id}")
    public HttpEntity<?> getOne(@PathVariable Long id) {
        Optional<Branch> optionalBranch = branchRepository.findByIdAndActiveTrue(id);
        if (optionalBranch.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(optionalBranch.get());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public HttpEntity<?> add(@RequestBody BranchDto dto) {
        ApiResponse<?> apiResponse = branchService.add(dto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public HttpEntity<?> edit(@RequestBody BranchDto dto, @PathVariable Long id) {
        ApiResponse<?> apiResponse = branchService.edit(id, dto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public HttpEntity<?> delete(@PathVariable Long id) {
        ApiResponse<?> apiResponse = branchService.delete(id);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }
}
