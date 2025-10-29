const API_BASE_URL = 'http://localhost:8080/api'

export async function fetchProductos(token) {
  const response = await fetch(`${API_BASE_URL}/productos`, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  })
  if (!response.ok) throw new Error('Error al cargar productos')
  return response.json()
}
