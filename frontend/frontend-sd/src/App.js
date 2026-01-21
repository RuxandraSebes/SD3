import React from "react";
import { BrowserRouter as Router, Routes, Route, NavLink, Navigate } from "react-router-dom";
import People from "./pages/People";
import Devices from "./pages/Devices";
import Auth from "./pages/Auth";
import Dashboard from "./pages/UserDashboard";
import MonitorDevice from "./pages/MonitorDevice";
import ChatWidget from "./components/ChatWidget";
import { AuthProvider, useAuth } from "./contexts/AuthContext";
import "./App.css";

const Navigation = () => {
  const { isAdmin, isAuthenticated, logout } = useAuth();

  const handleLogout = () => {
    logout();
    window.location.href = "/";
  };

  if (!isAuthenticated) return null;

  return (
    <div className="flex space-x-4">
      {isAdmin ? (
        <>
          <NavLink to="/people" className={({ isActive }) => isActive ? "font-bold text-blue-300" : "hover:text-gray-300"}>People (Admin)</NavLink>
          <NavLink to="/devices" className={({ isActive }) => isActive ? "font-bold text-blue-300" : "hover:text-gray-300"}>Devices (Admin)</NavLink>
        </>
      ) : (
        <NavLink to="/dashboard" className={({ isActive }) => isActive ? "font-bold text-blue-300" : "hover:text-gray-300"}>My Dashboard</NavLink>
      )}
      <button
        onClick={handleLogout}
        className="text-sm font-medium text-red-400 hover:text-red-300 transition duration-150"
      >
        Logout
      </button>
    </div>
  );
};

const ProtectedRoute = ({ children }) => {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/" replace />;
  }
  return children;
};

export default function AppWrapper() {
  return (
    <Router>
      <AuthProvider>
        <App />
      </AuthProvider>
    </Router>
  );
}

function App() {
  const { isAuthenticated, isAdmin } = useAuth();

  const handleRootRedirect = () => {
    if (isAuthenticated) {
      return <Navigate to={isAdmin ? "/people" : "/dashboard"} replace />;
    }
    return <Auth />;
  }

  return (
    <div className="flex flex-col min-h-screen">
      <header className="flex justify-between items-center bg-gray-800 text-white p-4 shadow-md">
        <h1 className="text-xl font-bold">Smart Management</h1>
        <Navigation />
      </header>

      <main className="flex-grow p-4">
        <Routes>
          <Route path="/" element={handleRootRedirect()} />

          <Route path="/people" element={<ProtectedRoute><People /></ProtectedRoute>} />
          <Route path="/devices" element={<ProtectedRoute><Devices /></ProtectedRoute>} />

          <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
          <Route path="/monitor/:deviceId" element={<ProtectedRoute><MonitorDevice /></ProtectedRoute>} />

        </Routes>
      </main>
      <ChatWidget isAdminView={isAdmin} />
    </div>
  );
}