import React, { useState, useEffect } from "react";
import {
  getPeople,
  addPerson,
  updatePerson,
  deletePerson,
} from "../api/peopleApi";
import "../App.css";

export default function People() {
  const [people, setPeople] = useState([]);
  const [form, setForm] = useState({ id: null, name: "", age: "", address: "" });

  const loadPeople = async () => {
    try {
      const data = await getPeople();
      setPeople(data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    loadPeople();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.name || !form.age || !form.address) {
      alert("Please fill in all fields");
      return;
    }
    if (form.id) {
      // Update existing person
      await updatePerson(form);
    } else {
      // For creating a new person, use Auth Service register endpoint
      // This ensures the user is created in Auth DB (login) AND People DB AND Device DB (sync)
      // We use the name as the username and default password "1234"
      const generatedUsername = form.name;

      const authPayload = {
        username: generatedUsername,
        password: "1234",
        role: "user", // Default role
        name: form.name,
        age: parseInt(form.age, 10),
        address: form.address
      };

      try {
        const res = await fetch("/auth/register", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            // Include token if Admin is logged in, though Register is usually public, 
            // but we might want to ensure we are creating it as "User" role for clients
            "Authorization": `Bearer ${sessionStorage.getItem("jwt")}`
          },
          body: JSON.stringify(authPayload)
        });

        if (!res.ok) {
          const text = await res.text();
          alert("Error creating user/person: " + text);
          return;
        }
        alert(`User created! Username: ${generatedUsername}, Password: 1234`);

      } catch (err) {
        console.error(err);
        alert("Failed to create user via Auth Service");
        return;
      }
    }
    setForm({ id: null, name: "", age: "", address: "" });
    loadPeople();
  };

  const handleEdit = (person) => {
    setForm(person);
  };

  const handleDelete = async (id) => {
    await deletePerson(id);
    loadPeople();
  };

  return (
    <div>
      <div >
        <h2>{form.id ? "Edit Person" : "Add Person"}</h2>
        <form onSubmit={handleSubmit} style={{ display: "flex", gap: "0.5rem", flexWrap: "wrap" }}>
          <input
            placeholder="Name"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
          />
          <input
            placeholder="Age"
            type="number"
            value={form.age}
            onChange={(e) => setForm({ ...form, age: e.target.value })}
            style={{ width: "80px" }}
          />
          <input
            placeholder="Address"
            value={form.address}
            onChange={(e) => setForm({ ...form, address: e.target.value })}
          />
          <button type="submit">{form.id ? "Update" : "Add"}</button>
        </form>
      </div>

      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(250px, 1fr))", gap: "1rem" }}>
        {people.length > 0 ? (
          people.map((p) => (
            <div key={p.id} >
              <div>
                <h3>{p.name}</h3>
                <p>Age: {p.age}</p>
                <p>Address: {p.address}</p>
              </div>
              <div >
                <button onClick={() => handleEdit(p)}>Edit</button>
                <button onClick={() => handleDelete(p.id)}>Delete</button>
              </div>
            </div>
          ))
        ) : (
          <p>No people found. Add someone above!</p>
        )}
      </div>
    </div>
  );
}
