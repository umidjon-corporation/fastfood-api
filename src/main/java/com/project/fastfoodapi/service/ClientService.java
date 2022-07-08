package com.project.fastfoodapi.service;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.HumanDto;
import com.project.fastfoodapi.dto.PageableResponse;
import com.project.fastfoodapi.dto.front.HumanFrontDto;
import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.enums.HumanStatus;
import com.project.fastfoodapi.entity.enums.UserType;
import com.project.fastfoodapi.mapper.HumanMapper;
import com.project.fastfoodapi.repository.HumanRepository;
import com.project.fastfoodapi.specification.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {
    final HumanRepository humanRepository;
    final HumanMapper humanMapper;

    public ApiResponse<HumanFrontDto> add(HumanDto dto) {
        Human human = humanMapper.humanDtoToHuman(dto);
        human.setUserType(UserType.CLIENT);
        if (dto.getStatus() == null) {
            human.setStatus(HumanStatus.ACTIVE);
        }
        ApiResponse<HumanFrontDto> checkDto = checkDto(dto, "");
        if (!checkDto.isSuccess()){
            return checkDto;
        }
        Human save = humanRepository.save(human);
        return ApiResponse.<HumanFrontDto>builder()
                .data(humanMapper.humanToHumanFrontDto(save))
                .success(true)
                .message("Added!")
                .build();
    }

    public ApiResponse<HumanFrontDto> edit(Long id, HumanDto dto) {
        Optional<Human> optionalHuman = humanRepository.findByStatusIsNotAndId(HumanStatus.DELETED, id);
        if (optionalHuman.isEmpty() || optionalHuman.get().getUserType() != UserType.CLIENT) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("Client with id=(" + id + ") not found")
                    .build();
        }
        ApiResponse<HumanFrontDto> checkDto = checkDto(dto, optionalHuman.get().getNumber());
        if (!checkDto.isSuccess()){
            return checkDto;
        }
        Human human = optionalHuman.get();
        humanMapper.updateHumanFromHumanDto(dto, human);
        Human save = humanRepository.save(human);
        return ApiResponse.<HumanFrontDto>builder()
                .data(humanMapper.humanToHumanFrontDto(save))
                .success(true)
                .message("Edited!")
                .build();
    }

    public ApiResponse<HumanFrontDto> checkDto(HumanDto dto, String oldNumber){
        if(dto.getPhoto()!=null){
            if (dto.getPhoto().getOriginalFilename()==null || !dto.getPhoto().getOriginalFilename().matches("^(.+)\\.(png|jpeg|ico|jpg)$")) {
                return ApiResponse.<HumanFrontDto>builder()
                        .message("Photo type must be png, jpeg, ico, jpg")
                        .build();
            }
        }

        if (!dto.getNumber().equals(oldNumber) && humanRepository.existsByNumber(dto.getNumber())) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("Number of client already existed")
                    .build();
        }
        return ApiResponse.<HumanFrontDto>builder().success(true).build();
    }

    public ApiResponse<?> delete(Long id) {
        Optional<Human> optionalHuman = humanRepository.findByStatusIsNotAndId(HumanStatus.DELETED, id);
        if (optionalHuman.isEmpty() || optionalHuman.get().getUserType() != UserType.CLIENT) {
            return ApiResponse.builder()
                    .message("Client with id=(" + id + ") not found")
                    .build();
        }
        optionalHuman.get().setStatus(HumanStatus.DELETED);
        humanRepository.save(optionalHuman.get());
        return ApiResponse.builder()
                .success(true)
                .message("Deleted!")
                .build();
    }

    public ApiResponse<HumanFrontDto> block(Long id) {
        Optional<Human> optionalHuman = humanRepository.findByStatusIsNotAndId(HumanStatus.DELETED, id);
        if (optionalHuman.isEmpty() || optionalHuman.get().getUserType() != UserType.CLIENT) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("Client with id=(" + id + ") not found")
                    .build();
        }
        optionalHuman.get().setStatus(HumanStatus.BLOCKED);
        humanRepository.save(optionalHuman.get());
        return ApiResponse.<HumanFrontDto>builder()
                .success(true)
                .message("Blocked!")
                .data(humanMapper.humanToHumanFrontDto(optionalHuman.get()))
                .build();
    }

    public ApiResponse<HumanFrontDto> unblock(Long id) {
        Optional<Human> optionalHuman = humanRepository.findByStatusIsNotAndId(HumanStatus.DELETED, id);
        if (optionalHuman.isEmpty() || optionalHuman.get().getUserType() != UserType.CLIENT) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("Client with id=(" + id + ") not found")
                    .build();
        }
        optionalHuman.get().setStatus(HumanStatus.ACTIVE);
        humanRepository.save(optionalHuman.get());
        return ApiResponse.<HumanFrontDto>builder()
                .success(true)
                .message("Unblocked!")
                .data(humanMapper.humanToHumanFrontDto(optionalHuman.get()))
                .build();
    }

    public PageableResponse<HumanFrontDto> getAll(int page, int size, String q, String[] sort, boolean desc, String status){
        SearchRequest.SearchRequestBuilder searchRequest = SearchRequest.builder();
        List<FilterRequest> filterRequests=new ArrayList<>();
        List<SortRequest> sortRequests=new ArrayList<>();
        if(q!=null && !q.equals("")){
            filterRequests.add(FilterRequest.builder()
                    .operator(Operator.LIKE)
                    .value(q)
                    .key("name")
                    .fieldType(FieldType.STRING)
                    .build());
            filterRequests.add(FilterRequest.builder()
                    .operator(Operator.LIKE)
                    .value(q)
                    .or(true)
                    .fieldType(FieldType.STRING)
                    .key("number")
                    .build());
        }
        if(sort!=null){
            for (String s : sort) {
                sortRequests.add(SortRequest.builder()
                        .key(s)
                        .direction(desc?SortDirection.DESC:SortDirection.ASC)
                        .build());
            }
            searchRequest.sorts(sortRequests);
        }
        List<FilterRequest> requiredFilters=new ArrayList<>();

        if(status!=null && (status.equalsIgnoreCase("blocked") || status.equalsIgnoreCase("active"))){
            requiredFilters.add(FilterRequest.builder()
                            .key("status")
                            .value(status.equalsIgnoreCase("blocked")?HumanStatus.BLOCKED:HumanStatus.ACTIVE)
                            .operator(Operator.EQUAL)
                            .fieldType(FieldType.OBJECT)
                    .build());
        }else {
            requiredFilters.add( FilterRequest.builder()
                    .fieldType(FieldType.OBJECT)
                    .value(HumanStatus.DELETED)
                    .key("status")
                    .operator(Operator.NOT_EQUAL)
                    .build());
        }

        Page<Human> all = humanRepository.findAll(
                new EntitySpecification<Human>(searchRequest.filters(requiredFilters).build())
                        .and(new EntitySpecification<>(searchRequest.filters(filterRequests).build())),
                EntitySpecification.getPageable(page, size)
        );
        return PageableResponse.parsePage(all, humanMapper.humanToHumanFrontDto(all.getContent()));
    }
}
