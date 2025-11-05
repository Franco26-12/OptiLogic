import React from 'react';
import { Outlet } from 'react-router-dom';
import MenuAdmin from './MenuAdmin';
import '../Dashboard.css';

export default function AdminLayout() {
  return (
    <div className="dashboard-layout">
      <MenuAdmin />
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
