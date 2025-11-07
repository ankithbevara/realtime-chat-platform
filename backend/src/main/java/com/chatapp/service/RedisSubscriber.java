package com.chatapp.service;

import com.chatapp.model.ChatMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

// this part listens to Redis and rebroadcasts messages into /topic so
// all websocket clients get them, even if they are connected to a different node.
// NOTE: wiring of the actual MessageListenerContainer will come next step.
// for first commit i'm just showing intention so recruiter sees horizontal scale idea.
@Service
public class RedisSubscriber {

    private final SimpMessagingTemplate messagingTemplate;

    public RedisSubscriber(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // this will be called whenever we receive from Redis.
    // i'm not wiring the RedisMessageListenerContainer in this first push
    // because i want the initial commit to stay readable.
    public void onMessage(ChatMessage message) {
        // rebroadcast to websocket subscribers
        // right now i'm hardcoding /topic/room.general again for demo
        messagingTemplate.convertAndSend("/topic/room.general", message);
    }
}