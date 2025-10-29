import React, { useEffect, useState } from 'react'
import { fetchProductos, crearProducto, actualizarProducto, borrarProducto } from '../services'

export default function Productos() {
  const [productos, setProductos] = useState([])
  const [error, setError] = useState(null)
  const [nombre, setNombre] = useState('')
  const [precio, setPrecio] = useState('')
  const [editId, setEditId] = useState(null)

  const token = localStorage.getItem('token')

  const cargarProductos = () => {
    fetchProductos(token)
      .then(setProductos)
      .catch(err => setError(err.message))
  }

  useEffect(() => {
    cargarProductos()
  }, [token])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    try {
      if (editId) {
        await actualizarProducto(token, editId, { nombre, precio: Number(precio) })
        setEditId(null)
      } else {
        await crearProducto(token, { nombre, precio: Number(precio) })
      }
      setNombre('')
      setPrecio('')
      cargarProductos()
    } catch (err) {
      setError(err.message)
    }
  }

  const handleEdit = (prod) => {
    setNombre(prod.nombre)
    setPrecio(prod.precio)
    setEditId(prod.id)
  }

  const handleDelete = async (id) => {
    setError(null)
    try {
      await borrarProducto(token, id)
      cargarProductos()
    } catch (err) {
      setError(err.message)
    }
  }

  if (error) return <div>Error: {error}</div>

  return (
    <div>
      <h1>Productos</h1>
      <form onSubmit={handleSubmit}>
        <input placeholder="Nombre" value={nombre} onChange={e => setNombre(e.target.value)} required />
        <input type="number" placeholder="Precio" value={precio} onChange={e => setPrecio(e.target.value)} required />
        <button type="submit">{editId ? 'Actualizar' : 'Crear'}</button>
      </form>
      <ul>
        {productos.map(p => (
          <li key={p.id}>
            {p.nombre} - ${p.precio} 
            <button onClick={() => handleEdit(p)}>Editar</button>
            <button onClick={() => handleDelete(p.id)}>Eliminar</button>
          </li>
        ))}
      </ul>
    </div>
  )
}
