package com.project.fastfoodapi.service;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.BranchDto;
import com.project.fastfoodapi.dto.PageableResponse;
import com.project.fastfoodapi.entity.Branch;
import com.project.fastfoodapi.mapper.BranchMapper;
import com.project.fastfoodapi.repository.BranchRepository;
import com.project.fastfoodapi.specification.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public PageableResponse<Branch> getAll(int page, int size, String q, String[] sort, boolean desc){
        SearchRequest.SearchRequestBuilder builder = SearchRequest.builder();
        List<FilterRequest> filterRequests=new ArrayList<>();
        List<SortRequest> sortRequests=new ArrayList<>();
        if(q!=null && !q.equals("")){
            filterRequests.add(FilterRequest.builder()
                    .key("nameUz")
                    .operator(Operator.LIKE)
                    .value(q)
                    .fieldType(FieldType.STRING)
                    .build());
            filterRequests.add(FilterRequest.builder()
                    .key("nameRu")
                    .operator(Operator.LIKE)
                    .value(q)
                    .or(true)
                    .fieldType(FieldType.STRING)
                    .build());
            filterRequests.add(FilterRequest.builder()
                    .key("intended")
                    .operator(Operator.LIKE)
                    .value(q)
                    .or(true)
                    .fieldType(FieldType.STRING)
                    .build());
        }
        if(sort!=null){
            for (String s : sort) {
                sortRequests.add(SortRequest.builder()
                                .key(s)
                                .direction(desc?SortDirection.DESC:SortDirection.ASC)
                        .build());
            }
        }

        Page<Branch> all = branchRepository.findAll(
                new EntitySpecification<Branch>(builder.filters(List.of(FilterRequest.builder()
                        .fieldType(FieldType.BOOLEAN)
                        .key("active")
                        .value(true)
                        .operator(Operator.EQUAL)
                        .build())).sorts(sortRequests).build()).and(
                                new EntitySpecification<>(builder.filters(filterRequests).build())),
                EntitySpecification.getPageable(page, size)
        );
        return PageableResponse.<Branch>builder()
                .content(all.getContent())
                .totalItems(all.getTotalElements())
                .currentPage(all.getNumber())
                .totalPages(all.getTotalPages())
                .build();
    }
}
