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