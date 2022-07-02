package com.project.fastfoodapi.controller;

import com.project.fastfoodapi.config.PropertySource;
import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.LoginDto;
import com.project.fastfoodapi.model.AuthTokenModel;
import com.project.fastfoodapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    final AuthService authService;
    final PropertySource propertySource;

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
}
