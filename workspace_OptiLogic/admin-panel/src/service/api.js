const API_BASE = "http://localhost:8080/api";

let auth = null;

export function setAuth(credentials) {
  auth = credentials;
}

async function fetchWithAuth(url, options = {}) {
  const headers = options.headers || {};
  if (auth) {
    headers.Authorization = "Basic " + btoa(`${auth.user}:${auth.pass}`);
  }
  options.headers = headers;
  return fetch(url, options);
}

export async function fetchCategorias() {
  const response = await fetchWithAuth(`${API_BASE}/categorias`);
  if (!response.ok) throw new Error("Error fetch categorias");
  return response.json();
}

export async function createCategoria(categoria) {
  const response = await fetchWithAuth(`${API_BASE}/categorias`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(categoria),
  });
  if (!response.ok) throw new Error("Error crear categoria");
  return response.json();
}

export async function fetchProductos() {
  const response = await fetchWithAuth(`${API_BASE}/productos`);
  if (!response.ok) throw new Error("Error fetch productos");
  return response.json();
}

export async function createProducto(producto) {
  const response = await fetchWithAuth(`${API_BASE}/productos`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(producto),
  });
  if (!response.ok) throw new Error("Error crear producto");
  return response.json();
}
