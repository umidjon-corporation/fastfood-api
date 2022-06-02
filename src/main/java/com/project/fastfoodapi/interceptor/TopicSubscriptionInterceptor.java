package com.project.fastfoodapi.interceptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.project.fastfoodapi.config.PropertySource;
import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.entity.Human;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Component
@RequiredArgsConstructor
public class TopicSubscriptionInterceptor implements ChannelInterceptor {
    final AuthService authService;
    final PropertySource propertySource;

    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getSessionAttributes()!=null && accessor.getCommand()!=null) {
//            Gson gson=new GsonBuilder().setPrettyPrinting().create();
            Gson gson=new Gson();
            switch (accessor.getCommand()){
                case SUBSCRIBE -> {
                    return message;
                }
                case CONNECT -> {
                    try {
                        Map<String, List<Object>> nativeHeaders = gson.fromJson(gson.toJson(message.getHeaders().get("nativeHeaders")),
                                new TypeToken<Map<String, List<Object>>>() {
                                }.getType());
                        String token = (String) nativeHeaders.get(propertySource.getAppAuthHeaderKey()).get(0);
                        ApiResponse<Map<String, Object>> apiResponse =
                                authService.checkJwt(token);
                        if(!apiResponse.isSuccess()){
                            throw new AccessDeniedException(apiResponse.getMessage());
                        }
                        accessor.getSessionAttributes().put("user", apiResponse.getData());
                    }
                    catch (Exception e){
                        MessageHeaders messageHeaders = accessor.getMessageHeaders();
                        Map<String, Object>headers=new LinkedHashMap<>();
                        for (String s : messageHeaders.keySet()) {
                            headers.put(s, messageHeaders.get(s));
                        }
                        headers.put("simpMessageType", SimpMessageType.DISCONNECT);
                        headers.replace("stompCommand", StompCommand.DISCONNECT);
                        return MessageBuilder.createMessage("Access denied", new MessageHeaders(headers));
                    }

                }
            }

        }
        return message;
    }
}
