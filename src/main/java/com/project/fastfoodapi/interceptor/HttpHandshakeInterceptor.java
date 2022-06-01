package com.project.fastfoodapi.interceptor;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.repository.HumanRepository;
import com.project.fastfoodapi.service.AuthService;
import com.project.fastfoodapi.utils.TokenClaims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalField;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HttpHandshakeInterceptor implements HandshakeInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(HttpHandshakeInterceptor.class);
    final AuthService authService;
    final HumanRepository humanRepository;
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        logger.info("Before handshake");
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpSession session = servletRequest.getServletRequest().getSession();
            attributes.put("sessionId", session.getId());
            ApiResponse<Map<String, Object>> apiResponse = authService.checkJwt(servletRequest.getServletRequest());
            if(!apiResponse.isSuccess()){
                return false;
            }
            attributes.put("user", apiResponse.getData());
            long expire = (long) apiResponse.getData().get(TokenClaims.EXPIRE.getKey());
            long l = expire - (new Date().getTime());
            session.setMaxInactiveInterval((int)l);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        logger.info("After handshake");
    }
}