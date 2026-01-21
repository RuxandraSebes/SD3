import React, { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useAuth } from '../contexts/AuthContext';

const ChatWidget = ({ isAdminView = false }) => {
    const { user, isAdmin, isAuthenticated } = useAuth();
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [isOpen, setIsOpen] = useState(false);
    const [connected, setConnected] = useState(false);
    const [activeChatUser, setActiveChatUser] = useState(null); // For admin to pick who to reply to
    const [notifications, setNotifications] = useState([]);
    const [showAlertModal, setShowAlertModal] = useState(false);
    const [activeAlert, setActiveAlert] = useState(null);

    const stompClient = useRef(null);
    const scrollRef = useRef(null);

    const currentUser = user?.username || 'Anonymous';

    useEffect(() => {
        if (!isAuthenticated) return;

        const socket = new SockJS('/chat/ws');
        const client = new Client({
            webSocketFactory: () => socket,
            onConnect: (frame) => {
                setConnected(true);
                console.log('Connected to WebSocket: ' + frame);

                // Subscribe to public chat
                if (!isAdmin) {
                    client.subscribe('/topic/public', (message) => {
                        const msg = JSON.parse(message.body);
                        if (msg.type === 'CHAT') {
                            setMessages(prev => [...prev, msg]);
                        }
                    });
                }

                client.subscribe('/topic/alerts', (message) => {
                    const msg = JSON.parse(message.body);
                    setActiveAlert(msg);
                    setShowAlertModal(true);
                    setNotifications(prev => [...prev, msg]);
                });

                // Subscribe to private messages/notifications for current user
                if (user && user.id) {
                    client.subscribe(`/topic/user.${user.id}`, (message) => {
                        const msg = JSON.parse(message.body);
                        if (msg.type === 'CHAT') {
                            setMessages(prev => [...prev, msg]);
                        } else if (msg.type === 'ALERT') {
                            setActiveAlert(msg);
                            setShowAlertModal(true);
                            setNotifications(prev => [...prev, msg]);
                        } else {
                            // Other types like JOIN/LEAVE could be handled if needed
                        }
                    });
                }

                // If admin, subscribe to admin topic
                if (isAdmin) {
                    client.subscribe('/topic/admin', (message) => {
                        const msg = JSON.parse(message.body);
                        if (msg.type === 'CHAT') {
                            setMessages(prev => [...prev, { ...msg, isAdminAlert: true }]);
                        }
                    });
                }

                // Register user
                client.publish({
                    destination: '/app/chat.addUser',
                    body: JSON.stringify({ sender: currentUser, type: 'JOIN' })
                });
            },
            onDisconnect: () => {
                setConnected(false);
                console.log('Disconnected from WebSocket');
            },
            onStompError: (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            },
            onWebSocketError: (event) => {
                console.error('WebSocket error observed:', event);
            }
        });

        client.activate();
        stompClient.current = client;

        return () => {
            if (stompClient.current) stompClient.current.deactivate();
        };
    }, [isAuthenticated, isAdmin, currentUser, user?.id]);

    useEffect(() => {
        if (scrollRef.current) {
            scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
        }
    }, [messages, notifications]);

    const sendMessage = (e) => {
        e.preventDefault();
        if (input.trim() && connected) {
            const chatMessage = {
                sender: currentUser,
                senderId: user?.id?.toString(),
                content: input,
                type: 'CHAT',
                to: isAdminView ? activeChatUser : null
            };

            stompClient.current.publish({
                destination: '/app/chat.sendMessage',
                body: JSON.stringify(chatMessage)
            });
            setInput('');
        }
    };

    if (!isAuthenticated) return null;

    return (
        <div className="chat-container" style={widgetStyle(isOpen)}>
            {/* Header */}
            <div
                className="chat-header"
                onClick={() => setIsOpen(!isOpen)}
                style={{ backgroundColor: '#1e293b', color: 'white', padding: '10px', cursor: 'pointer', display: 'flex', justifyContent: 'space-between', borderRadius: '8px 8px 0 0' }}
            >
                <span>{isAdminView ? "Admin Support Console" : "Customer Support"}</span>
                <span>{isOpen ? "▼" : "▲"}</span>
            </div>

            {isOpen && (
                <div className="chat-body" style={{ display: 'flex', flexDirection: 'column', height: '400px', backgroundColor: '#f8fafc' }}>

                    {/* Chat Messages */}
                    <div
                        ref={scrollRef}
                        style={{ flex: 1, overflowY: 'auto', padding: '10px' }}
                    >
                        {messages.map((msg, i) => (
                            <div
                                key={i}
                                style={{
                                    marginBottom: '10px',
                                    textAlign: msg.sender === currentUser ? 'right' : 'left',
                                    padding: '5px'
                                }}
                            >
                                <div style={{ fontSize: '10px', color: '#64748b' }}>{msg.sender}</div>
                                <div style={{
                                    display: 'inline-block',
                                    padding: '8px 12px',
                                    borderRadius: '12px',
                                    backgroundColor: msg.sender === currentUser ? '#3b82f6' : '#e2e8f0',
                                    color: msg.sender === currentUser ? 'white' : '#1e293b',
                                    maxWidth: '80%'
                                }}>
                                    {msg.content}
                                </div>
                                {isAdminView && msg.senderId && msg.senderId !== user?.id?.toString() && (
                                    <button
                                        onClick={() => setActiveChatUser(msg.senderId)}
                                        style={{
                                            fontSize: '9px',
                                            marginLeft: '5px',
                                            padding: '2px 4px',
                                            cursor: 'pointer',
                                            backgroundColor: '#3b82f6',
                                            color: 'white',
                                            border: 'none',
                                            borderRadius: '3px'
                                        }}
                                    >
                                        Reply
                                    </button>
                                )}
                                {msg.isAdminAlert && (
                                    <div style={{ fontSize: '10px', color: '#e11d48', marginTop: '2px' }}>* Requested Admin</div>
                                )}
                            </div>
                        ))}
                    </div>

                    {/* Admin User Selection */}
                    {isAdminView ? (
                        <div style={{ padding: '8px', backgroundColor: '#e2e8f0', borderTop: '1px solid #cbd5e1', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                            <span style={{ fontSize: '11px', fontWeight: 'bold' }}>
                                Target: {activeChatUser ? `User ID ${activeChatUser}` : "Public Chat"}
                            </span>
                            {activeChatUser && (
                                <button
                                    onClick={() => setActiveChatUser(null)}
                                    style={{ fontSize: '10px', padding: '2px 6px', backgroundColor: '#94a3b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                                >
                                    Switch to Public
                                </button>
                            )}
                        </div>
                    ) : (
                        <div style={{ padding: '5px', backgroundColor: '#e2e8f0', fontSize: '10px', color: '#475569' }}>
                            Connected as {currentUser}
                        </div>
                    )}

                    {/* Input Area */}
                    <form onSubmit={sendMessage} style={{ display: 'flex', padding: '10px', borderTop: '1px solid #e2e8f0' }}>
                        <input
                            type="text"
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            placeholder={isAdminView && activeChatUser ? `Private reply to ${activeChatUser}...` : "Type a message..."}
                            style={{ flex: 1, padding: '8px', border: '1px solid #cbd5e1', borderRadius: '4px', outline: 'none' }}
                        />
                        <button
                            type="submit"
                            style={{ marginLeft: '5px', backgroundColor: '#3b82f6', color: 'white', border: 'none', padding: '8px 12px', borderRadius: '4px' }}
                        >
                            Send
                        </button>
                    </form>
                </div>
            )}
            {/* Overconsumption Alert Modal */}
            {showAlertModal && activeAlert && (
                <div style={modalOverlayStyle}>
                    <div style={modalContentStyle}>
                        <div style={{ padding: '20px' }}>
                            <div style={{ display: 'flex', alignItems: 'center', marginBottom: '15px' }}>
                                <div style={{
                                    width: '40px',
                                    height: '40px',
                                    backgroundColor: '#fee2e2',
                                    borderRadius: '50%',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    marginRight: '15px'
                                }}>
                                    <span style={{ fontSize: '24px' }}>⚠️</span>
                                </div>
                                <h3 style={{ margin: 0, color: '#991b1b', fontSize: '18px' }}>Critical Alert</h3>
                            </div>

                            <p style={{ color: '#4b5563', fontSize: '14px', lineHeight: '1.5', marginBottom: '20px' }}>
                                {activeAlert.content}
                            </p>

                            <button
                                onClick={() => setShowAlertModal(false)}
                                style={{
                                    width: '100%',
                                    backgroundColor: '#991b1b',
                                    color: 'white',
                                    border: 'none',
                                    padding: '10px',
                                    borderRadius: '6px',
                                    fontWeight: 'bold',
                                    cursor: 'pointer',
                                    transition: 'background-color 0.2s'
                                }}
                                onMouseOver={(e) => e.target.style.backgroundColor = '#7f1d1d'}
                                onMouseOut={(e) => e.target.style.backgroundColor = '#991b1b'}
                            >
                                Acknowledge
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

const modalOverlayStyle = {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 2000,
    backdropFilter: 'blur(2px)'
};

const modalContentStyle = {
    backgroundColor: 'white',
    borderRadius: '12px',
    width: '90%',
    maxWidth: '400px',
    boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
    animation: 'modalFadeIn 0.3s ease-out'
};

const widgetStyle = (isOpen) => ({
    position: 'fixed',
    bottom: '20px',
    right: '20px',
    width: '350px',
    zIndex: 1000,
    boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)',
    borderRadius: '8px',
    overflow: 'hidden',
    transition: 'height 0.3s ease'
});

export default ChatWidget;
