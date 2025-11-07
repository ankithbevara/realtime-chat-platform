//this is my very simple chat UI.
//i kept it small on purpose so a reviewer can read it in 1-2 minutes.
//i connect to Spring WebSocket (STOMP over SockJS), join a fixed room, and send/receive messages live.

import React, { useEffect, useRef, useState } from 'react'
import { Client } from '@stomp/stompjs' //STOMP client
import SockJS from 'sockjs-client/dist/sockjs.js'   //SockJS fallback for browsers
import { WS_ENDPOINT, TOPIC_ROOM, APP_SEND } from './config'

//tiny helper to format time in chat
function timeNow() {
  const d = new Date()
  return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

export default function App() {
  // my local UI state
  const [username, setUsername] = useState('')  //who am i in chat
  const [isConnected, setIsConnected] = useState(false) //whether socket is up
  const [message, setMessage] = useState('')    //message draft
  const [messages, setMessages] = useState([])  //list of messages shown in UI
  const [typing, setTyping] = useState(false)    //simple typing flag
  const clientRef = useRef(null)                 //holds STOMP client across renders

  //connect to backend websocket
  const connect = () => {
    if (!username.trim()) {
      alert('Please enter a username first')
      return
    }

    //create a STOMP client using SockJS transport to Spring's /ws-chat
    const client = new Client({
      //i'm using webSocketFactory so it works with SockJS
      webSocketFactory: () => new SockJS(WS_ENDPOINT),
      reconnectDelay: 2000, // simple auto-reconnect
      debug: (msg) => {
        //comment out if too noisy
        console.log('[stomp]', msg)
      }
    })

    //on connect, subscribe to the room topic so i can receive messages
    client.onConnect = () => {
      setIsConnected(true)

      client.subscribe(TOPIC_ROOM, (frame) => {
        try {
          const payload = JSON.parse(frame.body)
          setMessages(prev => [...prev, { ...payload, ts: timeNow() }])
        } catch (e) {
          console.error('bad message', e)
        }
      })

      //optional: announce that i "joined" (could be a system message)
      //i'm not doing a special type here; just a simple text
      client.publish({
        destination: APP_SEND,
        body: JSON.stringify({ sender: username, room: 'general', content: `${username} joined the room` })
      })
    }

    //on disconnect set flags
    client.onStompError = (frame) => {
      console.error('broker error', frame.headers['message'])
    }
    client.onWebSocketClose = () => {
      setIsConnected(false)
    }

    client.activate()
    clientRef.current = client
  }

  //disconnect cleanly
  const disconnect = () => {
    const c = clientRef.current
    if (c && c.active) {
      c.deactivate()
    }
    setIsConnected(false)
  }

  //send chat message to backend -> backend broadcasts to /topic/room.general
  const sendMessage = () => {
    if (!message.trim() || !clientRef.current) return

    clientRef.current.publish({
      destination: APP_SEND,
      body: JSON.stringify({ sender: username || 'anon', room: 'general', content: message })
    })

    setMessage('')
  }

  //very small "typing..." indicator:
  //when i type, show "typing" for ~1.2s and clear it; this is local-only
  useEffect(() => {
    if (!message) return
    setTyping(true)
    const t = setTimeout(() => setTyping(false), 1200)
    return () => clearTimeout(t)
  }, [message])

  return (
    <div style={{
      maxWidth: 720, margin: '30px auto', fontFamily: 'system-ui, Arial, sans-serif',
      padding: 16, border: '1px solid #ddd', borderRadius: 12
    }}>
      <h2 style={{ marginTop: 0 }}>Realtime Chat (Java 17 + Spring Boot + Redis)</h2>

      {/* connect controls */}
      {!isConnected ? (
        <div style={{ display: 'flex', gap: 8, marginBottom: 16 }}>
          <input
            placeholder="type your name"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            style={{ flex: 1, padding: 10, borderRadius: 8, border: '1px solid #ccc' }}
          />
          <button onClick={connect} style={{ padding: '10px 16px', borderRadius: 8, cursor: 'pointer' }}>
            Connect
          </button>
        </div>
      ) : (
        <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginBottom: 16 }}>
          <span style={{ fontSize: 14, color: '#1a7f37' }}>connected as <b>{username}</b></span>
          <button onClick={disconnect} style={{ marginLeft: 'auto', padding: '6px 10px', borderRadius: 8, cursor: 'pointer' }}>
            Disconnect
          </button>
        </div>
      )}

      {/* messages area */}
      <div style={{
        height: 360, overflowY: 'auto', border: '1px solid #eee',
        borderRadius: 8, padding: 12, background: '#fafafa', marginBottom: 12
      }}>
        {messages.length === 0 ? (
          <div style={{ color: '#888' }}>no messages yet… say hi 👋</div>
        ) : (
          messages.map((m, idx) => (
            <div key={idx} style={{ marginBottom: 8 }}>
              <div style={{ fontSize: 12, color: '#888' }}>{m.ts}</div>
              <div><b>{m.sender}:</b> {m.content}</div>
            </div>
          ))
        )}
      </div>

      {/* input area */}
      <div style={{ display: 'flex', gap: 8 }}>
        <input
          disabled={!isConnected}
          placeholder={isConnected ? "type a message…" : "connect first"}
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyDown={(e) => { if (e.key === 'Enter') sendMessage() }}
          style={{ flex: 1, padding: 10, borderRadius: 8, border: '1px solid #ccc' }}
        />
        <button
          disabled={!isConnected}
          onClick={sendMessage}
          style={{ padding: '10px 16px', borderRadius: 8, cursor: 'pointer' }}
        >
          Send
        </button>
      </div>

      {/* tiny typing indicator */}
      {typing && isConnected && (
        <div style={{ marginTop: 6, fontSize: 12, color: '#888' }}>
          typing…
        </div>
      )}
    </div>
  )
}