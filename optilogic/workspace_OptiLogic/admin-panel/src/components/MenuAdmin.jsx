// src/components/MenuAdmin.jsx

import React from 'react';
import { Link } from 'react-router-dom';

import '../assets/MenuAdmin.css'; 

export default function MenuAdmin() {
    return (
        <aside className="sidebar">
            <div className="sidebar-header">
                <h2>OptiLogic Admin</h2>
            </div>
            <nav className="sidebar-nav">
                <ul>
                    <li>
                        {/* El 'to' debe coincidir con el 'path' en App.jsx */}
                        <Link to="/dashboard" className="nav-item">Dashboard</Link>
                    </li>
                    <li>
                        <Link to="/productos" className="nav-item">Productos</Link>
                    </li>
                    <li>
                        <Link to="/categorias" className="nav-item">Categorías</Link>
                    </li>
                    <li>
                        <Link to="/envios" className="nav-item">Envíos</Link>
                    </li>
                    {/* Añadirás más links aquí (Clientes, Repartidores) */}
                </ul>
            </nav>
            <div className="sidebar-footer">
                <button 
                    className="logout-button"
                    onClick={() => {
                        localStorage.removeItem('token');
                        window.location.href = '/login'; // Forzamos la redirección
                    }}
                >
                    Cerrar Sesión
                </button>
            </div>
        </aside>
    );
}