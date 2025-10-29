import React from 'react'
import { Link } from 'react-router-dom'

export default function MenuAdmin() {
  return (
    <nav>
      <ul>
        <li><Link to="/productos">Productos</Link></li>
        <li><Link to="/categorias">Categorías</Link></li>
        {/* Agrega más enlaces admin aquí */}
      </ul>
    </nav>
  )
}
