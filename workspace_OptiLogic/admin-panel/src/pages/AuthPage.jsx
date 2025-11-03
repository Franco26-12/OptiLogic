import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { User, Lock, Mail, Phone, UserCheck, AlertCircle, Package } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import api from '../services/api';

const AuthPage = () => {
  const navigate = useNavigate();
  const [isLogin, setIsLogin] = useState(true);
  const [loading, setLoading] = useState(false);
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
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      if (isLogin) {
        // Login
        const response = await api.post('/auth/login', {
          cedula: formData.cedula,
          password: formData.password
        });
        
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('user', JSON.stringify(response.data.usuario));
        toast.success('¡Bienvenido de nuevo!');
        navigate('/dashboard');
      } else {
        // Registro
        if (formData.password !== formData.confirmPassword) {
          toast.error('Las contraseñas no coinciden');
          setLoading(false);
          return;
        }

        const response = await api.post('/auth/registro', {
          cedula: formData.cedula,
          nombre: formData.nombre,
          apellido: formData.apellido,
          password: formData.password,
          telefono: formData.telefono,
          email: formData.email
        });
        
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('user', JSON.stringify(response.data.usuario));
        toast.success('¡Registro exitoso! Bienvenido a OptiLogic');
        navigate('/dashboard');
      }
    } catch (error) {
      toast.error(error.response?.data?.error || 'Error en la autenticación');
    } finally {
      setLoading(false);
    }
  };

  const switchMode = () => {
    setIsLogin(!isLogin);
    setFormData({
      cedula: '',
      nombre: '',
      apellido: '',
      password: '',
      confirmPassword: '',
      telefono: '',
      email: ''
    });
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4" style={{ background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}>
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-md"
      >
        {/* Logo y Título */}
        <div className="text-center mb-8">
          <motion.div
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            transition={{ delay: 0.2, type: "spring" }}
            className="inline-flex items-center justify-center w-20 h-20 bg-white rounded-full mb-4 shadow-lg"
          >
            <Package size={40} className="text-blue-600" />
          </motion.div>
          <h1 className="text-3xl font-bold text-white mb-2">OptiLogic</h1>
          <p className="text-blue-100">Sistema de Gestión Logística</p>
        </div>

        {/* Card del formulario */}
        <div className="bg-white rounded-2xl shadow-xl overflow-hidden">
          {/* Tabs */}
          <div className="flex bg-gray-50">
            <button
              onClick={() => setIsLogin(true)}
              className={`flex-1 py-3 text-center font-medium transition-all ${
                isLogin 
                  ? 'bg-white text-blue-600 border-b-2 border-blue-600' 
                  : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              Iniciar Sesión
            </button>
            <button
              onClick={() => setIsLogin(false)}
              className={`flex-1 py-3 text-center font-medium transition-all ${
                !isLogin 
                  ? 'bg-white text-blue-600 border-b-2 border-blue-600' 
                  : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              Registrarse
            </button>
          </div>

          {/* Formulario */}
          <form onSubmit={handleSubmit} className="p-6 space-y-4">
            <AnimatePresence mode="wait">
              {isLogin ? (
                <motion.div
                  key="login"
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: 20 }}
                  className="space-y-4"
                >
                  {/* Cédula */}
                  <div>
                    <label className="label">Cédula de Identidad</label>
                    <div className="relative">
                      <UserCheck className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                      <input
                        type="text"
                        name="cedula"
                        value={formData.cedula}
                        onChange={handleChange}
                        className="input pl-10"
                        placeholder="12345678"
                        required
                      />
                    </div>
                  </div>

                  {/* Contraseña */}
                  <div>
                    <label className="label">Contraseña</label>
                    <div className="relative">
                      <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                      <input
                        type="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        className="input pl-10"
                        placeholder="••••••••"
                        required
                      />
                    </div>
                  </div>

                  <div className="flex items-center justify-between text-sm">
                    <label className="flex items-center">
                      <input type="checkbox" className="mr-2" />
                      <span className="text-gray-600">Recordarme</span>
                    </label>
                    <a href="#" className="text-blue-600 hover:underline">
                      ¿Olvidaste tu contraseña?
                    </a>
                  </div>
                </motion.div>
              ) : (
                <motion.div
                  key="register"
                  initial={{ opacity: 0, x: 20 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: -20 }}
                  className="space-y-4"
                >
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
                    <div className="relative">
                      <UserCheck className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                      <input
                        type="text"
                        name="cedula"
                        value={formData.cedula}
                        onChange={handleChange}
                        className="input pl-10"
                        placeholder="12345678"
                        required
                      />
                    </div>
                  </div>

                  {/* Email */}
                  <div>
                    <label className="label">Email (opcional)</label>
                    <div className="relative">
                      <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                      <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        className="input pl-10"
                        placeholder="correo@ejemplo.com"
                      />
                    </div>
                  </div>

                  {/* Teléfono */}
                  <div>
                    <label className="label">Teléfono</label>
                    <div className="relative">
                      <Phone className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                      <input
                        type="tel"
                        name="telefono"
                        value={formData.telefono}
                        onChange={handleChange}
                        className="input pl-10"
                        placeholder="+1234567890"
                      />
                    </div>
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
                </motion.div>
              )}
            </AnimatePresence>

            {/* Botón Submit */}
            <button
              type="submit"
              disabled={loading}
              className="w-full btn btn-primary py-3"
            >
              {loading ? (
                <span className="animate-pulse">Procesando...</span>
              ) : (
                isLogin ? 'Iniciar Sesión' : 'Registrarse'
              )}
            </button>
          </form>

          {/* Footer */}
          <div className="px-6 pb-6">
            <div className="text-center text-sm text-gray-600">
              {isLogin ? (
                <>
                  ¿No tienes cuenta?{' '}
                  <button
                    onClick={switchMode}
                    className="text-blue-600 font-medium hover:underline"
                  >
                    Regístrate aquí
                  </button>
                </>
              ) : (
                <>
                  ¿Ya tienes cuenta?{' '}
                  <button
                    onClick={switchMode}
                    className="text-blue-600 font-medium hover:underline"
                  >
                    Inicia sesión
                  </button>
                </>
              )}
            </div>
          </div>
        </div>

        {/* Info adicional */}
        <div className="mt-6 text-center text-white text-sm">
          <p className="flex items-center justify-center gap-2 opacity-80">
            <AlertCircle size={16} />
            Sistema seguro con encriptación de datos
          </p>
        </div>
      </motion.div>
    </div>
  );
};

export default AuthPage;
