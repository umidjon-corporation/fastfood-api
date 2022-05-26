package com.project.fastfoodapi.security;

import com.google.gson.Gson;
import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.service.AuthService;
import com.project.fastfoodapi.utils.JWTHelper;
import com.project.fastfoodapi.utils.TokenClaims;
import jdk.jfr.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    String[] urls=new String[]{
            "/api/auth/**",
            "/api/assets/**"
    };

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) {
        try {
            for (String url : urls) {
                int index = url.indexOf("*");
                if(index==-1){
                    if(req.getRequestURI().equals(url)){
                        doFilter(req,res,filterChain);
                        return;
                    }
                }else {
                    if(req.getRequestURI().startsWith(url.substring(0, index))){
                        doFilter(req,res,filterChain);
                        return;
                    }
                }

            }
            ApiResponse<Map<String, Object>> tokenClaims = authService.checkJwt(req);
            if (tokenClaims.isSuccess()) {
                UserDetails userDetails = authService.loadUserByUsername((String)
                        tokenClaims.getData().get(TokenClaims.USER_NUMBER.getKey()));
                if(userDetails==null){
                    throw new AccessDeniedException("User not found");
                }
                UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(
                        userDetails, userDetails.getPassword(), userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }else {
                res.setStatus(403);
                res.setContentType("application/json");
                res.getWriter().write(gson.toJson(tokenClaims));
                return;
            }

            doFilter(req,res,filterChain);
        }
        catch (AccessDeniedException | NestedServletException e){
            res.setStatus(403);
        }
        catch (Exception e){
            e.printStackTrace();
            res.setStatus(500);
        }

    }
}
