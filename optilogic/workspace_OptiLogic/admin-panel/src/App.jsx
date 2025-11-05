import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';

import Login from './pages/Login.jsx';
import Dashboard from './pages/Dashboard.jsx';
import Productos from './pages/Productos.jsx';
import Registro from './pages/Registro.jsx';
import Categoria from './pages/Categoria.jsx';
import AdminLayout from './components/AdminLayout.jsx';

function PrivateRoute({ children }) {
  const token = localStorage.getItem('token');
  return token ? children : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <Routes>
      {/* Rutas públicas */}
      <Route path="/registro" element={<Registro />} />
      <Route path="/login" element={<Login />} />

      {/* Rutas protegidas con layout admin */}
      <Route
        path="/"
        element={
          <PrivateRoute>
            <AdminLayout />
          </PrivateRoute>
        }
      >
        <Route index element={<Navigate to="dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="productos" element={<Productos />} />
        <Route path="categorias" element={<Categoria />} />
      </Route>

      {/* Ruta comodín */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
