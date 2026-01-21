const BASE_URL = "/auth";

async function register(username, password, role) {
  try {
    const response = await fetch(`${BASE_URL}/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({
        username: username,
        password: password,
        role: role,
      }),
    });

    const data = await response.text();
    console.log("Register response:", data);
  } catch (error) {
    console.error("Register error:", error);
  }
}

async function login(username, password) {
  try {
    const response = await fetch(`${BASE_URL}/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({
        username: username,
        password: password,
      }),
    });

    const token = await response.text();
    console.log("Login success! Token:", token);

    sessionStorage.setItem("jwt", token);
  } catch (error) {
    console.error("Login error:", error);
  }
}

