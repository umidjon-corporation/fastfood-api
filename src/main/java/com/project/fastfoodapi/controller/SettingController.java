package com.project.fastfoodapi.controller;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.SettingDto;
import com.project.fastfoodapi.dto.SettingsDto;
import com.project.fastfoodapi.dto.front.SettingFrontDto;
import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.enums.UserType;
import com.project.fastfoodapi.mapper.SettingMapper;
import com.project.fastfoodapi.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/setting")
public class SettingController {
    private final SettingService settingService;
    private final SettingMapper settingMapper;

    @PreAuthorize("isFullyAuthenticated()")
    @GetMapping()
    public HttpEntity<?> getSettings(@AuthenticationPrincipal Human human){
        return ResponseEntity.ok().body(settingMapper.toFrontDto(human.getSettings()));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/props/{userType}")
    public HttpEntity<?> getSettingsProps(@PathVariable UserType userType){
        return ResponseEntity.ok().body(settingService.getSettingsProps(userType));
    }

    @PreAuthorize("isFullyAuthenticated()")
    @PatchMapping("/{name}")
    public HttpEntity<?> changeSetting(
            @PathVariable String name,
            @RequestBody SettingDto dto,
            @AuthenticationPrincipal Human human,
            @RequestParam(required = false) boolean reset
    ){
        ApiResponse<SettingFrontDto> apiResponse=settingService.editSetting(name, dto, human, reset, true);
        return ResponseEntity.status(apiResponse.isSuccess()?200:400).body(apiResponse);
    }

    @PreAuthorize("isFullyAuthenticated()")
    @PutMapping()
    public HttpEntity<?> editSettings(
            @RequestBody List<SettingsDto> dto,
            @AuthenticationPrincipal Human human
    ){
        ApiResponse<List<SettingFrontDto>> apiResponse=settingService.editSettings(dto, human, true);
        return ResponseEntity.status(apiResponse.isSuccess()?200:400).body(apiResponse);
    }
}
