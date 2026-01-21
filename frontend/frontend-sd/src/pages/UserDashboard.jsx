import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { getPeople } from "../api/peopleApi";
import { getDevices } from "../api/devicesApi";
import { useAuth } from "../contexts/AuthContext";
import "../App.css";

export default function UserDashboard() {
    const { isUser, isAuthenticated } = useAuth();
    const [profile, setProfile] = useState(null);
    const [devices, setDevices] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!isAuthenticated || !isUser) {
            setLoading(false);
            setError("Access Denied for this role.");
            return;
        }

        const loadData = async () => {
            setLoading(true);
            setError(null);
            try {
                const peopleData = await getPeople();
                if (peopleData.length === 1) {
                    setProfile(peopleData[0]);
                } else if (peopleData.length === 0) {
                    setError("Profile data not found. Please contact support.");
                } else {
                    setError("Multiple profiles found, authentication error.");
                }

                const devicesData = await getDevices();
                setDevices(devicesData);

            } catch (err) {
                console.error("Dashboard data fetch error:", err);
                setError("Failed to load dashboard data. Please log out and log in again.");
            } finally {
                setLoading(false);
            }
        };

        loadData();
    }, [isAuthenticated, isUser]);

    if (loading) {
        return <div className="text-center mt-10 text-blue-600">Loading your data...</div>;
    }

    if (error) {
        return <div className="text-center mt-10 text-red-600">Error: {error}</div>;
    }

    const renderDeviceCard = (d) => (
        <div key={d.id} className="bg-white p-4 rounded-xl shadow-lg border border-gray-200">
            <h3 className="text-xl font-bold text-gray-800">{d.name}</h3>
            <p>Location: <span className="font-medium">{d.location}</span></p>
            <p className="mb-4">Max Consumption: <span className="font-medium">{d.maxConsumption} kWh</span></p>
            <Link
                to={`/monitor/${d.id}`}
                className="inline-block bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 transition duration-300"
            >
                Monitor
            </Link>
        </div>
    );

    return (
        <div className="p-6 max-w-7xl mx-auto">
            <h1 className="text-4xl font-extrabold text-gray-900 mb-8 border-b pb-2">User Dashboard</h1>

            <section className="mb-10 p-6 bg-blue-50 rounded-xl shadow-md">
                <h2 className="text-2xl font-semibold text-blue-800 mb-4">My Profile</h2>
                {profile ? (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-gray-700">
                        <p><span className="font-medium">Name:</span> {profile.name}</p>
                        <p><span className="font-medium">Age:</span> {profile.age}</p>
                        <p><span className="font-medium">Address:</span> {profile.address}</p>
                    </div>
                ) : (
                    <p className="text-red-500">Profile data missing.</p>
                )}
            </section>

            <section>
                <h2 className="text-2xl font-semibold text-gray-800 mb-4">My Devices ({devices.length})</h2>
                {devices.length > 0 ? (
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                        {devices.map(renderDeviceCard)}
                    </div>
                ) : (
                    <p className="text-gray-500">You currently have no devices assigned.</p>
                )}
            </section>
        </div>
    );
}