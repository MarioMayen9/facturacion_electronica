// src/App.jsx
import Sidebar from "./components/Sidebar";
import Header from "./components/Header";
import Configuracion from "./pages/Configuracion";

function App() {
  return (
    <div className="min-h-screen" style={{ backgroundColor: '#F8FAFE' }}>
      <Sidebar />
      <Header />
      <Configuracion />
    </div>
  );
}

export default App;
