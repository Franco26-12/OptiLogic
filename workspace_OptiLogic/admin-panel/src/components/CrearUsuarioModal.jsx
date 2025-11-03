import React, { useState } from 'react';

const CrearUsuarioModal = ({ tipo, onClose, onSuccess }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [formData, setFormData] = useState({
    cedula: '',
    nombre: '',
    apellido: '',
    password: '',
    telefono: '',
    email: '',
    // Campos específicos para repartidor
    licenciaConducir: '',
    vehiculoAsignado: '',
    zonaAsignada: ''
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
      const endpoint = tipo === 'admin' ? '/usuarios/admin' : '/usuarios/repartidor';
      const token = localStorage.getItem('token');
      
      const response = await fetch(`http://localhost:8080/api${endpoint}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(formData)
      });

      if (response.ok) {
        onSuccess();
        onClose();
      } else {
        const errorData = await response.json();
        setError(errorData.error || 'Error al crear usuario');
      }
    } catch (error) {
      setError('Error de conexión con el servidor');
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full mx-4 max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-bold text-gray-800">
              Crear {tipo === 'admin' ? 'Administrador' : 'Repartidor'}
            </h2>
            <button
              onClick={onClose}
              className="text-gray-500 hover:text-gray-700 text-2xl"
            >
              ×
            </button>
          </div>

          {error && (
            <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Nombre y Apellido */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="label">Nombre *</label>
                <input
                  type="text"
                  name="nombre"
                  value={formData.nombre}
                  onChange={handleChange}
                  className="input"
                  required
                />
              </div>
              <div>
                <label className="label">Apellido *</label>
                <input
                  type="text"
                  name="apellido"
                  value={formData.apellido}
                  onChange={handleChange}
                  className="input"
                  required
                />
              </div>
            </div>

            {/* Cédula */}
            <div>
              <label className="label">Cédula *</label>
              <input
                type="text"
                name="cedula"
                value={formData.cedula}
                onChange={handleChange}
                className="input"
                required
              />
            </div>

            {/* Email y Teléfono */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="label">Email</label>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  className="input"
                />
              </div>
              <div>
                <label className="label">Teléfono *</label>
                <input
                  type="tel"
                  name="telefono"
                  value={formData.telefono}
                  onChange={handleChange}
                  className="input"
                  required
                />
              </div>
            </div>

            {/* Contraseña */}
            <div>
              <label className="label">Contraseña *</label>
              <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                className="input"
                required
              />
            </div>

            {/* Campos específicos para repartidor */}
            {tipo === 'repartidor' && (
              <>
                <div>
                  <label className="label">Licencia de Conducir</label>
                  <input
                    type="text"
                    name="licenciaConducir"
                    value={formData.licenciaConducir}
                    onChange={handleChange}
                    className="input"
                  />
                </div>
                <div>
                  <label className="label">Vehículo Asignado</label>
                  <input
                    type="text"
                    name="vehiculoAsignado"
                    value={formData.vehiculoAsignado}
                    onChange={handleChange}
                    className="input"
                  />
                </div>
                <div>
                  <label className="label">Zona Asignada</label>
                  <input
                    type="text"
                    name="zonaAsignada"
                    value={formData.zonaAsignada}
                    onChange={handleChange}
                    className="input"
                  />
                </div>
              </>
            )}

            {/* Botones */}
            <div className="flex gap-3 pt-4">
              <button
                type="button"
                onClick={onClose}
                className="flex-1 btn btn-secondary"
                disabled={loading}
              >
                Cancelar
              </button>
              <button
                type="submit"
                className="flex-1 btn btn-primary"
                disabled={loading}
              >
                {loading ? 'Creando...' : 'Crear'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default CrearUsuarioModal;
