// frontend/components/MonitorDeviceModal.jsx (UPDATED CONTENT)

import React, { useState, useEffect, useMemo } from "react";
import { getDeviceMeasurements } from "../api/monitoringApi";
import EnergyChart from "./EnergyChart"; // NOU: Import EnergyChart

// Functie de agregare a datelor brute (value, timestamp) pe ore
const aggregateHourlyData = (measurements) => {
    const hourlyDataMap = new Map();

    measurements.forEach(item => {
        // Presupunem ca timestamp-ul este in format ISO-8601 (ex: "2025-12-11T16:00:00")
        const date = new Date(item.timestamp);

        // Cream cheia de agregare pe ZI si ORA (ex: "2025-12-11T16")
        // Trebuie sa folosim toISOString pentru a gestiona fusul orar corespunzator UTC,
        // dar pentru o implementare simplificata vom folosi un substring de data/ora.

        // Luam doar data si ora completa (YYYY-MM-DDTHH) pentru a grupa pe ore
        const yearMonthDayHour = date.toISOString().substring(0, 13); // Ex: "2025-12-11T16"
        const hourKey = yearMonthDayHour + ':00:00.000Z'; // Cheia finala: "2025-12-11T16:00:00.000Z"

        const currentData = hourlyDataMap.get(hourKey) || {
            hourTimestamp: hourKey,
            totalConsumption: 0
        };

        currentData.totalConsumption += item.value;
        hourlyDataMap.set(hourKey, currentData);
    });

    // Convertim Map-ul inapoi in array
    return Array.from(hourlyDataMap.values());
};


export default function MonitorDeviceModal({ deviceId, deviceName, onClose }) {
    const [measurements, setMeasurements] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const loadMeasurements = async () => {
        setLoading(true);
        setError(null);
        try {
            const data = await getDeviceMeasurements(deviceId);
            setMeasurements(data);
        } catch (err) {
            console.error("Monitoring data fetch error:", err);
            setError(`Failed to load data for device ${deviceName}. Check API URL and service status.`);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (deviceId) {
            loadMeasurements();
        }

        // Reîncărcăm datele la fiecare 5 secunde pentru "real-time"
        const intervalId = setInterval(loadMeasurements, 5000);

        return () => clearInterval(intervalId); // Curățăm intervalul la închiderea modalului
    }, [deviceId]);

    // Agregam datele doar cand se schimba measurements
    const aggregatedData = useMemo(() => aggregateHourlyData(measurements), [measurements]);


    if (!deviceId) return null;

    return (
        // Modal Backdrop (Rămâne neschimbat)
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
            {/* Modal Content */}
            <div className="bg-white p-6 rounded-xl shadow-2xl w-full max-w-2xl mx-4">
                <div className="flex justify-between items-center mb-4 border-b pb-2">
                    <h2 className="text-2xl font-bold text-gray-800">Monitoring: {deviceName}</h2>
                    <button onClick={onClose} className="text-gray-500 hover:text-gray-800 text-xl font-semibold">
                        &times;
                    </button>
                </div>

                {loading && <div className="text-center p-8 text-blue-600">Loading real-time data...</div>}

                {error && <div className="text-center p-8 text-red-600">Error: {error}</div>}

                {!loading && !error && aggregatedData.length > 0 && (
                    <div className="mt-4">
                        {/* FOLOSIM NOUA COMPONENTĂ DE GRAFIC CU DATELE AGREGATE */}
                        <EnergyChart data={aggregatedData} />

                        <div className="mt-4 text-xs text-gray-500 text-center">
                            * Data is aggregated hourly. The chart updates every 5 seconds.
                        </div>
                    </div>
                )}

                {!loading && !error && aggregatedData.length === 0 && (
                    <div className="text-center p-8 text-gray-500">No measurement data available for this device.</div>
                )}

                <div className="mt-4 text-center">
                    <button onClick={onClose} className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 transition">
                        Close
                    </button>
                </div>
            </div>
        </div>
    );
}