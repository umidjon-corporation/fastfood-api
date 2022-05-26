package com.project.fastfoodapi.controller;

import com.project.fastfoodapi.config.PropertySource;
import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.LoginDto;
import com.project.fastfoodapi.model.AuthTokenModel;
import com.project.fastfoodapi.service.AuthService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    final AuthService authService;
    final PropertySource propertySource;

    @PostMapping("/token/get")
    public HttpEntity<AuthTokenModel> getJWTToken(@RequestBody LoginDto dto) {
        return ResponseEntity.ok(authService.validateApiKeyAndGetJwtToken(dto));
    }

    @GetMapping("/token/check")
    public HttpEntity<?> checkJwtToken(HttpServletRequest request){
        ApiResponse<Map<String, Object>> apiResponse = authService.checkJwt(request);
        return ResponseEntity.status(apiResponse.isSuccess()?200:400).body(apiResponse);
    }
}
