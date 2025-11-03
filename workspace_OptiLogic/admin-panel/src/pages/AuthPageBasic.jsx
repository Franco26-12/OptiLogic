import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const AuthPageBasic = () => {
  const navigate = useNavigate();
  const [isLogin, setIsLogin] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [formData, setFormData] = useState({
    cedula: '',
    nombre: '',
    apellido: '',
    password: '',
    confirmPassword: '',
    telefono: '',
    email: ''
  });

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      if (isLogin) {
        // Login real
        const response = await fetch('http://localhost:8080/api/auth/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            cedula: formData.cedula,
            password: formData.password
          })
        });
        
        if (response.ok) {
          const data = await response.json();
          localStorage.setItem('token', data.token);
          localStorage.setItem('user', JSON.stringify(data.usuario));
          navigate('/dashboard');
        } else {
          const errorData = await response.json();
          setError(errorData.error || 'Error en login. Verifica tus credenciales.');
        }
      } else {
        // Registro - Solo para primer admin
        if (formData.password !== formData.confirmPassword) {
          setError('Las contraseñas no coinciden');
          setLoading(false);
          return;
        }

        const response = await fetch('http://localhost:8080/api/auth/registro', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            cedula: formData.cedula,
            nombre: formData.nombre,
            apellido: formData.apellido,
            password: formData.password,
            telefono: formData.telefono,
            email: formData.email
          })
        });
        
        if (response.ok) {
          const data = await response.json();
          localStorage.setItem('token', data.token);
          localStorage.setItem('user', JSON.stringify(data.usuario));
          navigate('/dashboard');
        } else {
          const errorData = await response.json();
          setError(errorData.error || 'Error en el registro. Verifica los datos.');
        }
      }
    } catch (error) {
      setError('Error de conexión. Verifica que el servidor esté activo.');
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4" 
         style={{ background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}>
      <div className="w-full max-w-md">
        {/* Logo y Título */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-20 h-20 bg-white rounded-full mb-4 shadow-lg">
            <div className="text-4xl">📦</div>
          </div>
          <h1 className="text-3xl font-bold text-white mb-2">OptiLogic</h1>
          <p className="text-blue-100">Sistema de Gestión Logística</p>
        </div>

        {/* Card del formulario */}
        <div className="bg-white rounded-2xl shadow-xl overflow-hidden">
          {/* Tabs */}
          <div className="flex bg-gray-50">
            <button
              onClick={() => { setIsLogin(true); setError(''); }}
              className={`flex-1 py-3 text-center font-medium transition-all ${
                isLogin 
                  ? 'bg-white text-blue-600 border-b-2 border-blue-600' 
                  : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              Iniciar Sesión
            </button>
            <button
              onClick={() => { setIsLogin(false); setError(''); }}
              className={`flex-1 py-3 text-center font-medium transition-all ${
                !isLogin 
                  ? 'bg-white text-blue-600 border-b-2 border-blue-600' 
                  : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              Registrarse
            </button>
          </div>

          {/* Mensaje de error */}
          {error && (
            <div className="mx-6 mt-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
              {error}
            </div>
          )}

          {/* Formulario */}
          <form onSubmit={handleSubmit} className="p-6 space-y-4">
            {isLogin ? (
              <div className="space-y-4">
                {/* Cédula */}
                <div>
                  <label className="label">Cédula de Identidad</label>
                  <input
                    type="text"
                    name="cedula"
                    value={formData.cedula}
                    onChange={handleChange}
                    className="input"
                    placeholder="12345678"
                    required
                  />
                </div>

                {/* Contraseña */}
                <div>
                  <label className="label">Contraseña</label>
                  <input
                    type="password"
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                    className="input"
                    placeholder="••••••••"
                    required
                  />
                </div>
              </div>
            ) : (
              <div className="space-y-4">
                {/* Grid de 2 columnas */}
                <div className="grid grid-cols-2 gap-4">
                  {/* Nombre */}
                  <div>
                    <label className="label">Nombre</label>
                    <input
                      type="text"
                      name="nombre"
                      value={formData.nombre}
                      onChange={handleChange}
                      className="input"
                      placeholder="Juan"
                      required
                    />
                  </div>

                  {/* Apellido */}
                  <div>
                    <label className="label">Apellido</label>
                    <input
                      type="text"
                      name="apellido"
                      value={formData.apellido}
                      onChange={handleChange}
                      className="input"
                      placeholder="Pérez"
                      required
                    />
                  </div>
                </div>

                {/* Cédula */}
                <div>
                  <label className="label">Cédula de Identidad</label>
                  <input
                    type="text"
                    name="cedula"
                    value={formData.cedula}
                    onChange={handleChange}
                    className="input"
                    placeholder="12345678"
                    required
                  />
                </div>

                {/* Email */}
                <div>
                  <label className="label">Email (opcional)</label>
                  <input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    className="input"
                    placeholder="correo@ejemplo.com"
                  />
                </div>

                {/* Teléfono */}
                <div>
                  <label className="label">Teléfono</label>
                  <input
                    type="tel"
                    name="telefono"
                    value={formData.telefono}
                    onChange={handleChange}
                    className="input"
                    placeholder="+1234567890"
                    required
                  />
                </div>

                {/* Grid contraseñas */}
                <div className="grid grid-cols-2 gap-4">
                  {/* Contraseña */}
                  <div>
                    <label className="label">Contraseña</label>
                    <input
                      type="password"
                      name="password"
                      value={formData.password}
                      onChange={handleChange}
                      className="input"
                      placeholder="••••••••"
                      required
                    />
                  </div>

                  {/* Confirmar Contraseña */}
                  <div>
                    <label className="label">Confirmar</label>
                    <input
                      type="password"
                      name="confirmPassword"
                      value={formData.confirmPassword}
                      onChange={handleChange}
                      className="input"
                      placeholder="••••••••"
                      required
                    />
                  </div>
                </div>
              </div>
            )}

            {/* Botón Submit */}
            <button
              type="submit"
              disabled={loading}
              className="w-full btn btn-primary py-3"
            >
              {loading ? (
                <span>Procesando...</span>
              ) : (
                isLogin ? 'Iniciar Sesión' : 'Registrarse'
              )}
            </button>
          </form>

          {/* Info Registro */}
          {!isLogin && (
            <div className="px-6 pb-3">
              <div className="p-3 bg-blue-50 rounded text-sm text-blue-800">
                <strong>Nota:</strong> El registro está disponible solo para el primer administrador del sistema.
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AuthPageBasic;
