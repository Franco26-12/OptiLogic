import React, { useEffect, useMemo, useState } from 'react';
import {
  fetchEnvios,
  fetchClientes,
  fetchProductos,
  crearEnvio,
  obtenerRepartidoresDisponibles,
  asignarRepartidorEnvio,
} from '../services';
import { QRCodeCanvas } from 'qrcode.react';

const initialFormState = {
  clienteId: '',
  repartidorId: '',
  direccionDestino: '',
  productoIds: [],
};

export default function Envios() {
  const [envios, setEnvios] = useState([]);
  const [clientes, setClientes] = useState([]);
  const [productos, setProductos] = useState([]);
  const [repartidores, setRepartidores] = useState([]);
  const [form, setForm] = useState(initialFormState);
  const [assignSelection, setAssignSelection] = useState({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [assigning, setAssigning] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const cargarEnvios = async () => {
    const data = await fetchEnvios();
    setEnvios(data);
  };

  const cargarClientes = async () => {
    const data = await fetchClientes();
    setClientes(data);
  };

  const cargarProductos = async () => {
    const data = await fetchProductos();
    setProductos(data);
  };

  const cargarRepartidores = async () => {
    const data = await obtenerRepartidoresDisponibles();
    setRepartidores(data);
  };

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      try {
        await Promise.all([
          cargarEnvios(),
          cargarClientes(),
          cargarProductos(),
          cargarRepartidores(),
        ]);
      } catch (err) {
        setError(err.message || 'No se pudo cargar la información inicial.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const resetForm = () => {
    setForm(initialFormState);
    setSaving(false);
  };

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleProductoToggle = (productoId) => {
    setForm((prev) => {
      const yaIncluido = prev.productoIds.includes(productoId);
      return {
        ...prev,
        productoIds: yaIncluido
          ? prev.productoIds.filter((id) => id !== productoId)
          : [...prev.productoIds, productoId],
      };
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    setSuccess(null);

    const payload = {
      clienteId: Number(form.clienteId),
      repartidorId: form.repartidorId ? Number(form.repartidorId) : null,
      direccionDestino: form.direccionDestino,
      productoIds: form.productoIds.map(Number),
    };

    try {
      await crearEnvio(payload);
      setSuccess('Envío creado correctamente.');
      resetForm();
      await Promise.all([cargarEnvios(), cargarRepartidores()]);
    } catch (err) {
      setError(err.message || 'Ocurrió un error al crear el envío.');
    } finally {
      setSaving(false);
    }
  };

  const handleAssignChange = (envioId, repartidorId) => {
    setAssignSelection((prev) => ({
      ...prev,
      [envioId]: repartidorId,
    }));
  };

  const handleAssignSubmit = async (envioId) => {
    const repartidorId = assignSelection[envioId];
    if (!repartidorId) {
      setError('Selecciona un repartidor disponible antes de asignar.');
      return;
    }

    setAssigning(true);
    setError(null);
    setSuccess(null);

    try {
      await asignarRepartidorEnvio(envioId, repartidorId);
      setSuccess('Repartidor asignado correctamente.');
      await Promise.all([cargarEnvios(), cargarRepartidores()]);
      setAssignSelection((prev) => ({ ...prev, [envioId]: '' }));
    } catch (err) {
      setError(err.message || 'No se pudo asignar el repartidor.');
    } finally {
      setAssigning(false);
    }
  };

  const repartidoresDisponiblesPorId = useMemo(() => {
    const map = new Map();
    repartidores.forEach((rep) => map.set(rep.id, rep));
    return map;
  }, [repartidores]);

  return (
    <>
      <header className="main-header">
        <h1>Gestión de Envíos</h1>
      </header>

      <section className="dashboard-content">
        {error && <p className="error-message">{error}</p>}
        {success && <p className="success-message">{success}</p>}

        <article className="stat-card">
          <h2>Crear nuevo envío</h2>
          <form className="product-form" onSubmit={handleSubmit}>
            <div className="form-grid">
              <label>
                Cliente
                <select
                  name="clienteId"
                  value={form.clienteId}
                  onChange={handleInputChange}
                  required
                >
                  <option value="">Seleccione un cliente</option>
                  {clientes.map((cliente) => (
                    <option key={cliente.id} value={cliente.id}>
                      {cliente.nombre} {cliente.apellido}
                    </option>
                  ))}
                </select>
              </label>

              <label>
                Dirección de entrega
                <input
                  type="text"
                  name="direccionDestino"
                  value={form.direccionDestino}
                  onChange={handleInputChange}
                  required
                />
              </label>

              <label>
                Asignar repartidor (opcional)
                <select
                  name="repartidorId"
                  value={form.repartidorId}
                  onChange={handleInputChange}
                >
                  <option value="">Seleccionar más tarde</option>
                  {repartidores.map((rep) => (
                    <option key={rep.id} value={rep.id}>
                      {rep.nombre} {rep.apellido}
                    </option>
                  ))}
                </select>
              </label>

              <fieldset className="full-width">
                <legend>Productos a enviar</legend>
                {productos.length === 0 ? (
                  <p>No hay productos disponibles.</p>
                ) : (
                  <div className="checkbox-grid">
                    {productos.map((producto) => (
                      <label key={producto.id} className="checkbox-item">
                        <input
                          type="checkbox"
                          checked={form.productoIds.includes(producto.id)}
                          onChange={() => handleProductoToggle(producto.id)}
                        />
                        <span>
                          {producto.nombre} (SKU: {producto.sku})
                        </span>
                      </label>
                    ))}
                  </div>
                )}
              </fieldset>
            </div>
            <div className="form-actions">
              <button type="submit" className="primary" disabled={saving}>
                {saving ? 'Creando envío...' : 'Crear envío'}
              </button>
              <button
                type="button"
                className="secondary"
                onClick={resetForm}
                disabled={saving}
              >
                Limpiar
              </button>
            </div>
          </form>
        </article>

        <article className="stat-card">
          <h2>Listado de envíos</h2>
          {loading ? (
            <p>Cargando envíos...</p>
          ) : envios.length === 0 ? (
            <p>No hay envíos registrados.</p>
          ) : (
            <div className="table-responsive">
              <table className="products-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Cliente</th>
                    <th>Dirección</th>
                    <th>Estado</th>
                    <th>Repartidor</th>
                    <th>Productos</th>
                    <th>QR</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {envios.map((envio) => {
                    const repartidorActual = envio.repartidor;
                    const productosDescripcion = (envio.productos || [])
                      .map((producto) => producto.nombre)
                      .join(', ');

                    return (
                      <tr key={envio.id}>
                        <td>{envio.id}</td>
                        <td>
                          {envio.cliente
                            ? `${envio.cliente.nombre} ${envio.cliente.apellido}`
                            : '—'}
                        </td>
                        <td>{envio.direccionDestino}</td>
                        <td>{envio.estado}</td>
                        <td>
                          {repartidorActual
                            ? `${repartidorActual.nombre} ${repartidorActual.apellido}`
                            : 'Sin asignar'}
                        </td>
                        <td>{productosDescripcion || '—'}</td>
                        <td>
                          {envio.codigoQR ? (
                            <div className="qr-wrapper">
                              <QRCodeCanvas value={envio.codigoQR} size={72} />
                              <p className="qr-code-text">{envio.codigoQR}</p>
                            </div>
                          ) : (
                            '—'
                          )}
                        </td>
                        <td className="actions">
                          <div className="assign-wrapper">
                            <select
                              value={assignSelection[envio.id] || ''}
                              onChange={(event) =>
                                handleAssignChange(envio.id, event.target.value)
                              }
                              disabled={assigning}
                            >
                              <option value="">Seleccionar repartidor</option>
                              {repartidores.map((rep) => (
                                <option key={rep.id} value={rep.id}>
                                  {rep.nombre} {rep.apellido}
                                </option>
                              ))}
                            </select>
                            <button
                              type="button"
                              className="primary"
                              onClick={() => handleAssignSubmit(envio.id)}
                              disabled={assigning}
                            >
                              {assigning ? 'Asignando...' : 'Asignar'}
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </article>
      </section>
    </>
  );
}
