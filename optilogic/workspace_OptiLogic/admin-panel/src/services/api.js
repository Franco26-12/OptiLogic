const API_URL = 'http://localhost:8080/api';

export const fetchProductos = () => {
  return fetch(`${API_URL}/productos`)
    .then(response => {
      if (!response.ok) {
        throw new Error('Error de red al intentar obtener productos');
      }
      return response.json();
    })
    .catch(error => {
      console.error("Error de conexión. Revisa que Eclipse esté corriendo y que la config de CORS esté aplicada.", error);
      return [];
    });
};

export const login = async (email, password) => {
  const response = await fetch(`${API_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });

  if (!response.ok) {
    let errorMessage = 'Error de autenticación desconocido';
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || errorMessage;
    } catch (e) {
      errorMessage = 'Credenciales inválidas o Error interno del servidor.';
    }
    throw new Error(errorMessage);
  }

  const data = await response.json();
  return data;
};

// Stubs para las funciones aún no implementadas:
export const crearProducto = async () => {
  throw new Error('crearProducto no implementado aún');
};

export const actualizarProducto = async () => {
  throw new Error('actualizarProducto no implementado aún');
};

export const borrarProducto = async () => {
  throw new Error('borrarProducto no implementado aún');
};
