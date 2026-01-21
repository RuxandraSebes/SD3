import React, { useState, useEffect, useMemo } from "react";
import { useParams } from "react-router-dom";
import { getDeviceMeasurements } from "../api/monitoringApi";
import EnergyChart from "../components/EnergyChart";

// Functie de agregare pe ore (copiata din Modal)
const aggregateHourlyData = (measurements) => {
    const hourlyDataMap = new Map();

    measurements.forEach(item => {
        const date = new Date(item.timestamp);
        // Agregare simplificata: YYYY-MM-DDTHH
        const yearMonthDayHour = date.toISOString().substring(0, 13);
        const hourKey = yearMonthDayHour + ':00:00.000Z';

        const currentData = hourlyDataMap.get(hourKey) || {
            hourTimestamp: hourKey,
            totalConsumption: 0
        };

        currentData.totalConsumption += item.value;
        hourlyDataMap.set(hourKey, currentData);
    });

    return Array.from(hourlyDataMap.values());
};

export default function MonitorDevice() {
    const { deviceId } = useParams();
    const [measurements, setMeasurements] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const loadMeasurements = async () => {
        // Nu setam loading la fiecare refresh pentru a evita flicker-ul chart-ului
        // Doar la initializare daca nu avem date
        if (measurements.length === 0) setLoading(true);

        setError(null);
        try {
            const data = await getDeviceMeasurements(deviceId);
            setMeasurements(data);
        } catch (err) {
            console.error("Monitoring data fetch error:", err);
            // Don't show error on every poll if it fails once, maybe just log it
            // setError(`Failed to load data for device ${deviceId}.`);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (deviceId) {
            loadMeasurements();
        }
        // Refresh every 5s
        const intervalId = setInterval(loadMeasurements, 5000);
        return () => clearInterval(intervalId);
    }, [deviceId]);

    const aggregatedData = useMemo(() => aggregateHourlyData(measurements), [measurements]);

    if (!deviceId) return <div className="text-red-600 p-6">Error: No device ID provided.</div>;

    return (
        <div className="p-6 max-w-4xl mx-auto">
            <h1 className="text-3xl font-bold text-gray-900 mb-6">Device Monitoring: {deviceId}</h1>

            <div className="bg-white p-6 rounded-xl shadow-lg border border-gray-200">
                <h2 className="text-xl font-semibold mb-4 text-gray-800">Hourly Energy Consumption</h2>

                {loading && measurements.length === 0 && <p className="text-blue-500">Loading initial data...</p>}

                {error && <p className="text-red-500">{error}</p>}

                {!loading && aggregatedData.length > 0 ? (
                    <>
                        <EnergyChart data={aggregatedData} />
                        <p className="mt-4 text-xs text-center text-gray-500">* Auto-refreshes every 5 seconds. Data is aggregated hourly.</p>
                    </>
                ) : (
                    !loading && measurements.length === 0 && <p className="text-gray-500 text-center py-10">No measurement data available yet. Start the simulator!</p>
                )}
            </div>
        </div>
    );
}