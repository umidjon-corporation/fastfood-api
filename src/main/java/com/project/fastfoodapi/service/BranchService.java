package com.project.fastfoodapi.service;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.BranchDto;
import com.project.fastfoodapi.entity.Branch;
import com.project.fastfoodapi.mapper.BranchMapper;
import com.project.fastfoodapi.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BranchService {
    final BranchRepository branchRepository;
    final BranchMapper branchMapper;

    public ApiResponse<Branch> add(BranchDto dto){
        Branch branch = branchMapper.branchDtoToBranch(dto);
        Branch save = branchRepository.save(branch);
        return ApiResponse.<Branch>builder()
                .data(save)
                .success(true)
                .message("Added!")
                .build();
    }

    public ApiResponse<Branch> edit(Long id, BranchDto dto){
        Optional<Branch> optionalBranch = branchRepository.findByIdAndActiveTrue(id);
        if(optionalBranch.isEmpty()){
            return ApiResponse.<Branch>builder()
                    .message("Branch with id=("+ id + ") not found")
                    .build();
        }
        Branch branch = optionalBranch.get();
        branchMapper.updateBranchFromBranchDto(dto, branch);
        Branch save = branchRepository.save(branch);
        return ApiResponse.<Branch>builder()
                .success(true)
                .message("Edited!")
                .data(save)
                .build();
    }


    public ApiResponse<Object> delete(Long id){
        Optional<Branch> optionalBranch = branchRepository.findByIdAndActiveTrue(id);
        if(optionalBranch.isEmpty()){
            return ApiResponse.builder()
                    .message("Branch with id=(" + id+") not found")
                    .build();
        }
        optionalBranch.get().setActive(false);
        branchRepository.save(optionalBranch.get());
        return ApiResponse.builder()
                .success(true)
                .message("Deleted!")
                .build();
    }
}
