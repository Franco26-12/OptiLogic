// En pages/Login.jsx

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../services';

// Asegúrate de que esta ruta sea correcta:
import '../App.css'; // Si App.css está en src/ (un nivel arriba)

export default function Login() {
  // En pages/Login.jsx


  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      // Usando la simulación temporal o la API real
      const data = await login(email, password);
      localStorage.setItem('token', data.token);
      navigate('/dashboard'); 
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    // 🚨 AÑADIMOS EL CONTENEDOR PRINCIPAL CON LA CLASE login-container
    <div className="login-container"> 
      {/* 🚨 AÑADIMOS LA CLASE login-form AL FORMULARIO */}
      <form onSubmit={handleSubmit} className="login-form"> 
        <h2>Iniciar Sesión - Admin</h2> {/* Título mejorado */}
        
        <input
          type="email"
          placeholder="Correo"
          value={email}
          onChange={e => setEmail(e.target.value)}
          required
        />

        <input
          type="password"
          placeholder="Contraseña"
          value={password}
          onChange={e => setPassword(e.target.value)}
          required
        />

        {/* 🚨 AÑADIMOS LA CLASE login-button */}
        <button type="submit" className="login-button">Ingresar</button>

        {/* 🚨 AÑADIMOS LA CLASE error-message */}
        {error && <p className="error-message">{error}</p>}
      </form>
    </div>
    // 🚨 FIN DEL CONTENEDOR
  );
}