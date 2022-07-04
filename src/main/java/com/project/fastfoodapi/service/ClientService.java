package com.project.fastfoodapi.service;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.EmployeeDto;
import com.project.fastfoodapi.dto.HumanDto;
import com.project.fastfoodapi.dto.front.HumanFrontDto;
import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.enums.HumanStatus;
import com.project.fastfoodapi.entity.enums.UserType;
import com.project.fastfoodapi.mapper.HumanMapper;
import com.project.fastfoodapi.repository.HumanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        ApiResponse<HumanFrontDto> checkDto = checkDto(dto);
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
        ApiResponse<HumanFrontDto> checkDto = checkDto(dto);
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

    public ApiResponse<HumanFrontDto> checkDto(HumanDto dto){
        if(dto.getPhoto()!=null){
            if (dto.getPhoto().getOriginalFilename()==null || dto.getPhoto().getOriginalFilename().matches("^(.+)\\.(png|jpeg|ico|jpg)$")) {
                return ApiResponse.<HumanFrontDto>builder()
                        .message("Photo type must be png, jpeg, ico, jpg")
                        .build();
            }
        }
        if (humanRepository.existsByNumber(dto.getNumber())) {
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

    public ApiResponse<Object> block(Long id) {
        Optional<Human> optionalHuman = humanRepository.findByStatusIsNotAndId(HumanStatus.DELETED, id);
        if (optionalHuman.isEmpty() || optionalHuman.get().getUserType() != UserType.CLIENT) {
            return ApiResponse.builder()
                    .message("Client with id=(" + id + ") not found")
                    .build();
        }
        optionalHuman.get().setStatus(HumanStatus.BLOCKED);
        humanRepository.save(optionalHuman.get());
        return ApiResponse.builder()
                .success(true)
                .message("Blocked!")
                .build();
    }
}
