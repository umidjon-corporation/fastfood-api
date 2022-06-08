package com.project.fastfoodapi.interceptor;

import com.google.gson.Gson;
import com.project.fastfoodapi.config.PropertySource;
import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TopicSubscriptionInterceptor implements ChannelInterceptor {
    final AuthService authService;
    final PropertySource propertySource;
    final Gson gson;

    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        Map<String, Object> headers = new LinkedHashMap<>();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        MessageHeaders messageHeaders = accessor.getMessageHeaders();
        for (String s : messageHeaders.keySet()) {
            headers.put(s, messageHeaders.get(s));
        }
        if (accessor.getSessionAttributes() != null && accessor.getCommand() != null) {
            if (accessor.getCommand() == StompCommand.CONNECT) {
                try {
                    String token = accessor.getFirstNativeHeader(propertySource.getAppAuthHeaderKey());
                    ApiResponse<Map<String, Object>> apiResponse = authService.checkJwt(token);
                    if (!apiResponse.isSuccess()) {
                        throw new AccessDeniedException(apiResponse.getMessage());
                    }
                    accessor.getSessionAttributes().put("user", apiResponse.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                    headers.put("simpMessageType", SimpMessageType.DISCONNECT);
                    headers.replace("stompCommand", StompCommand.DISCONNECT);
                    return MessageBuilder.createMessage("Access denied", new MessageHeaders(headers));
                }
            }
        }
        System.err.println(message.getHeaders());
        return MessageBuilder.createMessage(message.getPayload(), new MessageHeaders(headers));
    }
}
