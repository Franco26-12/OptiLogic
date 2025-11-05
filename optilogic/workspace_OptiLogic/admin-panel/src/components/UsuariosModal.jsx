import React, { useEffect, useState } from 'react';
import {
  obtenerUsuarios,
  crearUsuario,
  actualizarUsuario,
  borrarUsuario,
} from '../services';

const initialFormState = {
  id: null,
  nombre: '',
  apellido: '',
  cedula: '',
  email: '',
  password: '',
  rol: 'ADMIN',
  estado: 'DISPONIBLE',
};

export default function UsuariosModal({ abierto, onClose }) {
  const [usuarios, setUsuarios] = useState([]);
  const [form, setForm] = useState(initialFormState);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const cargarUsuarios = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await obtenerUsuarios();
      setUsuarios(data);
    } catch (err) {
      setError(err.message || 'No se pudieron obtener los usuarios.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (abierto) {
      cargarUsuarios();
      resetForm();
    }
  }, [abierto]);

  if (!abierto) {
    return null;
  }

  const resetForm = () => {
    setForm(initialFormState);
    setSaving(false);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    setSuccess(null);

    const payload = {
      nombre: form.nombre,
      apellido: form.apellido,
      cedula: form.cedula,
      email: form.email,
      password: form.password,
      rol: form.rol,
      estado: form.rol === 'REPARTIDOR' ? form.estado : undefined,
    };

    try {
      if (form.id) {
        await actualizarUsuario(form.id, payload);
        setSuccess('Usuario actualizado correctamente.');
      } else {
        await crearUsuario(payload);
        setSuccess('Usuario creado correctamente.');
      }
      await cargarUsuarios();
      resetForm();
    } catch (err) {
      setError(err.message || 'Ocurrió un error al guardar el usuario.');
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (usuario) => {
    setError(null);
    setSuccess(null);
    setForm({
      id: usuario.id,
      nombre: usuario.nombre || '',
      apellido: usuario.apellido || '',
      cedula: usuario.cedula || '',
      email: usuario.email || '',
      password: '',
      rol: usuario.rol || 'ADMIN',
      estado: usuario.estado || 'DISPONIBLE',
    });
  };

  const handleDelete = async (id) => {
    const confirmar = window.confirm('¿Seguro que deseas eliminar este usuario?');
    if (!confirmar) return;

    setError(null);
    setSuccess(null);
    try {
      await borrarUsuario(id);
      setSuccess('Usuario eliminado correctamente.');
      await cargarUsuarios();
      if (form.id === id) {
        resetForm();
      }
    } catch (err) {
      setError(err.message || 'No se pudo eliminar el usuario.');
    }
  };

  return (
    <div className="modal-overlay" role="dialog" aria-modal="true">
      <div className="modal-content">
        <header className="modal-header">
          <h2>Gestión de Usuarios</h2>
          <button type="button" className="close-button" onClick={onClose}>
            ×
          </button>
        </header>

        <div className="modal-body">
          {error && <p className="error-message">{error}</p>}
          {success && <p className="success-message">{success}</p>}

          <section className="stat-card">
            <h3>{form.id ? 'Editar usuario' : 'Crear nuevo usuario'}</h3>
            <form className="product-form" onSubmit={handleSubmit}>
              <div className="form-grid">
                <label>
                  Nombre
                  <input
                    type="text"
                    name="nombre"
                    value={form.nombre}
                    onChange={handleChange}
                    required
                  />
                </label>
                <label>
                  Apellido
                  <input
                    type="text"
                    name="apellido"
                    value={form.apellido}
                    onChange={handleChange}
                    required
                  />
                </label>
                <label>
                  Cédula
                  <input
                    type="text"
                    name="cedula"
                    value={form.cedula}
                    onChange={handleChange}
                    required
                  />
                </label>
                <label>
                  Email
                  <input
                    type="email"
                    name="email"
                    value={form.email}
                    onChange={handleChange}
                    required
                  />
                </label>
                <label>
                  Contraseña
                  <input
                    type="password"
                    name="password"
                    value={form.password}
                    onChange={handleChange}
                    placeholder={form.id ? 'Dejar vacío para no cambiar' : ''}
                    required={!form.id}
                  />
                </label>
                <label>
                  Rol
                  <select name="rol" value={form.rol} onChange={handleChange} required>
                    <option value="ADMIN">Administrador</option>
                    <option value="REPARTIDOR">Repartidor</option>
                  </select>
                </label>
                {form.rol === 'REPARTIDOR' && (
                  <label>
                    Estado
                    <select name="estado" value={form.estado} onChange={handleChange}>
                      <option value="DISPONIBLE">Disponible</option>
                      <option value="OCUPADO">Ocupado</option>
                      <option value="INACTIVO">Inactivo</option>
                    </select>
                  </label>
                )}
              </div>
              <div className="form-actions">
                <button type="submit" className="primary" disabled={saving}>
                  {saving ? 'Guardando...' : form.id ? 'Actualizar' : 'Crear'}
                </button>
                {form.id && (
                  <button type="button" className="secondary" onClick={resetForm} disabled={saving}>
                    Cancelar
                  </button>
                )}
              </div>
            </form>
          </section>

          <section className="stat-card">
            <h3>Usuarios registrados</h3>
            {loading ? (
              <p>Cargando usuarios...</p>
            ) : usuarios.length === 0 ? (
              <p>No hay usuarios registrados.</p>
            ) : (
              <div className="table-responsive">
                <table className="products-table">
                  <thead>
                    <tr>
                      <th>Nombre</th>
                      <th>Apellido</th>
                      <th>Cédula</th>
                      <th>Email</th>
                      <th>Rol</th>
                      <th>Estado</th>
                      <th>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {usuarios.map((usuario) => (
                      <tr key={usuario.id}>
                        <td>{usuario.nombre}</td>
                        <td>{usuario.apellido}</td>
                        <td>{usuario.cedula}</td>
                        <td>{usuario.email}</td>
                        <td>{usuario.rol}</td>
                        <td>{usuario.estado || '—'}</td>
                        <td className="actions">
                          <button
                            type="button"
                            className="secondary"
                            onClick={() => handleEdit(usuario)}
                          >
                            Editar
                          </button>
                          <button
                            type="button"
                            className="danger"
                            onClick={() => handleDelete(usuario.id)}
                          >
                            Eliminar
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        </div>
      </div>
    </div>
  );
}
