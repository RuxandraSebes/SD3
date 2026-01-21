import React, { useState } from "react";
import { useNavigate } from "react-router-dom"; 
import { useAuth } from "../contexts/AuthContext"; 

const initialForm = { 
    username: "", 
    password: "", 
    role: "user", 
    name: "", 
    age: "", 
    address: "" 
};

const Notification = ({ message, type }) => {
    if (!message) return null;
    const baseStyle = "p-3 rounded-lg text-sm mb-4 transition-opacity duration-300";
    const styles = type === 'success' 
        ? `${baseStyle} bg-green-100 text-green-700 border border-green-300`
        : `${baseStyle} bg-red-100 text-red-700 border border-red-300`;

    return <div className={styles}>{message}</div>;
};

export default function Auth() {
    const [isLogin, setIsLogin] = useState(true);
    const [form, setForm] = useState(initialForm);
    const [notification, setNotification] = useState({ message: "", type: "" });
    const [isLoading, setIsLoading] = useState(false);
    
    const navigate = useNavigate(); 
    const { login } = useAuth(); 

    const BASE_URL = "/auth"; 

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setNotification({ message: "", type: "" });
        setIsLoading(true);

        const endpoint = isLogin ? "/login" : "/register"; 
        
        let body;
        let headers = { "Content-Type": "application/json" };
        
        if (isLogin) {
            body = new URLSearchParams({ username: form.username, password: form.password });
            headers = { "Content-Type": "application/x-www-form-urlencoded" };
        } else {
            body = JSON.stringify({
                username: form.username,
                password: form.password,
                role: form.role,
                name: form.name,
                age: parseInt(form.age, 10), 
                address: form.address,
            });
        }

        try {
            const res = await fetch(`${BASE_URL}${endpoint}`, {
                method: "POST",
                headers: headers,
                body: body,
            });

            const text = await res.text();

            if (res.ok) {
                if (isLogin) {
                    const role = login(text); 
                    
                    if (role) {
                        setNotification({ message: `Login successful. Role: ${role}.`, type: "success" });
                        
                        if (role === 'admin') {
                            navigate('/people'); 
                        } else if (role === 'user') {
                            navigate('/dashboard'); 
                        }
                    } else {
                        setNotification({ message: "Login successful, but role could not be determined.", type: "error" });
                        navigate('/people'); 
                    }
                } else {
                    setNotification({ message: text, type: "success" });
                    if (text.toLowerCase().includes("success")) {
                        setTimeout(() => setIsLogin(true), 1500); 
                    }
                }
            } else {
                 setNotification({ message: `Error (${res.status}): ${text}`, type: "error" });
            }

        } catch (error) {
            setNotification({ message: `Network error: ${error.message}`, type: "error" });
        } finally {
            setIsLoading(false);
        }
    };
    
    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-50 p-4">
            <div className="bg-white p-8 rounded-xl shadow-2xl w-full max-w-md">
                <h2 className="text-3xl font-extrabold text-gray-900 mb-6 text-center">
                    {isLogin ? "Sign In" : "Create Account"}
                </h2>

                <Notification message={notification.message} type={notification.type} />

                <form onSubmit={handleSubmit} className="space-y-4">
                    <input
                        className="w-full p-3 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
                        placeholder="Username"
                        name="username"
                        value={form.username}
                        onChange={handleChange}
                        required
                    />
                    <input
                        className="w-full p-3 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
                        placeholder="Password"
                        type="password"
                        name="password"
                        value={form.password}
                        onChange={handleChange}
                        required
                    />
                    
                    {!isLogin && (
                        <>
                            <input
                                className="w-full p-3 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
                                placeholder="Full Name"
                                name="name"
                                value={form.name}
                                onChange={handleChange}
                                required
                            />
                            <input
                                className="w-full p-3 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
                                placeholder="Age"
                                type="number"
                                name="age"
                                value={form.age}
                                onChange={handleChange}
                                required
                            />
                            <input
                                className="w-full p-3 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
                                placeholder="Address"
                                name="address"
                                value={form.address}
                                onChange={handleChange}
                                required
                            />
                            
                            <select
                                className="w-full p-3 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500 bg-white"
                                name="role"
                                value={form.role}
                                onChange={handleChange}
                            >
                                <option value="user">User</option>
                                <option value="admin">Admin</option>
                            </select>
                        </>
                    )}

                    <button 
                        type="submit" 
                        className={`w-full p-3 font-semibold text-white rounded-lg transition duration-150 ${isLoading ? 'bg-blue-400' : 'bg-blue-600 hover:bg-blue-700'}`}
                        disabled={isLoading}
                    >
                        {isLoading ? "Processing..." : (isLogin ? "Login" : "Register")}
                    </button>
                </form>

                <p className="mt-6 text-center text-sm text-gray-600">
                    {isLogin ? (
                        <span>
                            Don't have an account?{" "}
                            <button
                                type="button"
                                onClick={() => setIsLogin(false)}
                                className="text-blue-600 hover:text-blue-500 font-medium transition duration-150"
                            >
                                Register here
                            </button>
                        </span>
                    ) : (
                        <span>
                            Already have an account?{" "}
                            <button
                                type="button"
                                onClick={() => setIsLogin(true)}
                                className="text-blue-600 hover:text-blue-500 font-medium transition duration-150"
                            >
                                Sign In
                            </button>
                        </span>
                    )}
                </p>
            </div>
        </div>
    );
}