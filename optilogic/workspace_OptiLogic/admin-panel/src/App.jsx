

import React from 'react';
import { Routes, Route, Navigate } from 'react-router';

// Importa tus componentes de página
import Login from './pages/Login.jsx';
import Dashboard from './pages/Dashboard.jsx';
import Productos from './pages/Productos.jsx';


// El componente PrivateRoute lo dejamos listo
function PrivateRoute({children}) {
  const token = localStorage.getItem('token');
  // Por ahora lo dejamos público para probar (quitaremos el token en un momento)
  return token ? children : <Navigate to="/login" />; 
}

export default function App() {
  return (
    // ESTE ES EL CONTEXTO QUE NECESITA useNavigate()
  
      <Routes>
        {/* Hacemos la ruta de productos PÚBLICA temporalmente para la conexión */}
        <Route path="/productos" element={<Productos />} /> 
        
        {/* El componente Login.jsx ahora está dentro de un <Route>, que está dentro de <Routes>, que está dentro de <BrowserRouter> */}
        <Route path="/login" element={<Login />} />
        
        {/* Las rutas protegidas (las activaremos después) */}
        <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
        
        {/* Ruta por defecto: si no encuentra nada, redirige a /login */}
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    
  );
}