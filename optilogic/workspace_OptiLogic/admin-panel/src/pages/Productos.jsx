import React, { useEffect, useState } from 'react';
import {
  fetchProductos,
  obtenerCategorias,
  crearProducto,
  actualizarProducto,
  borrarProducto,
} from '../services';

const initialFormState = {
  id: null,
  nombre: '',
  sku: '',
  descripcion: '',
  stockDisponible: 0,
  stockMinimo: 0,
  categoriaId: '',
};

export default function Productos() {
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [form, setForm] = useState(initialFormState);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const cargarProductos = async () => {
    setError(null);
    try {
      const data = await fetchProductos();
      setProductos(data);
    } catch (err) {
      setError(err.message || 'No se pudieron obtener los productos.');
    }
  };

  const cargarCategorias = async () => {
    try {
      const data = await obtenerCategorias();
      setCategorias(data);
    } catch (err) {
      setError((prev) => prev || err.message || 'No se pudieron obtener las categorías.');
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      await Promise.all([cargarProductos(), cargarCategorias()]);
      setLoading(false);
    };

    fetchData();
  }, []);

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

    const productoPayload = {
      nombre: form.nombre,
      sku: form.sku,
      descripcion: form.descripcion,
      stockDisponible: Number(form.stockDisponible),
      stockMinimo: Number(form.stockMinimo),
      categoria: form.categoriaId ? { id: Number(form.categoriaId) } : null,
    };

    try {
      if (form.id) {
        await actualizarProducto(form.id, productoPayload);
        setSuccess('Producto actualizado correctamente.');
      } else {
        await crearProducto(productoPayload);
        setSuccess('Producto creado correctamente.');
      }

      await cargarProductos();
      resetForm();
    } catch (err) {
      setError(err.message || 'Ocurrió un error al guardar el producto.');
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (producto) => {
    setError(null);
    setSuccess(null);
    setForm({
      id: producto.id,
      nombre: producto.nombre || '',
      sku: producto.sku || '',
      descripcion: producto.descripcion || '',
      stockDisponible: producto.stockDisponible ?? 0,
      stockMinimo: producto.stockMinimo ?? 0,
      categoriaId: producto.categoria ? producto.categoria.id : '',
    });
  };

  const handleDelete = async (id) => {
    const confirmar = window.confirm('¿Seguro que deseas eliminar este producto?');
    if (!confirmar) return;

    setError(null);
    setSuccess(null);
    try {
      await borrarProducto(id);
      setSuccess('Producto eliminado correctamente.');
      await cargarProductos();
      if (form.id === id) {
        resetForm();
      }
    } catch (err) {
      setError(err.message || 'No se pudo eliminar el producto.');
    }
  };

  return (
    <>
      <header className="main-header">
        <h1>Gestión de Productos</h1>
      </header>

      <section className="dashboard-content">
        {error && <p className="error-message">{error}</p>}
        {success && <p className="success-message">{success}</p>}

        <article className="stat-card">
          <h2>{form.id ? 'Editar producto' : 'Crear nuevo producto'}</h2>
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
                SKU
                <input
                  type="text"
                  name="sku"
                  value={form.sku}
                  onChange={handleChange}
                  required
                />
              </label>
              <label>
                Stock disponible
                <input
                  type="number"
                  name="stockDisponible"
                  value={form.stockDisponible}
                  onChange={handleChange}
                  min="0"
                  required
                />
              </label>
              <label>
                Stock mínimo
                <input
                  type="number"
                  name="stockMinimo"
                  value={form.stockMinimo}
                  onChange={handleChange}
                  min="0"
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
              <label className="full-width">
                Categoría
                <select
                  name="categoriaId"
                  value={form.categoriaId}
                  onChange={handleChange}
                  required
                >
                  <option value="">Seleccione una categoría</option>
                  {categorias.map((categoria) => (
                    <option key={categoria.id} value={categoria.id}>
                      {categoria.nombre}
                    </option>
                  ))}
                </select>
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
          <h2>Listado de productos</h2>
          {loading ? (
            <p>Cargando productos...</p>
          ) : productos.length === 0 ? (
            <p>No hay productos registrados.</p>
          ) : (
            <div className="table-responsive">
              <table className="products-table">
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>SKU</th>
                    <th>Categoría</th>
                    <th>Stock</th>
                    <th>Stock mínimo</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {productos.map((producto) => (
                    <tr key={producto.id}>
                      <td>{producto.nombre}</td>
                      <td>{producto.sku}</td>
                      <td>{producto.categoria ? producto.categoria.nombre : 'Sin categoría'}</td>
                      <td>{producto.stockDisponible}</td>
                      <td>{producto.stockMinimo}</td>
                      <td className="actions">
                        <button
                          type="button"
                          className="secondary"
                          onClick={() => handleEdit(producto)}
                        >
                          Editar
                        </button>
                        <button
                          type="button"
                          className="danger"
                          onClick={() => handleDelete(producto.id)}
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