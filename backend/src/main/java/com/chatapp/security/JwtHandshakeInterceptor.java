package com.chatapp.security;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

// this interceptor runs during the initial websocket handshake.
// the idea is: frontend will pass a JWT token (for example ?token=abc123),
// and here i can validate it before letting the socket connect.
// NOTE: for initial commit i'm not doing full JWT validation yet.
// but i'm leaving the hook and the comment so i can talk about security in interviews.
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    // beforeHandshake = this runs before websocket session is actually created.
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        // TODO: extract token from headers or query params.
        // example (pseudo):
        // String token = extractToken(request);
        // boolean valid = jwtService.validate(token);
        // if (!valid) return false;

        // for now i'm just allowing everyone so i can test quickly.
        return true;
    }

    // afterHandshake = not doing anything here for now
    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // no-op
    }
}