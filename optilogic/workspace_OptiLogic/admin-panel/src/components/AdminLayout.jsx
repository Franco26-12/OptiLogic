import React, { useEffect, useState } from 'react';
import { Outlet } from 'react-router-dom';
import MenuAdmin from './MenuAdmin';
import '../Dashboard.css';

export default function AdminLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(() => {
    if (typeof window === 'undefined') return true;
    return window.innerWidth >= 992;
  });
  const [isMobile, setIsMobile] = useState(() => {
    if (typeof window === 'undefined') return false;
    return window.innerWidth < 992;
  });

  useEffect(() => {
    const handleResize = () => {
      if (typeof window === 'undefined') return;
      const isDesktop = window.innerWidth >= 992;
      setIsMobile(!isDesktop);
      if (!isDesktop) {
        setSidebarOpen(false);
      }
    };

    window.addEventListener('resize', handleResize);
    handleResize();
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const toggleSidebar = () => setSidebarOpen((prev) => !prev);

  return (
    <div
      className={`dashboard-layout ${sidebarOpen ? 'sidebar-open' : 'sidebar-collapsed'}`}
    >
      <MenuAdmin isOpen={sidebarOpen} />
      {isMobile && sidebarOpen && (
        <div className="sidebar-backdrop visible" onClick={toggleSidebar} aria-hidden="true" />
      )}
      <button
        type="button"
        className={`sidebar-toggle ${sidebarOpen ? 'open' : ''}`}
        aria-label={sidebarOpen ? 'Ocultar menú' : 'Mostrar menú'}
        aria-expanded={sidebarOpen}
        aria-controls="admin-sidebar"
        onClick={toggleSidebar}
      >
        <span className="toggle-icon">{sidebarOpen ? '×' : '☰'}</span>
        <span className="toggle-text">Menú</span>
      </button>
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
