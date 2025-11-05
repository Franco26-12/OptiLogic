import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { login } from '../services';
import '../App.css';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      const data = await login(email, password);
      localStorage.setItem('token', data.accessToken);
      console.log(data);
      navigate('/dashboard'); 
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="login-container"> 
      <form onSubmit={handleSubmit} className="login-form"> 
        <h2>Iniciar Sesión</h2>
        
        <label>Email:</label>
        <input
          type="email"
          placeholder="Correo Electrónico"
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

        <button type="submit" className="login-button">Ingresar</button>

        {error && <p className="error-message">{error}</p>}
        
        <p style={{ marginTop: '20px', fontSize: '0.9em' }}>
          ¿No tienes cuenta? <Link to="/registro" style={{ color: '#007bff' }}>Regístrate aquí</Link>
        </p>
      </form>
    </div>
  );
}