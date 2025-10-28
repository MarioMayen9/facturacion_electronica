// src/pages/Configuracion.jsx
import { useState } from "react";
import Table from "../components/Table";
import Modal from "../components/Modal";

const Configuracion = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    nombre: "",
    impuestosAplicar: ""
  });

  const columns = [
    { header: "Nombre", accessor: "nombre" },
    { header: "Impuesto", accessor: "impuesto" },
    { header: "Tipo de documento", accessor: "tipo" },
    { header: "Código", accessor: "codigo" },
    { header: "Disminuye valores antes doc", accessor: "disminuye" },
    { header: "Mostrar precios con IVA", accessor: "iva" },
  ];

  const [data, setData] = useState([
    {
      nombre: "Contribuyente 1",
      impuesto: "12%",
      tipo: "Factura",
      codigo: "235678",
      disminuye: "Sí",
      iva: "No",
    },
    {
      nombre: "Contribuyente 2",
      impuesto: "12%",
      tipo: "Factura",
      codigo: "235678",
      disminuye: "Sí",
      iva: "No",
    },
  ]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!formData.nombre.trim() || !formData.impuestosAplicar.trim()) {
      alert("Por favor, complete todos los campos obligatorios");
      return;
    }

    // Crear nuevo contribuyente con valores por defecto
    const nuevoContribuyente = {
      nombre: formData.nombre,
      impuesto: formData.impuestosAplicar,
      tipo: "Factura", // Valor por defecto
      codigo: Math.floor(Math.random() * 900000 + 100000).toString(), // Código aleatorio
      disminuye: "No", // Valor por defecto
      iva: "No", // Valor por defecto
    };

    setData(prev => [...prev, nuevoContribuyente]);
    
    // Limpiar formulario y cerrar modal
    setFormData({ nombre: "", impuestosAplicar: "" });
    setIsModalOpen(false);
  };
  const handleCloseModal = () => {
    setFormData({ nombre: "", impuestosAplicar: "" });
    setIsModalOpen(false);
  };  return (
    <div className="lg:ml-32 lg:mr-8 pt-28 lg:pt-32 p-4 sm:p-6 lg:p-8 min-h-screen">
      {/* Título Configuración en negro */}
      <h1 className="text-xl sm:text-2xl font-semibold mb-6 sm:mb-8" style={{ color: '#000000' }}>
        Configuración
      </h1>
      
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 lg:p-8 max-w-none">
        <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-6 sm:mb-8 gap-4">
          <h2 className="text-2xl sm:text-3xl font-semibold" style={{ color: '#0095FF' }}>
            Clasificación de Contribuyente
          </h2>
          <button
            className="px-4 py-3 sm:py-2 rounded-lg text-gray-600 font-medium hover:opacity-90 transition border border-gray-300 touch-manipulation min-h-[44px]"
            style={{ backgroundColor: '#F7FAFF' }}
          >
            📤 Exportar
          </button>
        </div>

        <Table columns={columns} data={data} />
        
        {/* Agregar Nuevo Contribuyente */}
        <div className="mt-6">
          <button
            onClick={() => setIsModalOpen(true)}
            className="flex items-center gap-2 text-blue-500 hover:text-blue-600 transition font-medium px-4 py-3 sm:py-2 rounded-lg border border-gray-300 touch-manipulation min-h-[44px]"
            style={{ backgroundColor: '#FFFFFF' }}
          >
            <span className="text-xl">⊕</span>
            Agregar Nuevo Contribuyente
          </button>
        </div>
      </div>

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title="Crear Nuevo Clasificación de Contribuyente"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-2" style={{ color: '#333' }}>
              Nombre
            </label>
            <input
              type="text"
              name="nombre"
              value={formData.nombre}
              onChange={handleInputChange}
              placeholder="Gran Contribuyente"
              className="w-full px-3 py-3 sm:py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500 touch-manipulation text-base"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-2" style={{ color: '#333' }}>
              Impuestos a aplicar
            </label>
            <select
              name="impuestosAplicar"
              value={formData.impuestosAplicar}
              onChange={handleInputChange}
              className="w-full px-3 py-3 sm:py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500 text-gray-500 touch-manipulation text-base"
              required
            >
              <option value="">Seleccionar</option>
              <option value="0%">0% - Exento</option>
              <option value="12%">12% - IVA General</option>
              <option value="15%">15% - IVA Especial</option>
            </select>
          </div>

          <div className="flex justify-end pt-4">
            <button
              type="submit"
              className="px-6 py-3 sm:py-2 rounded-lg text-white font-medium hover:opacity-90 transition flex items-center gap-2 touch-manipulation"
              style={{ backgroundColor: '#0095FF' }}
            >
              <span>💾</span>
              Guardar
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default Configuracion;