package com.chatapp.model;

import lombok.Data; // this gives me getters/setters/toString automatically

// this is the payload we send/receive over the socket.
// keeping it super simple: who sent it, which room, and actual text.
@Data
public class ChatMessage {

    // which room/channel this message belongs to
    private String room;

    // username or display name
    private String sender;

    // the actual chat text
    private String content;

    // later we can add messageType = "CHAT" | "TYPING" | "JOIN" etc
    // for now keeping it clean for demo
}