import React, { useState, useEffect } from "react";
import Select from "react-select";
import {
  getDevices,
  addDevice,
  updateDevice,
  deleteDevice,
} from "../api/devicesApi";
import { getPeople } from "../api/peopleApi";
import "../App.css";

export default function Devices() {
  const [devices, setDevices] = useState([]);
  const [users, setUsers] = useState([]);
  const [form, setForm] = useState({
    id: null,
    name: "",
    location: "",
    maxConsumption: "",
    assignedUserIds: [],
  });

  useEffect(() => {
    loadDevices();
    loadUsers();
  }, []);

  const loadDevices = async () => {
    try {
      const data = await getDevices();
      const normalized = data.map((d) => ({
        ...d,
        assignedUserIds: Array.isArray(d.assignedUserIds)
          ? d.assignedUserIds
          : d.assignedUserIds
            ? [d.assignedUserIds]
            : [],
      }));
      setDevices(normalized);
    } catch (err) {
      console.error(err);
    }
  };

  const loadUsers = async () => {
    try {
      const data = await getPeople();
      setUsers(data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.name || !form.maxConsumption || !form.location) {
      alert("Please fill in all fields");
      return;
    }

    const deviceToSave = {
      ...form,
      assignedUserIds: form.assignedUserIds || [],
    };

    if (form.id) {
      await updateDevice(deviceToSave);
    } else {
      await addDevice(deviceToSave);
    }

    setForm({
      id: null,
      name: "",
      location: "",
      maxConsumption: "",
      assignedUserIds: [],
    });
    loadDevices();
  };

  const handleEdit = (device) => {
    setForm({
      ...device,
      assignedUserIds: device.assignedUserIds || [],
    });
  };

  const handleDelete = async (id) => {
    await deleteDevice(id);
    loadDevices();
  };


  const userOptions = users.map((u) => ({ value: u.id, label: u.name }));

  return (
    <div>

      <div >
        <h2>{form.id ? "Edit Device" : "Add Device"}</h2>
        <form
          onSubmit={handleSubmit}
          style={{
            display: "flex",
            gap: "0.5rem",
            flexWrap: "wrap",
            alignItems: "center",
          }}
        >
          <input
            placeholder="Name"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
          />
          <input
            placeholder="Max Consumption"
            type="number"
            value={form.maxConsumption}
            onChange={(e) =>
              setForm({ ...form, maxConsumption: e.target.value })
            }
            style={{ width: "150px" }}
          />
          <input
            placeholder="Location"
            value={form.location}
            onChange={(e) => setForm({ ...form, location: e.target.value })}
          />

          <div style={{ minWidth: "250px", flexGrow: 1 }}>
            <Select
              isMulti
              options={userOptions}
              value={userOptions.filter((o) =>
                form.assignedUserIds.includes(o.value)
              )}
              onChange={(selected) =>
                setForm({
                  ...form,
                  assignedUserIds: selected ? selected.map((s) => s.value) : [],
                })
              }
              placeholder="Assign users (optional)"
              isClearable
              noOptionsMessage={() => "No users found"}
            />
          </div>

          <button type="submit">{form.id ? "Update" : "Add"}</button>
        </form>
      </div>

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fill, minmax(250px, 1fr))",
          gap: "1rem",
        }}
      >
        {devices.length > 0 ? (
          devices.map((d) => (
            <div key={d.id} >
              <div>
                <h3>{d.name}</h3>
                <p>Max Consumption: {d.maxConsumption}</p>
                <p>Location: {d.location}</p>
                <p>
                  Assigned Users:{" "}
                  {d.assignedUserIds && d.assignedUserIds.length > 0
                    ? d.assignedUserIds
                      .map(
                        (uid) =>
                          users.find((u) => u.id === uid)?.name ||
                          "(Unknown user)"
                      )
                      .join(", ")
                    : "None"}
                </p>
              </div>
              <div >
                <button onClick={() => handleEdit(d)}>
                  Edit
                </button>
                <button onClick={() => handleDelete(d.id)}>
                  Delete
                </button>
              </div>
            </div>
          ))
        ) : (
          <p>No devices found. Add one above!</p>
        )}
      </div>
    </div>
  );
}