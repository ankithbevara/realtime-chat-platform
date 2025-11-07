package com.chatapp.controller;

import com.chatapp.model.ChatMessage;
import com.chatapp.service.RedisPublisher;
import org.springframework.messaging.handler.annotation.MessageMapping; // maps a message sent from client
import org.springframework.messaging.handler.annotation.Payload; // body of the socket message
import org.springframework.messaging.handler.annotation.SendTo; // where to broadcast
import org.springframework.stereotype.Controller;

// @Controller here is not a REST controller. this is a STOMP controller.
// when frontend sends messages to /app/chat.sendMessage, they land here.
@Controller
public class ChatSocketController {

    private final RedisPublisher redisPublisher;

    public ChatSocketController(RedisPublisher redisPublisher) {
        this.redisPublisher = redisPublisher;
    }

      // client -> /app/chat.sendMessage
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        // publish once to Redis; RedisListenerConfig will rebroadcast to /topic/room.general
        redisPublisher.publish(chatMessage);
        // NOTE: no @SendTo, no return -> prevents double send
    }
}

/* 
    // this method means:
    // frontend will send a message to destination "/app/chat.sendMessage"
    @MessageMapping("/chat.sendMessage")
    // and we will broadcast result to "/topic/room.{roomName}"
    // NOTE: @SendTo runs inside THIS instance. for multi-instance scaling
    // i'll also push to redis so other instances can rebroadcast.
   
   

    @SendTo("/topic/room.general") // temporary hardcoded room for first commit
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {

        // log/publish to redis channel so other app nodes also get it
        redisPublisher.publish(chatMessage);

        // just echo back to whoever is subscribed to the topic
        // spring will convert this object to JSON automatically
        return chatMessage;
    }
}

*/