// src/pages/Configuracion.jsx
import React, { useState, useEffect } from "react";
import Table from "../components/Table";
import Modal from "../components/Modal";
import { fetchPaymentTerms, fetchPaymentForms, fetchDocumentTypes, createPaymentTerm, createPaymentForm } from "../services/api";

const Configuracion = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentCatalog, setCurrentCatalog] = useState(null);
  const [editingIndex, setEditingIndex] = useState(null);
  const [selectedRowIndex, setSelectedRowIndex] = useState(null);
  const [formData, setFormData] = useState({
    nombre: "",
    impuestosAplicar: "",
    codigo: "",
    descripcion: "",
    dias: "",
    efectivo: false,
    transferenciaBancaria: false,
    chequeEmision: false,
    chequeRecepcion: false,
    otrosGastosAnticipados: false,
    otrosVentasAnticipados: false
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

  // Data for Tipo de Documento - se cargará desde la API
  const [tipoDocumentoData, setTipoDocumentoData] = useState([]);
  const [loadingDocumentTypes, setLoadingDocumentTypes] = useState(true);

  // Data for Condición de Pago - se cargará desde la API
  const [condicionPagoData, setCondicionPagoData] = useState([]);
  const [loadingCondiciones, setLoadingCondiciones] = useState(true);

  // Data for Forma de Pago - se cargará desde la API
  const [formaPagoData, setFormaPagoData] = useState([]);
  const [loadingFormas, setLoadingFormas] = useState(true);

  // Cargar datos desde la API cuando el componente se monte
  useEffect(() => {
    const loadData = async () => {
      try {
        // Cargar tipos de documentos
        setLoadingDocumentTypes(true);
        const documentTypes = await fetchDocumentTypes();
        const documentTypesFormateados = documentTypes.map(item => ({
          id: item.id,
          nombre: item.name,
          codigo: item.code || 'N/A',
          descripcion: item.name, // Usar name para la columna "CLASE"
          original: item
        }));
        setTipoDocumentoData(documentTypesFormateados);
        
        // Cargar condiciones de pago
        setLoadingCondiciones(true);
        const condiciones = await fetchPaymentTerms();
        const condicionesFormateadas = condiciones.map(item => ({
          id: item.id,
          nombre: item.name,
          dias: item.paymentPeriod?.toString() || "0",
          descripcion: item.description || `Pago ${item.paymentPeriod === 0 ? 'inmediato' : `a ${item.paymentPeriod} días`}`,
          isCash: item.paymentPeriod === 0,
          isSplitPaymentAllowed: item.isSplitPaymentAllowed || false
        }));
        setCondicionPagoData(condicionesFormateadas);
      } catch (error) {
        console.error('Error cargando condiciones de pago:', error);
      } finally {
        setLoadingCondiciones(false);
        setLoadingDocumentTypes(false);
      }

      try {
        // Cargar formas de pago
        setLoadingFormas(true);
        const formas = await fetchPaymentForms();
        const formasFormateadas = formas.map(item => ({
          id: item.id,
          nombre: item.name,
          codigo: item.id?.toString() || "", // Usar ID como código
          descripcion: item.description || "Forma de pago",
          isCash: item.isCash || false,
          bankAccountIsRequested: item.bankAccountIsRequested || false
        }));
        setFormaPagoData(formasFormateadas);
      } catch (error) {
        console.error('Error cargando formas de pago:', error);
      } finally {
        setLoadingFormas(false);
      }
    };

    loadData();
  }, []);

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleOpenModal = (catalog, index = null) => {
    setCurrentCatalog(catalog);
    setEditingIndex(index);
    
    // If editing, populate form with existing data
    if (index !== null) {
      switch (catalog) {
        case "contribuyente": {
          const contribData = data[index];
          setFormData({
            nombre: contribData.nombre,
            impuestosAplicar: contribData.impuesto,
            codigo: "",
            descripcion: "",
            dias: ""
          });
          break;
        }
        case "tipoDocumento": {
          const tipoDocData = tipoDocumentoData[index];
          setFormData({
            nombre: tipoDocData.nombre,
            codigo: tipoDocData.codigo,
            descripcion: tipoDocData.descripcion,
            impuestosAplicar: "",
            dias: ""
          });
          break;
        }
        case "condicionPago": {
          const condPagoData = condicionPagoData[index];
          setFormData({
            nombre: condPagoData.nombre,
            dias: condPagoData.dias,
            descripcion: condPagoData.descripcion,
            impuestosAplicar: "",
            codigo: "",
            efectivo: condPagoData.efectivo || false,
            transferenciaBancaria: condPagoData.transferenciaBancaria || false,
            chequeEmision: condPagoData.chequeEmision || false,
            chequeRecepcion: condPagoData.chequeRecepcion || false,
            otrosGastosAnticipados: condPagoData.otrosGastosAnticipados || false,
            otrosVentasAnticipados: condPagoData.otrosVentasAnticipados || false
          });
          break;
        }
        case "formaPago": {
          const formPagoData = formaPagoData[index];
          setFormData({
            nombre: formPagoData.nombre,
            codigo: formPagoData.codigo,
            descripcion: formPagoData.descripcion,
            impuestosAplicar: "",
            dias: ""
          });
          break;
        }
        default:
          break;
      }
    } else {
      // Reset form for new entry
      setFormData({
        nombre: "",
        impuestosAplicar: "",
        codigo: "",
        descripcion: "",
        dias: "",
        efectivo: false,
        transferenciaBancaria: false,
        chequeEmision: false,
        chequeRecepcion: false,
        otrosGastosAnticipados: false,
        otrosVentasAnticipados: false
      });
    }
    
    setIsModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.nombre || !formData.nombre.trim()) {
      alert("Por favor, complete el campo de nombre");
      return;
    }

    try {
      switch (currentCatalog) {
        case "contribuyente":
          if (!formData.impuestosAplicar || !formData.impuestosAplicar.trim()) {
            alert("Por favor, seleccione el impuesto a aplicar");
            return;
          }
          
          if (editingIndex !== null) {
            const updatedData = [...data];
            updatedData[editingIndex] = {
              ...updatedData[editingIndex],
              nombre: formData.nombre,
              impuesto: formData.impuestosAplicar
            };
            setData(updatedData);
          } else {
            const nuevoContribuyente = {
              nombre: formData.nombre,
              impuesto: formData.impuestosAplicar,
              tipo: "Factura",
              codigo: Math.floor(Math.random() * 900000 + 100000).toString(),
              disminuye: "No",
              iva: "No",
            };
            setData(prev => [...prev, nuevoContribuyente]);
          }
          break;

        case "tipoDocumento":
          if (!formData.codigo || !formData.codigo.trim()) {
            alert("Por favor, complete el campo de código");
            return;
          }
          
          if (editingIndex !== null) {
            const updatedData = [...tipoDocumentoData];
            updatedData[editingIndex] = {
              nombre: formData.nombre,
              codigo: formData.codigo,
              descripcion: formData.descripcion || ""
            };
            setTipoDocumentoData(updatedData);
          } else {
            const nuevoTipoDoc = {
              nombre: formData.nombre,
              codigo: formData.codigo,
              descripcion: formData.descripcion || ""
            };
            setTipoDocumentoData(prev => [...prev, nuevoTipoDoc]);
          }
          break;

        case "condicionPago":
          if (!formData.dias || !formData.dias.trim()) {
            alert("Por favor, complete el campo de días");
            return;
          }
          
          if (editingIndex !== null) {
            // Edición - por ahora solo actualiza localmente
            const updatedData = [...condicionPagoData];
            updatedData[editingIndex] = {
              ...updatedData[editingIndex],
              nombre: formData.nombre,
              dias: formData.dias,
              descripcion: formData.descripcion || `Pago ${formData.dias === "0" ? 'inmediato' : `a ${formData.dias} días`}`,
              isCash: formData.dias === "0"
            };
            setCondicionPagoData(updatedData);
          } else {
            // Crear nueva condición de pago vía API
            const nuevaCondicionData = {
              name: formData.nombre,
              description: formData.descripcion || `Pago ${formData.dias === "0" ? 'inmediato' : `a ${formData.dias} días`}`,
              paymentPeriod: parseInt(formData.dias) || 0,
              isSplitPaymentAllowed: formData.dias !== "0",
              organizationId: 1 // TODO: obtener de contexto de usuario
            };

            const condicionCreada = await createPaymentTerm(nuevaCondicionData);
            
            // Agregar a la lista local
            const nuevaCondicionFormateada = {
              id: condicionCreada.id,
              nombre: condicionCreada.name,
              dias: condicionCreada.paymentPeriod?.toString() || "0",
              descripcion: condicionCreada.description || "",
              isCash: condicionCreada.paymentPeriod === 0,
              isSplitPaymentAllowed: condicionCreada.isSplitPaymentAllowed || false
            };
            
            setCondicionPagoData(prev => [...prev, nuevaCondicionFormateada]);
          }
          break;

        case "formaPago":
          if (editingIndex !== null) {
            // Edición - por ahora solo actualiza localmente
            const updatedData = [...formaPagoData];
            updatedData[editingIndex] = {
              ...updatedData[editingIndex],
              nombre: formData.nombre,
              codigo: formData.codigo,
              descripcion: formData.descripcion || ""
            };
            setFormaPagoData(updatedData);
          } else {
            // Crear nueva forma de pago vía API
            const nuevaFormaData = {
              name: formData.nombre,
              bankAccountIsRequested: formData.transferenciaBancaria || formData.chequeEmision || formData.chequeRecepcion,
              isCash: formData.efectivo,
              organizationId: 1 // TODO: obtener de contexto de usuario
            };

            const formaCreada = await createPaymentForm(nuevaFormaData);
            
            // Agregar a la lista local
            const nuevaFormaFormateada = {
              id: formaCreada.id,
              nombre: formaCreada.name,
              codigo: formaCreada.code || "",
              descripcion: formaCreada.description || "Forma de pago",
              isCash: formaCreada.isCash || false,
              bankAccountIsRequested: formaCreada.bankAccountIsRequested || false
            };
            
            setFormaPagoData(prev => [...prev, nuevaFormaFormateada]);
          }
          break;

        default:
          break;
      }

      handleCloseModal();
    } catch (error) {
      console.error('Error guardando datos:', error);
      alert('Error al guardar. Por favor, inténtelo de nuevo.');
    }
  };
  
  const handleCloseModal = () => {
    setFormData({ 
      nombre: "", 
      impuestosAplicar: "",
      codigo: "",
      descripcion: "",
      dias: "",
      efectivo: false,
      transferenciaBancaria: false,
      chequeEmision: false,
      chequeRecepcion: false,
      otrosGastosAnticipados: false,
      otrosVentasAnticipados: false
    });
    setIsModalOpen(false);
    setEditingIndex(null);
    setCurrentCatalog(null);
  };

  const handleRowSelect = (catalog, index) => {
    setSelectedRowIndex(index);
    setCurrentCatalog(catalog);
  };

  const handleEdit = () => {
    if (selectedRowIndex !== null && currentCatalog) {
      handleOpenModal(currentCatalog, selectedRowIndex);
      setSelectedRowIndex(null);
    }
  };

  const handleDelete = () => {
    if (selectedRowIndex === null || !currentCatalog) {
      return;
    }

    // Confirmación antes de eliminar
    const confirmDelete = window.confirm("¿Está seguro que desea eliminar este elemento?");
    if (!confirmDelete) {
      return;
    }

    // Eliminar según el catálogo actual
    switch (currentCatalog) {
      case "contribuyente":
        setData(prevData => prevData.filter((_, index) => index !== selectedRowIndex));
        break;
      case "tipoDocumento":
        setTipoDocumentoData(prevData => prevData.filter((_, index) => index !== selectedRowIndex));
        break;
      case "condicionPago":
        setCondicionPagoData(prevData => prevData.filter((_, index) => index !== selectedRowIndex));
        break;
      case "formaPago":
        setFormaPagoData(prevData => prevData.filter((_, index) => index !== selectedRowIndex));
        break;
      default:
        break;
    }

    // Limpiar selección después de eliminar
    setSelectedRowIndex(null);
    setCurrentCatalog(null);
  };

  const getModalTitle = () => {
    const action = editingIndex !== null ? "Editar" : "Crear Nuevo";
    switch (currentCatalog) {
      case "contribuyente":
        return `${action} Clasificación de Contribuyente`;
      case "tipoDocumento":
        return `${action} Tipo de Documento`;
      case "condicionPago":
        return `${action} Condición de Pago`;
      case "formaPago":
        return `${action} Forma de Pago`;
      default:
        return action;
    }
  };

  const renderModalContent = () => {
    switch (currentCatalog) {
      case "contribuyente":
        return (
          <>
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
          </>
        );

      case "tipoDocumento":
        return (
          <>
            <div>
              <label className="block text-sm font-medium mb-2" style={{ color: '#333' }}>
                Nombre
              </label>
              <input
                type="text"
                name="nombre"
                value={formData.nombre || ""}
                onChange={handleInputChange}
                placeholder="Factura"
                className="w-full px-3 py-3 sm:py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500 touch-manipulation text-base"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-2" style={{ color: '#333' }}>
                Código
              </label>
              <input
                type="text"
                name="codigo"
                value={formData.codigo || ""}
                onChange={handleInputChange}
                placeholder="01"
                className="w-full px-3 py-3 sm:py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500 touch-manipulation text-base"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-2" style={{ color: '#333' }}>
                Descripción
              </label>
              <input
                type="text"
                name="descripcion"
                value={formData.descripcion || ""}
                onChange={handleInputChange}
                placeholder="Descripción del tipo de documento"
                className="w-full px-3 py-3 sm:py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500 touch-manipulation text-base"
              />
            </div>
            
            {/* Checkboxes Section */}
            <div className="flex flex-col sm:flex-row gap-4 pt-2">
              <label className="flex items-center gap-2 text-sm cursor-pointer">
                <input
                  type="checkbox"
                  name="disminuyeValores"
                  checked={formData.disminuyeValores || false}
                  onChange={handleInputChange}
                  className="w-4 h-4 rounded border-gray-300 focus:ring-2 focus:ring-blue-500"
                  style={{ accentColor: '#0095FF' }}
                />
                <span style={{ color: '#333' }}>Disminuye valores e impuestos antes documentos</span>
              </label>
              
              <label className="flex items-center gap-2 text-sm cursor-pointer">
                <input
                  type="checkbox"
                  name="mostrarPreciosIVA"
                  checked={formData.mostrarPreciosIVA || false}
                  onChange={handleInputChange}
                  className="w-4 h-4 rounded border-gray-300 focus:ring-2 focus:ring-blue-500"
                  style={{ accentColor: '#0095FF' }}
                />
                <span style={{ color: '#333' }}>Mostrar precios con IVA</span>
              </label>
            </div>
          </>
        );

      case "condicionPago":
        return (
          <>
            <div>
              <label className="block text-sm font-medium mb-2" style={{ color: '#333' }}>
                Nombre
              </label>
              <input
                type="text"
                name="nombre"
                value={formData.nombre || ""}
                onChange={handleInputChange}
                placeholder="Contado"
                className="w-full px-3 py-3 sm:py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500 touch-manipulation text-base"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-2" style={{ color: '#333' }}>
                Días plazo de pago
              </label>
              <select
                name="dias"
                value={formData.dias || ""}
                onChange={handleInputChange}
                className="w-full px-3 py-3 sm:py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500 text-gray-500 touch-manipulation text-base"
                required
              >
                <option value="">Seleccional el plazo</option>
                <option value="0">0 días - Contado</option>
                <option value="15">15 días</option>
                <option value="30">30 días</option>
                <option value="45">45 días</option>
                <option value="60">60 días</option>
                <option value="90">90 días</option>
              </select>
            </div>

            {/* Checkboxes para tipos de condición de pago */}
            <div className="space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <label className="flex items-center gap-2 text-sm cursor-pointer">
                  <input
                    type="checkbox"
                    name="efectivo"
                    checked={formData.efectivo || false}
                    onChange={handleInputChange}
                    className="w-4 h-4 rounded border-gray-300 focus:ring-2 focus:ring-blue-500"
                    style={{ accentColor: '#0095FF' }}
                  />
                  <span style={{ color: '#333' }}>Efectivo</span>
                </label>

                <label className="flex items-center gap-2 text-sm cursor-pointer">
                  <input
                    type="checkbox"
                    name="transferenciaBancaria"
                    checked={formData.transferenciaBancaria || false}
                    onChange={handleInputChange}
                    className="w-4 h-4 rounded border-gray-300 focus:ring-2 focus:ring-blue-500"
                    style={{ accentColor: '#0095FF' }}
                  />
                  <span style={{ color: '#333' }}>Transferencia bancaria</span>
                </label>

                <label className="flex items-center gap-2 text-sm cursor-pointer">
                  <input
                    type="checkbox"
                    name="chequeEmision"
                    checked={formData.chequeEmision || false}
                    onChange={handleInputChange}
                    className="w-4 h-4 rounded border-gray-300 focus:ring-2 focus:ring-blue-500"
                    style={{ accentColor: '#0095FF' }}
                  />
                  <span style={{ color: '#333' }}>Cheque de emisión</span>
                </label>

                <label className="flex items-center gap-2 text-sm cursor-pointer">
                  <input
                    type="checkbox"
                    name="chequeRecepcion"
                    checked={formData.chequeRecepcion || false}
                    onChange={handleInputChange}
                    className="w-4 h-4 rounded border-gray-300 focus:ring-2 focus:ring-blue-500"
                    style={{ accentColor: '#0095FF' }}
                  />
                  <span style={{ color: '#333' }}>Cheque de recepción</span>
                </label>

                <label className="flex items-center gap-2 text-sm cursor-pointer">
                  <input
                    type="checkbox"
                    name="otrosGastosAnticipados"
                    checked={formData.otrosGastosAnticipados || false}
                    onChange={handleInputChange}
                    className="w-4 h-4 rounded border-gray-300 focus:ring-2 focus:ring-blue-500"
                    style={{ accentColor: '#0095FF' }}
                  />
                  <span style={{ color: '#333' }}>Otros gastos pagados anticipados</span>
                </label>

                <label className="flex items-center gap-2 text-sm cursor-pointer">
                  <input
                    type="checkbox"
                    name="otrosVentasAnticipados"
                    checked={formData.otrosVentasAnticipados || false}
                    onChange={handleInputChange}
                    className="w-4 h-4 rounded border-gray-300 focus:ring-2 focus:ring-blue-500"
                    style={{ accentColor: '#0095FF' }}
                  />
                  <span style={{ color: '#333' }}>Otros ventas pagados anticipados</span>
                </label>
              </div>
            </div>
          </>
        );

      case "formaPago":
        return (
          <>
            <div>
              <label className="block text-sm font-medium mb-2" style={{ color: '#333' }}>
                Nombre
              </label>
              <input
                type="text"
                name="nombre"
                value={formData.nombre || ""}
                onChange={handleInputChange}
                placeholder="Efectivo"
                className="w-full px-3 py-3 sm:py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500 touch-manipulation text-base"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-2" style={{ color: '#333' }}>
                Código
              </label>
              <input
                type="text"
                name="codigo"
                value={formData.codigo || ""}
                onChange={handleInputChange}
                placeholder="01"
                className="w-full px-3 py-3 sm:py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500 touch-manipulation text-base"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-2" style={{ color: '#333' }}>
                Descripción
              </label>
              <input
                type="text"
                name="descripcion"
                value={formData.descripcion || ""}
                onChange={handleInputChange}
                placeholder="Descripción de la forma de pago"
                className="w-full px-3 py-3 sm:py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500 touch-manipulation text-base"
              />
            </div>
          </>
        );

      default:
        return null;
    }
  };

  return (
    <>
      <div className="pt-28 lg:pt-32 ml-4 lg:ml-8">
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 max-w-none">
        {/* Header con título Configuración y botón Exportar */}
          <div className="flex justify-between items-center px-8 lg:px-8 py-6 lg:py-8 border-b border-gray-100">
            <h2 className="text-2xl sm:text-3xl font-semibold" style={{ color: '#000000' }}>
              Configuración
            </h2>
            <button
              className="px-4 py-3 sm:py-2 rounded-lg text-gray-600 font-medium hover:opacity-90 transition border border-gray-300 touch-manipulation min-h-[44px]"
              style={{ backgroundColor: '#F7FAFF' }}
            >
              📤 Exportar
            </button>
          </div>

          {/* Sección de Clasificación de Contribuyente */}
          <div className="px-8 lg:px-8 py-6 lg:py-8 border-b border-gray-100">
            <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-6 lg:mb-8 gap-4">
              <h2 className="text-2xl sm:text-3xl font-semibold" style={{ color: '#0095FF' }}>
                Clasificación de Contribuyente
              </h2>
              <div className="flex gap-2">
                {/* Icono de Editar */}
                <button
                  onClick={handleEdit}
                  disabled={selectedRowIndex === null || currentCatalog !== "contribuyente"}
                  className={`p-2 rounded-lg transition-colors touch-manipulation ${
                    selectedRowIndex !== null && currentCatalog === "contribuyente"
                      ? 'hover:opacity-90'
                      : 'opacity-40 cursor-not-allowed'
                  }`}
                  style={{ backgroundColor: '#F7FAFF' }}
                  aria-label="Editar configuración"
                  title="Editar configuración"
                >
                  <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                </button>
                
                {/* Icono de Eliminar */}
                <button
                  onClick={handleDelete}
                  disabled={selectedRowIndex === null || currentCatalog !== "contribuyente"}
                  className={`p-2 rounded-lg transition-colors touch-manipulation ${
                    selectedRowIndex !== null && currentCatalog === "contribuyente"
                      ? 'hover:opacity-90'
                      : 'opacity-40 cursor-not-allowed'
                  }`}
                  style={{ backgroundColor: '#F7FAFF' }}
                  aria-label="Eliminar configuración"
                  title="Eliminar configuración"
                >
                  <svg className="w-5 h-5 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              </div>
            </div>

            <Table 
              columns={columns} 
              data={data} 
              onRowSelect={(index) => handleRowSelect("contribuyente", index)}
              selectedRowIndex={currentCatalog === "contribuyente" ? selectedRowIndex : null}
            />
            
            {/* Agregar Nueva Clasificacion de Contribuyente */}
            <div className="mt-6 lg:mt-8">
              <button
                onClick={() => handleOpenModal("contribuyente")}
                className="flex items-center gap-2 text-blue-500 hover:text-blue-600 transition font-medium px-4 py-3 sm:py-2 rounded-lg border border-gray-300 touch-manipulation min-h-[44px]"
                style={{ backgroundColor: '#FFFFFF' }}
              >
                <span className="text-xl">⊕</span>
                Agregar Nueva Clasificación de Contribuyente
              </button>
            </div>
          </div>

          {/* Sección de Tipo de Documento */}
          <div className="px-8 lg:px-8 py-6 lg:py-8 border-b border-gray-100">
            <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-6 lg:mb-8 gap-4">
              <h2 className="text-2xl sm:text-3xl font-semibold" style={{ color: '#0095FF' }}>
                Tipo de Documento
              </h2>
              <div className="flex gap-2">
                {/* Icono de Editar */}
                <button
                  onClick={handleEdit}
                  disabled={selectedRowIndex === null || currentCatalog !== "tipoDocumento"}
                  className={`p-2 rounded-lg transition-colors touch-manipulation ${
                    selectedRowIndex !== null && currentCatalog === "tipoDocumento"
                      ? 'hover:opacity-90'
                      : 'opacity-40 cursor-not-allowed'
                  }`}
                  style={{ backgroundColor: '#F7FAFF' }}
                  aria-label="Editar tipo de documento"
                  title="Editar tipo de documento"
                >
                  <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                </button>
                
                {/* Icono de Eliminar */}
                <button
                  onClick={handleDelete}
                  disabled={selectedRowIndex === null || currentCatalog !== "tipoDocumento"}
                  className={`p-2 rounded-lg transition-colors touch-manipulation ${
                    selectedRowIndex !== null && currentCatalog === "tipoDocumento"
                      ? 'hover:opacity-90'
                      : 'opacity-40 cursor-not-allowed'
                  }`}
                  style={{ backgroundColor: '#F7FAFF' }}
                  aria-label="Eliminar tipo de documento"
                  title="Eliminar tipo de documento"
                >
                  <svg className="w-5 h-5 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              </div>
            </div>

            {loadingDocumentTypes ? (
              <div className="flex items-center justify-center py-8">
                <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-500"></div>
                <span className="ml-2 text-gray-600">Cargando tipos de documentos...</span>
              </div>
            ) : (
              <Table 
                columns={[
                  { header: "Nombre", accessor: "nombre" },
                  { header: "Código", accessor: "codigo" },
                  { header: "Clase", accessor: "descripcion" }
                ]} 
                data={tipoDocumentoData}
                onRowSelect={(index) => handleRowSelect("tipoDocumento", index)}
                selectedRowIndex={currentCatalog === "tipoDocumento" ? selectedRowIndex : null}
              />
            )}
            
            {/* Agregar Nuevo Tipo de Documento */}
            <div className="mt-6 lg:mt-8">
              <button
                onClick={() => handleOpenModal("tipoDocumento")}
                className="flex items-center gap-2 text-blue-500 hover:text-blue-600 transition font-medium px-4 py-3 sm:py-2 rounded-lg border border-gray-300 touch-manipulation min-h-[44px]"
                style={{ backgroundColor: '#FFFFFF' }}
              >
                <span className="text-xl">⊕</span>
                Agregar Nuevo Tipo de Documento
              </button>
            </div>
          </div>

          {/* Sección de Condición de Pago */}
          <div className="px-8 lg:px-8 py-6 lg:py-8 border-b border-gray-100">
            <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-6 lg:mb-8 gap-4">
              <h2 className="text-2xl sm:text-3xl font-semibold" style={{ color: '#0095FF' }}>
                Condición de Pago
              </h2>
              <div className="flex gap-2">
                {/* Icono de Editar */}
                <button
                  onClick={handleEdit}
                  disabled={selectedRowIndex === null || currentCatalog !== "condicionPago"}
                  className={`p-2 rounded-lg transition-colors touch-manipulation ${
                    selectedRowIndex !== null && currentCatalog === "condicionPago"
                      ? 'hover:opacity-90'
                      : 'opacity-40 cursor-not-allowed'
                  }`}
                  style={{ backgroundColor: '#F7FAFF' }}
                  aria-label="Editar condición de pago"
                  title="Editar condición de pago"
                >
                  <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                </button>
                
                {/* Icono de Eliminar */}
                <button
                  onClick={handleDelete}
                  disabled={selectedRowIndex === null || currentCatalog !== "condicionPago"}
                  className={`p-2 rounded-lg transition-colors touch-manipulation ${
                    selectedRowIndex !== null && currentCatalog === "condicionPago"
                      ? 'hover:opacity-90'
                      : 'opacity-40 cursor-not-allowed'
                  }`}
                  style={{ backgroundColor: '#F7FAFF' }}
                  aria-label="Eliminar condición de pago"
                  title="Eliminar condición de pago"
                >
                  <svg className="w-5 h-5 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              </div>
            </div>

            <Table 
              columns={[
                { header: "Nombre", accessor: "nombre" },
                { header: "Días", accessor: "dias" },
                { header: "Descripción", accessor: "descripcion" }
              ]} 
              data={condicionPagoData}
              onRowSelect={(index) => handleRowSelect("condicionPago", index)}
              selectedRowIndex={currentCatalog === "condicionPago" ? selectedRowIndex : null}
            />
            
            {/* Agregar Nueva Condición de Pago */}
            <div className="mt-6 lg:mt-8">
              <button
                onClick={() => handleOpenModal("condicionPago")}
                className="flex items-center gap-2 text-blue-500 hover:text-blue-600 transition font-medium px-4 py-3 sm:py-2 rounded-lg border border-gray-300 touch-manipulation min-h-[44px]"
                style={{ backgroundColor: '#FFFFFF' }}
              >
                <span className="text-xl">⊕</span>
                Agregar Nueva Condición de Pago
              </button>
            </div>
          </div>

          {/* Sección de Forma de Pago */}
          <div className="px-8 lg:px-8 py-6 lg:py-8">
            <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-6 lg:mb-8 gap-4">
              <h2 className="text-2xl sm:text-3xl font-semibold" style={{ color: '#0095FF' }}>
                Forma de Pago
              </h2>
              <div className="flex gap-2">
                {/* Icono de Editar */}
                <button
                  onClick={handleEdit}
                  disabled={selectedRowIndex === null || currentCatalog !== "formaPago"}
                  className={`p-2 rounded-lg transition-colors touch-manipulation ${
                    selectedRowIndex !== null && currentCatalog === "formaPago"
                      ? 'hover:opacity-90'
                      : 'opacity-40 cursor-not-allowed'
                  }`}
                  style={{ backgroundColor: '#F7FAFF' }}
                  aria-label="Editar forma de pago"
                  title="Editar forma de pago"
                >
                  <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                </button>
                
                {/* Icono de Eliminar */}
                <button
                  onClick={handleDelete}
                  disabled={selectedRowIndex === null || currentCatalog !== "formaPago"}
                  className={`p-2 rounded-lg transition-colors touch-manipulation ${
                    selectedRowIndex !== null && currentCatalog === "formaPago"
                      ? 'hover:opacity-90'
                      : 'opacity-40 cursor-not-allowed'
                  }`}
                  style={{ backgroundColor: '#F7FAFF' }}
                  aria-label="Eliminar forma de pago"
                  title="Eliminar forma de pago"
                >
                  <svg className="w-5 h-5 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              </div>
            </div>

            <Table 
              columns={[
                { header: "Nombre", accessor: "nombre" },
                { header: "Código", accessor: "codigo" },
                { header: "Descripción", accessor: "descripcion" }
              ]} 
              data={formaPagoData}
              onRowSelect={(index) => handleRowSelect("formaPago", index)}
              selectedRowIndex={currentCatalog === "formaPago" ? selectedRowIndex : null}
            />
            
            {/* Agregar Nueva Forma de Pago */}
            <div className="mt-6 lg:mt-8">
              <button
                onClick={() => handleOpenModal("formaPago")}
                className="flex items-center gap-2 text-blue-500 hover:text-blue-600 transition font-medium px-4 py-3 sm:py-2 rounded-lg border border-gray-300 touch-manipulation min-h-[44px]"
                style={{ backgroundColor: '#FFFFFF' }}
              >
                <span className="text-xl">⊕</span>
                Agregar Nueva Forma de Pago
              </button>
            </div>
          </div>
        </div>

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={getModalTitle()}
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          {renderModalContent()}
          
          <div className="flex justify-end pt-4">
            <button
              type="submit"
              className="px-6 py-3 sm:py-2 rounded-lg text-white font-medium hover:opacity-90 transition flex items-center gap-2 touch-manipulation"
              style={{ backgroundColor: '#0095FF' }}
            >
              <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M3 4a1 1 0 011-1h12a1 1 0 011 1v12a1 1 0 01-1 1H4a1 1 0 01-1-1V4zm12 0H5v12h10V4z" clipRule="evenodd" />
                <path fillRule="evenodd" d="M8 8a1 1 0 011-1h2a1 1 0 110 2H9a1 1 0 01-1-1zm0 3a1 1 0 011-1h2a1 1 0 110 2H9a1 1 0 01-1-1z" clipRule="evenodd" />
              </svg>
              Guardar
            </button>
          </div>
        </form>
      </Modal>
      </div>
    </>
  );
};

export default Configuracion;