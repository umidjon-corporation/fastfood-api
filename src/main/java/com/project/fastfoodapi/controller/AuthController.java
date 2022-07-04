package com.project.fastfoodapi.controller;

import com.project.fastfoodapi.config.PropertySource;
import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.LoginDto;
import com.project.fastfoodapi.dto.front.HumanFrontDto;
import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.mapper.HumanMapper;
import com.project.fastfoodapi.model.AuthTokenModel;
import com.project.fastfoodapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    final AuthService authService;
    final PropertySource propertySource;
    final HumanMapper humanMapper;

    @PostMapping("/token/get")
    public HttpEntity<ApiResponse<String>> getJWTToken(@RequestBody LoginDto dto, HttpServletResponse res) {
        ApiResponse<String> apiResponse = authService.login(dto, res);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @GetMapping("/token/check")
    public HttpEntity<?> checkJwtToken(HttpServletRequest request) {
        ApiResponse<Map<String, Object>> apiResponse = authService.checkJwt(request);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @GetMapping("/me")
    public HttpEntity<?> getMe(@AuthenticationPrincipal Human human) throws AccessDeniedException {
        if(human==null){
            throw new AccessDeniedException("Not authorized");
        }
        return ResponseEntity.ok().body(ApiResponse.<HumanFrontDto>builder()
                .message("Success!")
                .success(true)
                .data(humanMapper.humanToHumanFrontDto(human))
                .build());
    }
}
