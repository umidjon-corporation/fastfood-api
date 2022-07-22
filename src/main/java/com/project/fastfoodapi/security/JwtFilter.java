package com.project.fastfoodapi.security;

import com.google.gson.Gson;
import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.service.AuthService;
import com.project.fastfoodapi.utils.TokenClaims;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.NestedServletException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    final AuthService authService;
    final Gson gson;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) {
        try {
            ApiResponse<Map<String, Object>> tokenClaims = authService.checkJwt(req);
            if (tokenClaims.isSuccess()) {
                UserDetails userDetails = authService.loadUserByUsername((String) tokenClaims.getData().get(TokenClaims.USER_NUMBER.getKey()));
                if (userDetails == null) {
                    throw new AccessDeniedException("User not found");
                }
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, userDetails.getPassword(), userDetails.getAuthorities()
                );
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(req, res);
            if (res.getStatus() == 401) {
                res.setContentType("application/json");
                res.getWriter().write(gson.toJson(tokenClaims));
            }
        } catch (AccessDeniedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            res.setContentType("application/json");
            res.getWriter().write(gson.toJson(ApiResponse.builder()
                            .message("Something went wrong")
                    .build()));
        }

    }
}
