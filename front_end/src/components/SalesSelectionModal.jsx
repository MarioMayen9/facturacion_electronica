// src/components/SalesSelectionModal.jsx
import React, { useState, useEffect } from 'react';
import { fetchSalesWithParams, fetchClients } from '../services/api';

const SalesSelectionModal = ({ isOpen, onClose, onSelectSale, onCreateNew }) => {
  const [sales, setSales] = useState([]);
  const [clients, setClients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [totalRecords, setTotalRecords] = useState(0);
  const [selectedSale, setSelectedSale] = useState(null);
  const perPage = 10;

  // Cargar ventas y clientes de la API
  useEffect(() => {
    if (isOpen) {
      loadData();
    }
  }, [isOpen]);

  // Cargar ventas con paginación
  const loadSales = async (page = 1, search = '') => {
    try {
      setLoading(true);
      const response = await fetchSalesWithParams(page, perPage, search);
      
      setSales(response.sales);
      setCurrentPage(response.pagination.currentPage);
      setTotalPages(response.pagination.totalPages);
      setTotalRecords(response.pagination.totalRecords);
      
      setError(null);
    } catch (err) {
      setError('Error cargando ventas');
      console.error('Error loading sales:', err);
    } finally {
      setLoading(false);
    }
  };

  // Cargar datos iniciales
  const loadData = async () => {
    try {
      setLoading(true);
      const [, clientsData] = await Promise.all([
        loadSales(1, searchTerm),
        fetchClients()
      ]);
      
      setClients(clientsData);
    } catch (err) {
      setError('Error cargando datos');
      console.error('Error loading data:', err);
    } finally {
      setLoading(false);
    }
  };

  // Funciones de paginación
  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
    loadSales(newPage, searchTerm);
  };

  const handlePreviousPage = () => {
    if (currentPage > 1) {
      handlePageChange(currentPage - 1);
    }
  };

  const handleNextPage = () => {
    if (currentPage < totalPages) {
      handlePageChange(currentPage + 1);
    }
  };

  // Función de búsqueda actualizada
  const handleSearch = (newSearchTerm) => {
    setSearchTerm(newSearchTerm);
    setCurrentPage(1);
    loadSales(1, newSearchTerm);
  };

  // Filtrar ventas por búsqueda (ya no necesario, la API maneja el filtrado)
  const filteredSales = sales;

  // La paginación ahora se maneja por la API
  const paginatedSales = filteredSales;

  const formatDate = (dateString) => {
    if (!dateString) return '';
    try {
      return new Date(dateString).toLocaleDateString('es-ES', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      });
    } catch {
      return dateString;
    }
  };

  const formatCurrency = (amount) => {
    if (!amount && amount !== 0) return '';
    const numAmount = parseFloat(amount);
    if (isNaN(numAmount)) return '';
    
    // Format with thousands separators
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(numAmount);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center z-50 p-4 backdrop-blur-sm"
         style={{ backgroundColor: '#42424259' }}>
      <div className="bg-white rounded-xl shadow-lg w-full max-w-[95vw] mx-4 relative max-h-[95vh] overflow-hidden border border-gray-100">
        
        {/* Header */}
        <div className="flex justify-between items-center px-6 py-4 border-b border-gray-100">
          <h2 className="text-xl font-semibold" style={{ color: '#0095FF' }}>
            Seleccione la venta
          </h2>
          
          <div className="flex items-center gap-3">
            {/* Botón cerrar */}
            <button
              onClick={onClose}
              className="text-xl font-bold p-2 rounded-lg transition-colors"
              style={{ 
                color: '#4242428C',
                backgroundColor: '#FFFFFF',
                border: '1px solid #E5E5E5'
              }}
              onMouseEnter={(e) => {
                e.target.style.color = '#424242';
                e.target.style.backgroundColor = '#F8F8F8';
              }}
              onMouseLeave={(e) => {
                e.target.style.color = '#4242428C';
                e.target.style.backgroundColor = '#FFFFFF';
              }}
            >
              ×
            </button>
          </div>
        </div>

        {/* Barra de búsqueda y botones de acción */}
        <div className="px-6 py-4 border-b border-gray-100">
          <div className="flex items-center justify-end">
            {/* Todos los controles agrupados a la derecha */}
            <div className="flex items-center gap-3">
              <div className="relative">
                <input
                  type="text"
                  placeholder="Buscar"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter') {
                      handleSearch(e.target.value);
                    }
                  }}
                  onBlur={(e) => handleSearch(e.target.value)}
                  className="w-64 px-4 py-2 pl-10 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  style={{ backgroundColor: '#F7FAFF' }}
                />
                <svg className="w-5 h-5 text-gray-400 absolute left-3 top-2.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                        d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>

              <button
                onClick={onCreateNew}
                className="flex items-center gap-2 px-4 py-2 rounded-lg transition-all duration-200"
                style={{ 
                  backgroundColor: '#F7FAFF',
                  border: '1px solid #1890FF17',
                  boxShadow: '0px 5.1px 10.21px 0px #0000000A',
                  color: '#0095FF'
                }}
                title="Generar nueva venta"
              >
                <span className="text-lg">+</span>
                Generar nueva
              </button>

              <button
                onClick={() => selectedSale && onSelectSale(selectedSale)}
                disabled={!selectedSale}
                className="flex items-center gap-2 px-4 py-2 rounded-lg transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                style={{ 
                  backgroundColor: selectedSale ? '#0095FF' : '#F7FAFF',
                  border: '1px solid #1890FF17',
                  boxShadow: '0px 5.1px 10.21px 0px #0000000A',
                  color: selectedSale ? '#FFFFFF' : '#9C9C9C'
                }}
                title="Seleccionar esta venta"
              >
                <span className="text-lg">✓</span>
                Seleccionar
              </button>
            </div>
          </div>
        </div>

        {/* Contenido de la tabla */}
        <div className="overflow-y-auto max-h-[75vh]">
          {loading ? (
            <div className="flex justify-center items-center h-64">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
              <span className="ml-2 text-gray-600">Cargando ventas...</span>
            </div>
          ) : error ? (
            <div className="flex flex-col items-center justify-center h-64 text-red-500">
              <p>{error}</p>
              <button 
                onClick={loadData}
                className="mt-2 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition"
              >
                Reintentar
              </button>
            </div>
          ) : (
            <table className="w-full text-sm">
              <thead style={{ backgroundColor: '#1890FF26' }}>
                <tr>
                  <th className="px-4 py-3 text-left text-xs font-medium text-blue-600 uppercase tracking-wider">Fecha de registro</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-blue-600 uppercase tracking-wider">Correlativo</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-blue-600 uppercase tracking-wider">Fecha de emisión</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-blue-600 uppercase tracking-wider">Cliente</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-blue-600 uppercase tracking-wider">Condición de pago</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-blue-600 uppercase tracking-wider">Descripción</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-blue-600 uppercase tracking-wider">Tipo de documento</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-blue-600 uppercase tracking-wider">N° Doc</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-blue-600 uppercase tracking-wider">Fecha de pago</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-blue-600 uppercase tracking-wider">Fecha de cobro</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-blue-600 uppercase tracking-wider">Estado</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-blue-600 uppercase tracking-wider">Total</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {paginatedSales.map((sale, index) => (
                  <tr 
                    key={sale.id} 
                    onClick={() => setSelectedSale(selectedSale?.id === sale.id ? null : sale)}
                    className={`cursor-pointer transition-colors ${
                      selectedSale?.id === sale.id 
                        ? 'bg-blue-50 border-l-4 border-blue-500' 
                        : 'hover:bg-gray-50'
                    }`}
                  >
                    <td className="px-4 py-3 text-gray-900">{formatDate(sale.registration_date) || 'N/A'}</td>
                    <td className="px-4 py-3 text-gray-900">{sale.sale_order_number || 'N/A'}</td>
                    <td className="px-4 py-3 text-gray-900">{formatDate(sale.emission_date) || 'N/A'}</td>
                    <td className="px-4 py-3">
                      <div className="flex items-center">
                        <div className="w-8 h-8 rounded-full bg-gray-300 flex items-center justify-center mr-3">
                          <span className="text-xs font-medium text-gray-600">
                            {(sale.client_name || 'S').charAt(0).toUpperCase()}
                          </span>
                        </div>
                        <span className="text-gray-900">{sale.client_name || 'Sin cliente'}</span>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-gray-900">{sale.payment_condition || 'N/A'}</td>
                    <td className="px-4 py-3 text-gray-900 max-w-xs truncate" title={sale.description}>
                      {sale.description || 'Sin descripción'}
                    </td>
                    <td className="px-4 py-3 text-gray-900">{sale.document_type || 'N/A'}</td>
                    <td className="px-4 py-3 text-gray-900">{sale.document_number || 'N/A'}</td>
                    <td className="px-4 py-3 text-gray-900">{formatDate(sale.payment_date) || 'N/A'}</td>
                    <td className="px-4 py-3 text-gray-900">{formatDate(sale.collection_date) || 'N/A'}</td>
                    <td className="px-4 py-3">
                      <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                        !sale.status ? 'bg-gray-100 text-gray-600' :
                        sale.status.toLowerCase().includes('pagado') || sale.status.toLowerCase().includes('completado') ? 'bg-green-100 text-green-800' :
                        sale.status.toLowerCase().includes('pendiente') || sale.status.toLowerCase().includes('proceso') ? 'bg-yellow-100 text-yellow-800' :
                        sale.status.toLowerCase().includes('cancelado') || sale.status.toLowerCase().includes('rechazado') ? 'bg-red-100 text-red-800' :
                        'bg-blue-100 text-blue-800'
                      }`}>
                        {sale.status || 'Sin estado'}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-gray-900 font-medium">{formatCurrency(sale.total)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        {/* Paginación */}
        {!loading && totalPages > 1 && (
          <div className="flex justify-between items-center px-6 py-4 border-t border-gray-100">
            <div className="text-sm text-gray-600">
              Página {currentPage} de {totalPages} - Mostrando {paginatedSales.length} de {totalRecords} ventas
            </div>
            <div className="flex items-center gap-2">
              <button
                onClick={handlePreviousPage}
                disabled={currentPage === 1}
                className="px-4 py-2 rounded-lg border text-sm font-medium transition"
                style={{
                  backgroundColor: currentPage === 1 ? '#F5F5F5' : '#FFFFFF',
                  color: currentPage === 1 ? '#9CA3AF' : '#374151',
                  borderColor: currentPage === 1 ? '#E5E7EB' : '#D1D5DB',
                  cursor: currentPage === 1 ? 'not-allowed' : 'pointer'
                }}
                onMouseEnter={(e) => {
                  if (currentPage !== 1) {
                    e.target.style.backgroundColor = '#F9FAFB';
                  }
                }}
                onMouseLeave={(e) => {
                  if (currentPage !== 1) {
                    e.target.style.backgroundColor = '#FFFFFF';
                  }
                }}
              >
                ← Anterior
              </button>
              
              {/* Números de página */}
              <div className="flex gap-1">
                {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                  let pageNum;
                  if (totalPages <= 5) {
                    pageNum = i + 1;
                  } else if (currentPage <= 3) {
                    pageNum = i + 1;
                  } else if (currentPage >= totalPages - 2) {
                    pageNum = totalPages - 4 + i;
                  } else {
                    pageNum = currentPage - 2 + i;
                  }
                  
                  const isCurrentPage = pageNum === currentPage;
                  
                  return (
                    <button
                      key={pageNum}
                      onClick={() => handlePageChange(pageNum)}
                      className="w-10 h-10 rounded-lg text-sm font-medium transition"
                      style={{
                        backgroundColor: isCurrentPage ? '#3B82F6' : '#FFFFFF',
                        color: isCurrentPage ? '#FFFFFF' : '#374151',
                        border: isCurrentPage ? 'none' : '1px solid #D1D5DB'
                      }}
                      onMouseEnter={(e) => {
                        if (!isCurrentPage) {
                          e.target.style.backgroundColor = '#F9FAFB';
                        }
                      }}
                      onMouseLeave={(e) => {
                        if (!isCurrentPage) {
                          e.target.style.backgroundColor = '#FFFFFF';
                        }
                      }}
                    >
                      {pageNum}
                    </button>
                  );
                })}
              </div>

              <button
                onClick={handleNextPage}
                disabled={currentPage === totalPages}
                className="px-4 py-2 rounded-lg border text-sm font-medium transition"
                style={{
                  backgroundColor: currentPage === totalPages ? '#F5F5F5' : '#FFFFFF',
                  color: currentPage === totalPages ? '#9CA3AF' : '#374151',
                  borderColor: currentPage === totalPages ? '#E5E7EB' : '#D1D5DB',
                  cursor: currentPage === totalPages ? 'not-allowed' : 'pointer'
                }}
                onMouseEnter={(e) => {
                  if (currentPage !== totalPages) {
                    e.target.style.backgroundColor = '#F9FAFB';
                  }
                }}
                onMouseLeave={(e) => {
                  if (currentPage !== totalPages) {
                    e.target.style.backgroundColor = '#FFFFFF';
                  }
                }}
              >
                Siguiente →
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default SalesSelectionModal;