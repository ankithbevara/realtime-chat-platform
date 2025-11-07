//simple place to change backend URL if I run containers later.
//in local dev I keep backend on 8080.
export const BACKEND_HTTP_BASE = import.meta.env.VITE_BACKEND_HTTP_BASE || "http://localhost:8080";
export const WS_ENDPOINT = `${BACKEND_HTTP_BASE}/ws-chat`; //spring exposes /ws-chat
export const TOPIC_ROOM = "/topic/room.general";           //same room as backend for first demo
export const APP_SEND = "/app/chat.sendMessage";           //controller @MessageMapping