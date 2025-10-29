import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Categorias from "./pages/Categorias";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Categorias />} />
      </Routes>
    </Router>
  );
}

export default App;
