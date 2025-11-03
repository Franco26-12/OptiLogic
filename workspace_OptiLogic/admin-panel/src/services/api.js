import axios from 'axios';
import toast from 'react-hot-toast';

// Configuración base de API
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para agregar token a las peticiones
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para manejar respuestas y errores
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expirado o inválido
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
      toast.error('Sesión expirada. Por favor, inicia sesión nuevamente.');
    }
    return Promise.reject(error);
  }
);

// Servicios de Auth
export const authService = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (data) => api.post('/auth/registro', data),
  validateToken: () => api.get('/auth/validar-token'),
  changePassword: (data) => api.post('/auth/cambiar-password', data),
};

// Servicios de Productos
export const productosService = {
  getAll: () => api.get('/productos'),
  getById: (id) => api.get(`/productos/${id}`),
  getBySku: (sku) => api.get(`/productos/sku/${sku}`),
  getByCategoria: (categoriaId) => api.get(`/productos/categoria/${categoriaId}`),
  getStockBajo: () => api.get('/productos/stock-bajo'),
  getSinStock: () => api.get('/productos/sin-stock'),
  getEstadisticas: () => api.get('/productos/estadisticas'),
  create: (data) => api.post('/productos', data),
  update: (id, data) => api.put(`/productos/${id}`, data),
  updateStock: (id, cantidad, tipoOperacion) => 
    api.put(`/productos/${id}/stock`, null, { params: { cantidad, tipoOperacion } }),
  delete: (id) => api.delete(`/productos/${id}`),
};

// Servicios de Categorías
export const categoriasService = {
  getAll: () => api.get('/categorias'),
  getById: (id) => api.get(`/categorias/${id}`),
  create: (data) => api.post('/categorias', data),
  update: (id, data) => api.put(`/categorias/${id}`, data),
  delete: (id) => api.delete(`/categorias/${id}`),
};

// Servicios de Envíos
export const enviosService = {
  getAll: () => api.get('/envios'),
  getById: (id) => api.get(`/envios/${id}`),
  getByTracking: (numero) => api.get(`/envios/tracking/${numero}`),
  getByQR: (codigoQR) => api.get(`/envios/qr/${codigoQR}`),
  getByRepartidor: (repartidorId) => api.get(`/envios/repartidor/${repartidorId}`),
  getByCliente: (clienteId) => api.get(`/envios/cliente/${clienteId}`),
  getByEstado: (estado) => api.get(`/envios/estado/${estado}`),
  getEstadisticas: () => api.get('/envios/estadisticas'),
  create: (data, adminId) => api.post('/envios', data, { params: { adminId } }),
  updateEstado: (data) => api.put('/envios/estado', data),
  asignarRepartidor: (envioId, repartidorId) => 
    api.put(`/envios/${envioId}/asignar-repartidor`, null, { params: { repartidorId } }),
  escanearQR: (data) => api.post('/envios/escanear-qr', data),
};

// Servicios de Usuarios
export const usuariosService = {
  getAll: () => api.get('/usuarios'),
  getById: (id) => api.get(`/usuarios/${id}`),
  getRepartidores: () => api.get('/usuarios/repartidores'),
  getClientes: () => api.get('/usuarios/clientes'),
  createRepartidor: (data) => api.post('/usuarios/repartidor', data),
  createCliente: (data) => api.post('/usuarios/cliente', data),
  update: (id, data) => api.put(`/usuarios/${id}`, data),
  toggleActivo: (id) => api.put(`/usuarios/${id}/toggle-activo`),
};

// Servicios de Dashboard
export const dashboardService = {
  getEstadisticas: () => api.get('/dashboard/estadisticas'),
  getResumen: () => api.get('/dashboard/resumen'),
  getActividadReciente: () => api.get('/dashboard/actividad-reciente'),
};

export default api;
