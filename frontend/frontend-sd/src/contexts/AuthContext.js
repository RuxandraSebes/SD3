import React, { createContext, useState, useContext, useEffect } from 'react';

const AuthContext = createContext(null);

const decodeJwt = (token) => {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const payload = JSON.parse(atob(base64));
        return payload;
    } catch (e) {
        return null;
    }
};

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);

    useEffect(() => {
        const token = sessionStorage.getItem('jwt');
        if (token) {
            const payload = decodeJwt(token);
            if (payload) {
                setUser({
                    id: payload.userId,
                    username: payload.sub,
                    role: payload.role?.toLowerCase()
                });
            }
        }
    }, []);

    const login = (token) => {
        sessionStorage.setItem('jwt', token);
        const payload = decodeJwt(token);
        if (payload) {
            const userData = {
                id: payload.userId,
                username: payload.sub,
                role: payload.role?.toLowerCase()
            };
            setUser(userData);
            return userData.role;
        }
        setUser(null);
        return null;
    };

    const logout = () => {
        sessionStorage.removeItem('jwt');
        setUser(null);
    };

    const userRole = user?.role;
    const isAdmin = userRole === 'admin';
    const isUser = userRole === 'user';
    const isAuthenticated = user !== null;

    return (
        <AuthContext.Provider value={{ user, userRole, isAdmin, isUser, isAuthenticated, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);