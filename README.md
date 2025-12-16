# Realtime Chat Platform

Small side project I'm building to understand real-time messaging and horizontal scaling.

### Tech
- Java 17 + Spring Boot 3 (WebSocket/STOMP)
- Redis Pub/Sub for cross-node broadcast
- React (Vite) frontend
- JWT handshake for socket auth (in progress)
- Docker Compose to run everything together

### What it does
- Users connect over WebSocket (no HTTP polling).
- Users can send messages to a "room".
- Everyone in that room receives the message instantly.
- Redis is used, so if I scale the backend to multiple containers, all instances stay in sync and still broadcast the same message.

### Why I built this
Most chat tutorials stop at 1 server instance. In real life, you almost always run multiple pods/containers behind a load balancer.
I wanted to see how to handle that using Redis pub/sub.

### TODO / next steps
- Proper JWT validation in `JwtHandshakeInterceptor`
- Dynamic rooms instead of hardcoded `/topic/room.general`
- Typing indicator events
- Online/offline presence map stored in Redis
