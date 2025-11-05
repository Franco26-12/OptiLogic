const API_URL = 'http://localhost:8080/api';

const authHeaders = () => {
  const token = localStorage.getItem('token');
  if (!token) {
    throw new Error('No se encontró token. Inicia sesión nuevamente.');
  }

  return {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
  };
};

const manejarRespuesta = async (response, mensajeError) => {
  if (!response.ok) {
    let mensaje = mensajeError;
    try {
      const errorData = await response.json();
      mensaje = errorData.message || mensaje;
    } catch (e) {
      // mantener mensaje por defecto
    }
    throw new Error(mensaje);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
};

export const fetchProductos = async () => {
  const response = await fetch(`${API_URL}/admin/productos`, {
    headers: authHeaders(),
  });

  return manejarRespuesta(response, 'No se pudieron obtener los productos.');
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

export const obtenerDashboard = async () => {
  const response = await fetch(`${API_URL}/admin/dashboard`, {
    headers: authHeaders(),
  });

  return manejarRespuesta(response, 'No se pudieron cargar las estadísticas.');
};

export const obtenerCategorias = async () => {
  const response = await fetch(`${API_URL}/admin/categorias`, {
    headers: authHeaders(),
  });

  return manejarRespuesta(response, 'No se pudieron obtener las categorías.');
};

export const crearCategoria = async (categoria) => {
  const response = await fetch(`${API_URL}/admin/categorias`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(categoria),
  });

  return manejarRespuesta(response, 'No se pudo crear la categoría.');
};

export const actualizarCategoria = async (id, categoria) => {
  const response = await fetch(`${API_URL}/admin/categorias/${id}`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(categoria),
  });

  return manejarRespuesta(response, 'No se pudo actualizar la categoría.');
};

export const borrarCategoria = async (id) => {
  const response = await fetch(`${API_URL}/admin/categorias/${id}`, {
    method: 'DELETE',
    headers: authHeaders(),
  });

  await manejarRespuesta(response, 'No se pudo eliminar la categoría.');
};

export const crearProducto = async (producto) => {
  const response = await fetch(`${API_URL}/admin/productos`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(producto),
  });

  return manejarRespuesta(response, 'No se pudo crear el producto.');
};

export const actualizarProducto = async (id, producto) => {
  const response = await fetch(`${API_URL}/admin/productos/${id}`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(producto),
  });

  return manejarRespuesta(response, 'No se pudo actualizar el producto.');
};

export const borrarProducto = async (id) => {
  const response = await fetch(`${API_URL}/admin/productos/${id}`, {
    method: 'DELETE',
    headers: authHeaders(),
  });

  await manejarRespuesta(response, 'No se pudo eliminar el producto.');
};

export const obtenerUsuarios = async () => {
  const response = await fetch(`${API_URL}/admin/usuarios`, {
    headers: authHeaders(),
  });

  return manejarRespuesta(response, 'No se pudieron obtener los usuarios.');
};

export const crearUsuario = async (usuario) => {
  const response = await fetch(`${API_URL}/admin/usuarios`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(usuario),
  });

  return manejarRespuesta(response, 'No se pudo crear el usuario.');
};

export const actualizarUsuario = async (id, usuario) => {
  const response = await fetch(`${API_URL}/admin/usuarios/${id}`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(usuario),
  });

  return manejarRespuesta(response, 'No se pudo actualizar el usuario.');
};

export const borrarUsuario = async (id) => {
  const response = await fetch(`${API_URL}/admin/usuarios/${id}`, {
    method: 'DELETE',
    headers: authHeaders(),
  });

  await manejarRespuesta(response, 'No se pudo eliminar el usuario.');
};
