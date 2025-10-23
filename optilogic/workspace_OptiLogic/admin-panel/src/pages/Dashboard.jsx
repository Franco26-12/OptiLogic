

import React from 'react';
import MenuAdmin from '../components/MenuAdmin';
import '../assets/Dashboard.css'; 

export default function Dashboard() {
    return (
        
        <div className="dashboard-layout"> 
            
           
            <MenuAdmin />

         
            <main className="main-content">
                <header className="main-header">
                    <h1>Bienvenido al Panel de Administración de OptiLogic</h1>
                   
                </header>
                
               
                <div className="dashboard-widgets">
                    <p>Este es el área principal del Dashboard. Aquí irán gráficos, resúmenes de envíos y estadísticas.</p>
                   
                </div>
            </main>
        </div>
    );
}