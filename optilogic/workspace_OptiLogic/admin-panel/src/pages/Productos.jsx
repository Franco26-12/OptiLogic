
import React, { useEffect, useState } from 'react';
import { fetchProductos } from '../services';

export default function Productos() {   
 const [productos, setProductos] = useState([]);
  const [error, setError] = useState(null);

useEffect(() => {
    setError(null);
    fetchProductos()
      .then(data => {
        console.log("Conexión Exitosa desde React", data);
        setProductos(data);
      })
      .catch(err => {
        console.error("Error al llamar a fetchProductos", err);
        setError(err.message);
      });
 }, []); 

 return (
 <div className="App"> 
 <h1>Dashboard de Logística</h1>
 <h2>Productos desde el Backend (Eclipse):</h2>

      {error && <p style={{ color: 'red' }}>Error: {error}</p>}

 <ul>
{productos.map(producto => (
 <li key={producto.id}>
 {producto.nombre} (SKU: {producto.sku})
 </li>
 ))}
 </ul>
 </div> );
}