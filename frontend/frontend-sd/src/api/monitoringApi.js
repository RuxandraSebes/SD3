// frontend/frontend-sd/src/api/monitoringApi.js
const BASE_URL = "/monitoring";

const getAuthHeaders = () => {
    const token = sessionStorage.getItem("jwt");
    const headers = {};
    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }
    return headers;
};

export const getDeviceMeasurements = async (deviceId, startDate = null, endDate = null) => {
    try {
        let url = `${BASE_URL}/devices/${deviceId}/measurements`;

        // Add query parameters if provided
        const params = new URLSearchParams();
        if (startDate) params.append('startDate', startDate);
        if (endDate) params.append('endDate', endDate);

        if (params.toString()) {
            url += `?${params.toString()}`;
        }

        console.log('Fetching measurements from:', url);

        const res = await fetch(url, {
            headers: getAuthHeaders(),
        });

        console.log('Response status:', res.status);

        if (!res.ok) {
            const errorText = await res.text();
            console.error('Error response:', errorText);
            throw new Error(`Failed to fetch measurements (${res.status}): ${errorText}`);
        }

        const data = await res.json();
        console.log('Received measurements:', data.length);
        return data;
    } catch (error) {
        console.error('Monitoring API error:', error);
        throw error;
    }
};

export const getAllDeviceUuids = async () => {
    try {
        const res = await fetch(`${BASE_URL}/devices/uuids`, {
            headers: getAuthHeaders(),
        });

        if (!res.ok) {
            throw new Error('Failed to fetch device UUIDs');
        }

        return await res.json();
    } catch (error) {
        console.error('Error fetching device UUIDs:', error);
        throw error;
    }
};

// Health check function
export const checkMonitoringHealth = async () => {
    try {
        const res = await fetch(`${BASE_URL}/health`);
        return res.ok;
    } catch (error) {
        console.error('Monitoring service health check failed:', error);
        return false;
    }
};