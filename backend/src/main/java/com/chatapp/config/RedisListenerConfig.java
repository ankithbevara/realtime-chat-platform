package com.chatapp.config;

import com.chatapp.model.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Subscribes to Redis "chatroom" channel and rebroadcasts messages
 * to the WebSocket topic so all connected clients (on any node) receive them.
 */
@Configuration
public class RedisListenerConfig {

    // Topic name must match what the publisher uses in RedisPublisher.publish(...)
    @Bean
    public ChannelTopic chatTopic() {
        return new ChannelTopic("chatroom");
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory connectionFactory,
            ChannelTopic chatTopic,
            ObjectMapper mapper,
            SimpMessagingTemplate messagingTemplate
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // Add a MessageListener (functional interface) directly with a lambda
        container.addMessageListener((message, pattern) -> {
            try {
                ChatMessage msg = mapper.readValue(message.getBody(), ChatMessage.class);
                // rebroadcast to the same topic the frontend is subscribed to
                messagingTemplate.convertAndSend("/topic/room.general", msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, chatTopic);

        container.setRecoveryInterval(2000L);  // retry every 2 seconds if Redis not ready yet
        
        return container;
    }
}