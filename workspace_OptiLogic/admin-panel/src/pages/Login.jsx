import React, { useState } from "react";

function Login({ onLogin }) {
  const [user, setUser] = useState("");
  const [pass, setPass] = useState("");
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const response = await fetch("http://localhost:8080/api/categorias", {
      headers: {
        Authorization: "Basic " + btoa(`${user}:${pass}`),
      },
    });
    if (response.ok) {
      onLogin(user, pass);
    } else {
      setError("Credenciales incorrectas");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input placeholder="Usuario" value={user} onChange={e => setUser(e.target.value)} required />
      <input type="password" placeholder="Contraseña" value={pass} onChange={e => setPass(e.target.value)} required />
      <button type="submit">Entrar</button>
      {error && <p>{error}</p>}
    </form>
  );
}

export default Login;
