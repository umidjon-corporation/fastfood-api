package com.project.fastfoodapi.interceptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

public class TopicSubscriptionInterceptor implements ChannelInterceptor {
    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getSessionAttributes()!=null && accessor.getCommand()!=null) {
            Gson gson=new GsonBuilder().setPrettyPrinting().create();
            Map<String, Object> user =gson.fromJson(gson.toJson(accessor.getSessionAttributes().get("user")),
                    new TypeToken<Map<String, Object>>(){}.getType());
            System.out.println(accessor.getSessionAttributes());
            if(user!=null){
                switch (accessor.getCommand()){
                    case SUBSCRIBE -> {
                        String destination = (String)message.getHeaders().get("simpDestination");
                        System.out.println(destination);
                    }
                    case CONNECT -> {
                        System.err.println(user);
                    }
                    case CONNECTED -> {
                        System.out.println(message);
                        System.out.println(channel);
                    }
                }
            }else {
//                throw new Exception("Access denied!");
            }
            return message;
        }
        return message;
    }
}
