import React, { useEffect, useState } from 'react';
import { obtenerDashboard } from '../services/api';
import UsuariosModal from '../components/UsuariosModal';

export default function Dashboard() {
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [modalUsuariosAbierto, setModalUsuariosAbierto] = useState(false);

    useEffect(() => {
        const cargarEstadisticas = async () => {
            try {
                const data = await obtenerDashboard();
                setStats(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        cargarEstadisticas();
    }, []);

    return (
        <>
            <header className="main-header">
                <div className="header-content">
                    <h1>Panel de Administración de OptiLogic</h1>
                    <button
                        type="button"
                        className="primary"
                        onClick={() => setModalUsuariosAbierto(true)}
                    >
                        Gestionar usuarios
                    </button>
                </div>
            </header>

            <section className="dashboard-content">
                {loading && <p>Cargando estadísticas...</p>}
                {error && <p className="error-message">{error}</p>}

                {stats && !loading && !error && (
                    <div className="stats-grid">
                        <article className="stat-card">
                            <h2>Productos</h2>
                            <p>Total: <strong>{stats.totalProductos}</strong></p>
                            <p>Disponibles: <strong>{stats.productosDisponibles}</strong></p>
                            <p>Sin stock: <strong>{stats.productosSinStock}</strong></p>
                            <p className={stats.productosStockBajo > 0 ? 'warning' : ''}>
                                Stock bajo: <strong>{stats.productosStockBajo}</strong>
                            </p>
                        </article>

                        <article className="stat-card">
                            <h2>Envíos</h2>
                            <p>Total: <strong>{stats.totalEnvios}</strong></p>
                            <p>Pendientes: <strong>{stats.enviosPendientes}</strong></p>
                            <p>En tránsito: <strong>{stats.enviosEnTransito}</strong></p>
                            <p>Entregados: <strong>{stats.enviosEntregados}</strong></p>
                        </article>

                        <article className="stat-card">
                            <h2>Repartidores</h2>
                            <p>Total: <strong>{stats.totalRepartidores}</strong></p>
                            <p>Disponibles: <strong>{stats.repartidoresDisponibles}</strong></p>
                            <p>Ocupados: <strong>{stats.repartidoresOcupados}</strong></p>
                        </article>

                        <article className="stat-card">
                            <h2>Administradores</h2>
                            <p>Total: <strong>{stats.totalAdmins}</strong></p>
                        </article>
                    </div>
                )}
            </section>

            <UsuariosModal
                abierto={modalUsuariosAbierto}
                onClose={() => setModalUsuariosAbierto(false)}
            />
        </>
    );
}