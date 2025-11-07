package com.chatapp.config;

import com.chatapp.security.JwtHandshakeInterceptor;
import org.springframework.context.annotation.Configuration; // marks this class as config bean
import org.springframework.messaging.simp.config.MessageBrokerRegistry; // broker config
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker; // enable STOMP ws
import org.springframework.web.socket.config.annotation.StompEndpointRegistry; // endpoint registry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer; // hook to customize

//@Configuration -> spring should pick this up as a config class
//@EnableWebSocketMessageBroker -> turn on WebSocket / STOMP messaging support
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //i am injecting my own interceptor that will validate JWT on connect.
    //right now it's a basic skeleton, but this shows security thinking
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    public WebSocketConfig(JwtHandshakeInterceptor jwtHandshakeInterceptor) {
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
    }

    //this is basically "where can the browser open a socket connection?"
    //i'm exposing /ws-chat as the endpoint.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat") // frontend will connect to this path
                .addInterceptors(jwtHandshakeInterceptor) // check token during handshake
                .setAllowedOriginPatterns("*") // allow any origin for now (dev only)
                .withSockJS(); // fallback for browsers that don't support native WebSocket
    }

    //this part is about routing messages.
    //client sends to /app/... and we broadcast to /topic/...
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //this is what clients "subscribe" to.
        //example: /topic/room.general -> all users in general room get messages
        registry.enableSimpleBroker("/topic");

        //this is prefix for "sending" messages from client.
        //if frontend sends to /app/chat.sendMessage, it will hit my @MessageMapping method.
        registry.setApplicationDestinationPrefixes("/app");
    }
}