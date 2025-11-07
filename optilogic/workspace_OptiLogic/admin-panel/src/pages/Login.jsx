import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { login } from '../services';
import '../App.css';

export default function Login() {
  const [cedula, setCedula] = useState('');
  const [nombre, setNombre] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      const data = await login(cedula, nombre, password);
      localStorage.setItem('token', data.accessToken);
      localStorage.setItem('nombreUsuario', data.nombre);
      navigate('/dashboard');
      alert(`Bienvenido ${data.nombre}`);
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="login-container"> 
      <form onSubmit={handleSubmit} className="login-form"> 
        <h2>Iniciar Sesión</h2>
        
        <label>Cédula:</label>
        <input
          type="text"
          placeholder="Número de cédula"
          value={cedula}
          onChange={e => setCedula(e.target.value)}
          required
        />

        <label>Nombre:</label>
        <input
          type="text"
          placeholder="Nombre"
          value={nombre}
          onChange={e => setNombre(e.target.value)}
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

        <button type="submit" className="login-button">Ingresar</button>

        {error && <p className="error-message">{error}</p>}
        
        <p style={{ marginTop: '20px', fontSize: '0.9em' }}>
          ¿No tienes cuenta? <Link to="/registro" style={{ color: '#007bff' }}>Regístrate aquí</Link>
        </p>
      </form>
    </div>
  );
}