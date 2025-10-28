package com.chatapp.service;

import com.chatapp.model.ChatMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

// this service is responsible for publishing messages into Redis.
// idea: if i run 2 backend instances, both subscribe to Redis.
// so even if user is connected to instance A and another user is on instance B,
// everyone still hears the same message.
@Service
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisPublisher(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // simple helper method to publish the chat message
    public void publish(ChatMessage message) {
        // i'm using channel name "chatroom" for now.
        // in real prod you normally do room-based channels.
        redisTemplate.convertAndSend("chatroom", message);
    }
}