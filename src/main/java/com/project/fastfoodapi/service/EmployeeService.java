package com.project.fastfoodapi.service;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.EmployeeDto;
import com.project.fastfoodapi.dto.front.HumanFrontDto;
import com.project.fastfoodapi.dto.front.ProductFrontDto;
import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.enums.HumanStatus;
import com.project.fastfoodapi.entity.enums.UserType;
import com.project.fastfoodapi.mapper.HumanMapper;
import com.project.fastfoodapi.repository.HumanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    final HumanRepository humanRepository;
    final HumanMapper humanMapper;

    public ApiResponse<HumanFrontDto> add(EmployeeDto dto) {
        Human human = humanMapper.humanDtoToHuman(dto);
        if (dto.getStatus() == null) {
            human.setStatus(HumanStatus.ACTIVE);
        }
        ApiResponse<HumanFrontDto> checkDto = checkDto(dto, "");
        if(!checkDto.isSuccess()){
            return checkDto;
        }
        Human save = humanRepository.save(human);
        return ApiResponse.<HumanFrontDto>builder()
                .data(humanMapper.humanToHumanFrontDto(save))
                .success(true)
                .message("Added!")
                .build();
    }

    public ApiResponse<HumanFrontDto> edit(Long id, EmployeeDto dto) {
        Optional<Human> optionalHuman = humanRepository.findByStatusIsNotAndId(HumanStatus.DELETED, id);
        if (optionalHuman.isEmpty()) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message(dto.getType().name() + " with id=(" + id + ") not found")
                    .build();
        }
        ApiResponse<HumanFrontDto> checkDto = checkDto(dto, optionalHuman.get().getNumber());
        if(!checkDto.isSuccess()){
            return checkDto;
        }
        Human human = optionalHuman.get();
        humanMapper.updateHumanFromHumanDto(dto, human);
        if (dto.getStatus() == null) {
            human.setStatus(HumanStatus.ACTIVE);
        }
        Human save = humanRepository.save(human);
        return ApiResponse.<HumanFrontDto>builder()
                .data(humanMapper.humanToHumanFrontDto(save))
                .success(true)
                .message("Edited!")
                .build();
    }

    public ApiResponse<HumanFrontDto> checkDto(EmployeeDto dto, String oldNumber){
        if (dto.getType() == UserType.CLIENT) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("You can't save employee with user type client")
                    .build();
        }
        if(dto.getPhoto()!=null){
            if (dto.getPhoto().getOriginalFilename()==null || !dto.getPhoto().getOriginalFilename().matches("^(.+)\\.(png|jpeg|ico|jpg)$")) {
                return ApiResponse.<HumanFrontDto>builder()
                        .message("Photo type must be png, jpeg, ico, jpg")
                        .build();
            }
        }

        if (!dto.getNumber().equals(oldNumber) && humanRepository.existsByNumber(dto.getNumber())) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("Number of employee already existed")
                    .build();
        }

        return ApiResponse.<HumanFrontDto>builder().success(true).build();
    }

    public ApiResponse<?> delete(Long id) {
        Optional<Human> optionalHuman = humanRepository.findByStatusIsNotAndId(HumanStatus.DELETED, id);
        if (optionalHuman.isEmpty()) {
            return ApiResponse.builder()
                    .message("Employee with id=(" + id + ") not found")
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
        if (optionalHuman.isEmpty()) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("Human with id=(" + id + ") not found")
                    .build();
        }
        optionalHuman.get().setStatus(HumanStatus.BLOCKED);
        humanRepository.save(optionalHuman.get());
        return ApiResponse.<HumanFrontDto>builder()
                .success(true)
                .message("Blocked!")
                .build();
    }

    public ApiResponse<HumanFrontDto> unblock(Long id) {
        Optional<Human> optionalHuman = humanRepository.findByStatusIsNotAndId(HumanStatus.DELETED, id);
        if (optionalHuman.isEmpty()) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("Human with id=(" + id + ") not found")
                    .build();
        }
        optionalHuman.get().setStatus(HumanStatus.ACTIVE);
        humanRepository.save(optionalHuman.get());
        return ApiResponse.<HumanFrontDto>builder()
                .success(true)
                .message("Unblocked!")
                .build();
    }
}
