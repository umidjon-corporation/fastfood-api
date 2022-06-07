package com.project.fastfoodapi.service;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.HumanDto;
import com.project.fastfoodapi.dto.front.HumanFrontDto;
import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.enums.ClientStatus;
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
            human.setStatus(ClientStatus.ACTIVE);
        }
        human.setUserType(UserType.CLIENT);
        Human save = humanRepository.save(human);
        return ApiResponse.<HumanFrontDto>builder()
                .data(humanMapper.humanToHumanFrontDto(save))
                .success(true)
                .message("Added!")
                .build();
    }

    public ApiResponse<HumanFrontDto> edit(Long id, HumanDto dto) {
        Optional<Human> optionalHuman = humanRepository.findByStatusIsNotAndId(ClientStatus.DELETED, id);
        if (optionalHuman.isEmpty() || optionalHuman.get().getUserType() != UserType.CLIENT) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("Client with id=(" + id + ") not found")
                    .build();
        }
        Human human = optionalHuman.get();
        humanMapper.updateHumanFromHumanDto(dto, human);
        if (dto.getStatus() == null) {
            human.setStatus(ClientStatus.ACTIVE);
        }
        Human save = humanRepository.save(human);
        return ApiResponse.<HumanFrontDto>builder()
                .data(humanMapper.humanToHumanFrontDto(save))
                .success(true)
                .message("Edited!")
                .build();
    }

    public ApiResponse<?> delete(Long id) {
        Optional<Human> optionalHuman = humanRepository.findByStatusIsNotAndId(ClientStatus.DELETED, id);
        if (optionalHuman.isEmpty() || optionalHuman.get().getUserType() != UserType.CLIENT) {
            return ApiResponse.builder()
                    .message("Client with id=(" + id + ") not found")
                    .build();
        }
        optionalHuman.get().setStatus(ClientStatus.DELETED);
        humanRepository.save(optionalHuman.get());
        return ApiResponse.builder()
                .success(true)
                .message("Deleted!")
                .build();
    }

    public ApiResponse<Object> block(Long id) {
        Optional<Human> optionalHuman = humanRepository.findByStatusIsNotAndId(ClientStatus.DELETED, id);
        if (optionalHuman.isEmpty() || optionalHuman.get().getUserType() != UserType.CLIENT) {
            return ApiResponse.builder()
                    .message("Client with id=(" + id + ") not found")
                    .build();
        }
        optionalHuman.get().setStatus(ClientStatus.BLOCKED);
        humanRepository.save(optionalHuman.get());
        return ApiResponse.builder()
                .success(true)
                .message("Blocked!")
                .build();
    }
}
