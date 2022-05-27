package com.project.fastfoodapi.service;

import com.project.fastfoodapi.config.PropertySource;
import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.LoginDto;
import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.enums.UserType;
import com.project.fastfoodapi.model.AuthTokenModel;
import com.project.fastfoodapi.repository.HumanRepository;
import com.project.fastfoodapi.utils.JWTHelper;
import com.project.fastfoodapi.utils.TokenClaims;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.nio.file.AccessDeniedException;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    final PropertySource propertySource;
    final HumanRepository humanRepository;
    final PasswordEncoder passwordEncoder;

    public Key getSecretKey(){
        final byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(propertySource.getAppAuthSecret());
        return new SecretKeySpec(apiKeySecretBytes, JWTHelper.SIGNATURE_ALGORITHM.getJcaName());
    }

    public AuthTokenModel getJwtToken(LoginDto dto, HttpServletResponse res) {
        Map<String, Object> claims = getUserInfo(dto);
        String jwt = JWTHelper.creatJWT(claims, "Auth service", propertySource.getAppAuthSecret());
        Cookie cookie=new Cookie(propertySource.getCookieName(), jwt);
        cookie.setMaxAge(7200);
        cookie.setPath("/");
        cookie.setSecure(true);
        res.addCookie(cookie);
        return getTokenModel(jwt);
    }

    @SneakyThrows
    private final Map<String, Object> getUserInfo(LoginDto dto) {
        final Map<String, Object> claims = new HashMap<>();
        Optional<Human> optionalHuman = humanRepository.findByNumber(dto.getLogin());
        if (optionalHuman.isEmpty() || optionalHuman.get().getUserType() != UserType.ADMIN
                || !passwordEncoder.matches(dto.getPassword(), optionalHuman.get().getPassword())) {
            throw new AccessDeniedException("Username or password not valid");
        }
        claims.put(TokenClaims.USER_ID.getKey(), optionalHuman.get().getId());
        claims.put(TokenClaims.USER_NAME.getKey(), optionalHuman.get().getName());
        claims.put(TokenClaims.USER_NUMBER.getKey(), optionalHuman.get().getNumber());
        return claims;
    }

    private final AuthTokenModel getTokenModel(String jwt) {
        return new AuthTokenModel(propertySource.getAppAuthTokenType(), jwt);
    }

    public ApiResponse<Map<String, Object>> checkJwt(String token) {
        try {
            JWTHelper.checkJwt(getSecretKey(), token);
        }catch (Exception e){
            return ApiResponse.<Map<String, Object>>builder()
                    .message(e.getMessage())
                    .build();
        }

        return ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Token passed!")
                .data(JWTHelper.getClaims(getSecretKey(), token))
                .build();
    }


    public ApiResponse<Map<String, Object>> checkJwt(HttpServletRequest request){
        String authorization = request.getHeader(propertySource.getAppAuthHeaderKey());
        if(authorization==null){
            Cookie[] cookies = request.getCookies();
            if(cookies==null){
                return ApiResponse.<Map<String, Object>>builder()
                        .message("Token not found")
                        .build();
            }
            for (Cookie cookie : cookies) {
                if(cookie!=null && cookie.getName().equals(propertySource.getCookieName())){
                    return checkJwt(cookie.getValue());
                }
            }
            return ApiResponse.<Map<String, Object>>builder()
                    .message("Token not found")
                    .build();
        }
        return checkJwt(authorization);
    }

    @Override
    public UserDetails loadUserByUsername(String number) throws UsernameNotFoundException {
        Optional<Human> optionalUser = humanRepository.findByNumber(number);
        return optionalUser.orElse(null);
    }
}
