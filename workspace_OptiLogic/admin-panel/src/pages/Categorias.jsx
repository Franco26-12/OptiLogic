import React, { useState, useEffect } from "react";
import { fetchCategorias, createCategoria } from "../service/api";

function Categorias() {
  const [categorias, setCategorias] = useState([]);
  const [nombre, setNombre] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchCategorias()
      .then(data => {
        setCategorias(data);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!nombre.trim()) return;
    try {
      await createCategoria({ nombre });
      const data = await fetchCategorias();
      setCategorias(data);
      setNombre("");
    } catch (err) {
      setError(err.message);
    }
  };

  if (loading) return <div>Cargando categorías...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      <h2>Gestión de Categorías</h2>

      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Nombre de categoría"
          value={nombre}
          onChange={e => setNombre(e.target.value)}
          required
        />
        <button type="submit">Agregar Categoría</button>
      </form>

      <ul>
        {categorias.length === 0 ? (
          <li>No hay categorías registradas</li>
        ) : (
          categorias.map(cat => (
            <li key={cat.id}>{cat.nombre}</li>
          ))
        )}
      </ul>
    </div>
  );
}

export default Categorias;
