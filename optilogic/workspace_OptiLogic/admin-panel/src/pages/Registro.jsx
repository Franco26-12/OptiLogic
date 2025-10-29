import React, { useState } from "react";
import { Link, useNavigate } from 'react-router-dom'; // Importamos Link y useNavigate
import '../App.css'; // Importamos estilos
// NOTA: Debes crear la función 'registrar' en tu service/api.js
// import { registrar } from '../services'; 

export default function Registro() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(null); // Para mostrar mensajes
  const navigate = useNavigate();
  
  // Asumimos que el primer usuario registrado será ADMIN (para el backend)
  const rol = "ADMIN"; 

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setMessage(null);

    const usuario = { email, password, rol };
    
    // 🚨 CORRECCIÓN: Usar API_URL y endpoint real del backend 🚨
    const REGISTER_URL = 'http://localhost:8080/api/auth/register'; 

    try {
      const response = await fetch(REGISTER_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(usuario),
      });

      if (response.ok) {
        // En lugar de alert(), mostramos mensaje en la interfaz y redirigimos
        setMessage("✅ Registro exitoso. Iniciando sesión...");
        
        // Redirigir al login después de un breve momento
        setTimeout(() => {
            navigate('/login'); 
        }, 1500);

      } else {
        const errorData = await response.json();
        setError(errorData.message || "Error desconocido al registrar.");
      }
    } catch (error) {
      console.error("Error de conexión:", error);
      setError("Error de conexión. Revisa el backend.");
    }
  };

  return (
    // Reutilizamos el diseño del Login
    <div className="login-container"> 
      <form onSubmit={handleSubmit} className="login-form">
        <h2>Registro de Administrador</h2>
        
        <label>Email:</label>
        <input 
          type="email" 
          placeholder="Correo"
          value={email} 
          onChange={e => setEmail(e.target.value)} 
          required 
        />

        <label>Contraseña:</label>
        <input 
          type="password" 
          placeholder="Contraseña"
          value={password} 
          onChange={e => setPassword(e.target.value)} 
          required 
        />

        <button type="submit" className="login-button">Registrar</button>
        
        {/* Mostrar mensajes */}
        {error && <p className="error-message">{error}</p>}
        {message && <p className="success-message">{message}</p>}

        <p style={{ marginTop: '20px', fontSize: '0.9em' }}>
            ¿Ya tienes cuenta? <Link to="/login" style={{ color: '#007bff' }}>Inicia Sesión</Link>
        </p>
      </form>
    </div>
  );
}




