import React, { useState } from "react";

export default function Registro() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const rol = "ADMIN";

  const handleSubmit = async (e) => {
    e.preventDefault();
    const usuario = { email, password, rol };

    try {
      const response = await fetch("/registro", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(usuario),
      });
      if (response.ok) {
        alert("Registro exitoso, por favor inicia sesión.");
       
      } else {
        alert("Error en el registro");
      }
    } catch (error) {
      console.error("Error:", error);
      alert("Error en la conexión");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <label>Email:</label>
      <input type="email" value={email} onChange={e => setEmail(e.target.value)} required />

      <label>Contraseña:</label>
      <input type="password" value={password} onChange={e => setPassword(e.target.value)} required />

      <button type="submit">Registrar</button>
    </form>
  );
}
