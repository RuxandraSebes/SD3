const BASE_URL = "/devices";

const getAuthHeaders = (contentType) => {
  const token = sessionStorage.getItem("jwt");
  const headers = {};
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }
  if (contentType) {
    headers["Content-Type"] = contentType;
  }
  return headers;
};

export const getDevices = async () => {
  const res = await fetch(BASE_URL, {
    headers: getAuthHeaders(),
  });
  if (!res.ok) throw new Error("Failed to fetch devices");
  return await res.json();
};

export const addDevice = async (device) => {
  const res = await fetch(BASE_URL, {
    method: "POST",
    headers: getAuthHeaders("application/json"),
    body: JSON.stringify(device),
  });
  if (!res.ok) throw new Error("Failed to add device");
  return await res.json();
};

export const updateDevice = async (device) => {
  const res = await fetch(`${BASE_URL}/${device.id}`, {
    method: "PUT",
    headers: getAuthHeaders("application/json"),
    body: JSON.stringify(device),
  });
  if (!res.ok) throw new Error("Failed to update device");
  return res.json();
};

export const deleteDevice = async (id) => {
  const res = await fetch(`${BASE_URL}/${id}`, {
    method: "DELETE",
    headers: getAuthHeaders(),
  });
  if (!res.ok) throw new Error("Failed to delete device");
};