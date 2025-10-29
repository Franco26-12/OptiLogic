import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom'; // 🚨 Nota: Usar 'react-router-dom', no solo 'react-router'

// Importa tus componentes de página
import Login from './pages/Login.jsx';
import Dashboard from './pages/Dashboard.jsx';
import Productos from './pages/Productos.jsx';
import Registro from './pages/Registro.jsx';

function PrivateRoute({children}) {
  const token = localStorage.getItem('token');
  // Aquí puedes agregar la lógica para verificar el rol (userRole === 'ADMIN')
  return token ? children : <Navigate to="/login" />; 
}

export default function App() {
  return (
    // 🚨 Falta <BrowserRouter> en el bloque de código que me enviaste. 
    // Si lo tienes en tu main.jsx, está bien. Si no, debe ir aquí.
    <Routes>
      
      {/* 1. RUTAS PÚBLICAS (Deben ir al principio) */}
      <Route path="/registro" element={<Registro />} />
      <Route path="/login" element={<Login />} />
      
      {/* RUTA DE INICIO: Cuando la URL es solo '/'. Redirige a /registro */}
      <Route path="/" element={<Navigate to="/registro" replace />} />

      {/* 2. RUTAS PROTEGIDAS */}
      <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
      
      {/* NOTA: Productos NO debe ser pública si está en el Dashboard */}
      <Route path="/productos" element={<PrivateRoute><Productos /></PrivateRoute>} />
      
      {/* 3. RUTA COMODÍN: Si la URL no coincide con ninguna anterior, redirige a /login */}
      <Route path="*" element={<Navigate to="/login" replace />} />

    </Routes>
    
    // 🚨 Si <BrowserRouter> no está en main.jsx, envuelve <Routes> con <BrowserRouter>
  );
}
