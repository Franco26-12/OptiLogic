import React, { useEffect, useMemo, useState } from 'react';
import {
  obtenerCategorias,
  crearCategoria,
  actualizarCategoria,
  borrarCategoria,
} from '../services';

const initialFormState = {
  id: null,
  nombre: '',
  descripcion: '',
};

export default function Categoria() {
  const [categorias, setCategorias] = useState([]);
  const [form, setForm] = useState(initialFormState);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [orden, setOrden] = useState('nombre-asc');
  const [busquedaNombre, setBusquedaNombre] = useState('');

  const cargarCategorias = async () => {
    setError(null);
    try {
      const datos = await obtenerCategorias();
      setCategorias(datos);
    } catch (err) {
      setError(err.message || 'No se pudieron cargar las categorías.');
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      await cargarCategorias();
      setLoading(false);
    };

    fetchData();
  }, []);

  const categoriasFiltradas = useMemo(() => {
    const termino = busquedaNombre.trim().toLowerCase();

    const filtradas = termino
      ? categorias.filter((categoria) =>
          (categoria.nombre || '').toLowerCase().includes(termino)
        )
      : categorias;

    const copia = [...filtradas];

    switch (orden) {
      case 'nombre-asc':
        return copia.sort((a, b) => (a.nombre || '').localeCompare(b.nombre || '', 'es', { sensitivity: 'base' }));
      case 'nombre-desc':
        return copia.sort((a, b) => (b.nombre || '').localeCompare(a.nombre || '', 'es', { sensitivity: 'base' }));
      case 'creacion-reciente':
        return copia.sort((a, b) => (b.id ?? 0) - (a.id ?? 0));
      case 'creacion-antigua':
        return copia.sort((a, b) => (a.id ?? 0) - (b.id ?? 0));
      default:
        return copia;
    }
  }, [busquedaNombre, categorias, orden]);

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
      descripcion: form.descripcion,
    };

    try {
      if (form.id) {
        await actualizarCategoria(form.id, payload);
        setSuccess('Categoría actualizada correctamente.');
      } else {
        await crearCategoria(payload);
        setSuccess('Categoría creada correctamente.');
      }

      await cargarCategorias();
      resetForm();
    } catch (err) {
      setError(err.message || 'Ocurrió un error al guardar la categoría.');
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (categoria) => {
    setError(null);
    setSuccess(null);
    setForm({
      id: categoria.id,
      nombre: categoria.nombre || '',
      descripcion: categoria.descripcion || '',
    });
  };

  const handleDelete = async (id) => {
    const confirmar = window.confirm('¿Seguro que deseas eliminar esta categoría?');
    if (!confirmar) return;

    setError(null);
    setSuccess(null);
    try {
      await borrarCategoria(id);
      setSuccess('Categoría eliminada correctamente.');
      await cargarCategorias();
      if (form.id === id) {
        resetForm();
      }
    } catch (err) {
      setError(err.message || 'No se pudo eliminar la categoría.');
    }
  };

  return (
    <>
      <header className="main-header">
        <h1>Gestión de Categorías</h1>
      </header>

      <section className="dashboard-content">
        {error && <p className="error-message">{error}</p>}
        {success && <p className="success-message">{success}</p>}

        <article className="stat-card">
          <h2>{form.id ? 'Editar categoría' : 'Crear nueva categoría'}</h2>
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
              <label className="full-width">
                Descripción
                <textarea
                  name="descripcion"
                  value={form.descripcion}
                  onChange={handleChange}
                  rows="2"
                />
              </label>
            </div>
            <div className="form-actions">
              <button type="submit" className="primary" disabled={saving}>
                {saving ? 'Guardando...' : form.id ? 'Actualizar' : 'Crear'}
              </button>
              {form.id && (
                <button
                  type="button"
                  className="secondary"
                  onClick={resetForm}
                  disabled={saving}
                >
                  Cancelar
                </button>
              )}
            </div>
          </form>
        </article>

        <article className="stat-card">
          <h2>Listado de categorías</h2>
          <div className="table-controls">
            <label htmlFor="orden-categorias" className="table-control">
              Ordenar por
              <select
                id="orden-categorias"
                value={orden}
                onChange={(event) => setOrden(event.target.value)}
              >
                <option value="nombre-asc">Nombre (A-Z)</option>
                <option value="nombre-desc">Nombre (Z-A)</option>
                <option value="creacion-reciente">Creación (más recientes)</option>
                <option value="creacion-antigua">Creación (más antiguas)</option>
              </select>
            </label>
            <label htmlFor="busqueda-categorias" className="table-control">
              Buscar
              <input
                id="busqueda-categorias"
                type="text"
                value={busquedaNombre}
                placeholder="Escribe para filtrar..."
                onChange={(event) => setBusquedaNombre(event.target.value)}
              />
            </label>
          </div>
          {loading ? (
            <p>Cargando categorías...</p>
          ) : categorias.length === 0 ? (
            <p>No hay categorías registradas.</p>
          ) : categoriasFiltradas.length === 0 ? (
            <p>No se encontraron categorías que coincidan con la búsqueda.</p>
          ) : (
            <div className="table-responsive">
              <table className="products-table">
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Descripción</th>
                    <th>Productos asociados</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {categoriasFiltradas.map((categoria) => (
                    <tr key={categoria.id}>
                      <td>{categoria.nombre}</td>
                      <td>{categoria.descripcion || 'Sin descripción'}</td>
                      <td>{categoria.productos ? categoria.productos.length : 0}</td>
                      <td className="actions">
                        <button
                          type="button"
                          className="secondary"
                          onClick={() => handleEdit(categoria)}
                        >
                          Editar
                        </button>
                        <button
                          type="button"
                          className="danger"
                          onClick={() => handleDelete(categoria.id)}
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
        </article>
      </section>
    </>
  );
}
