import React, { useState, useEffect } from 'react';
import { fetchClientsFromExternalAPI } from '../services/api';

const ClientSelectionModal = ({ isOpen, onClose, onSelectClient, onCreateNew }) => {
  const [clients, setClients] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [pagination, setPagination] = useState({});

  useEffect(() => {
    if (isOpen) {
      loadClients();
    }
  }, [isOpen, currentPage]);

  useEffect(() => {
    if (isOpen) {
      // Reset search and reload when modal opens
      setSearchTerm('');
      setCurrentPage(1);
    }
  }, [isOpen]);

  useEffect(() => {
    // Debounce search
    const timeoutId = setTimeout(() => {
      if (searchTerm !== '') {
        setCurrentPage(1);
        loadClients(searchTerm);
      } else if (searchTerm === '') {
        loadClients();
      }
    }, 300);

    return () => clearTimeout(timeoutId);
  }, [searchTerm]);

  const loadClients = async (search = '') => {
    try {
      setLoading(true);
      setError(null);
      console.log('Cargando clientes desde API externa...', { currentPage, search });
      const response = await fetchClientsFromExternalAPI(currentPage, 15, search);
      
      console.log('Respuesta de la API:', response);
      setClients(response.clients || []);
      setPagination(response.pagination || {});
    } catch (err) {
      setError('Error cargando clientes');
      console.error('Error loading clients:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectClient = (client) => {
    onSelectClient(client);
    onClose();
  };

  const handleCreateNew = () => {
    onCreateNew();
    onClose();
  };

  const getClientInitials = (name) => {
    if (!name) return '?';
    const words = name.split(' ').filter(word => word.length > 0);
    if (words.length >= 2) {
      return (words[0][0] + words[1][0]).toUpperCase();
    } else if (words.length === 1) {
      return words[0][0].toUpperCase();
    }
    return '?';
  };

  const getClientDisplayLocation = (client) => {
    const parts = [];
    if (client.city) parts.push(client.city);
    if (client.state) parts.push(client.state);
    return parts.join(', ') || 'Sin ubicación';
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center z-50 p-4 backdrop-blur-sm"
         style={{ backgroundColor: '#42424259' }}>
      <div className="bg-white rounded-xl shadow-lg w-full max-w-[95vw] mx-4 relative max-h-[95vh] overflow-hidden border border-gray-100">
        {/* Header */}
        <div className="flex justify-between items-center px-6 py-4 border-b border-gray-100">
          <h2 className="text-xl font-semibold" style={{ color: '#0095FF' }}>
            Seleccione el cliente
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
                  placeholder="Buscar cliente..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-80 pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  style={{
                    backgroundColor: '#FFFFFF',
                    border: '1px solid #E5E5E5',
                    borderRadius: '8px'
                  }}
                />
                <svg className="w-4 h-4 absolute left-3 top-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>
              
              <button
                onClick={handleCreateNew}
                className="flex items-center gap-2 px-4 py-2 rounded-lg transition-colors"
                style={{
                  backgroundColor: '#0095FF',
                  color: '#FFFFFF',
                  border: 'none',
                  borderRadius: '8px'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = '#0080E6';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = '#0095FF';
                }}
              >
                <span className="text-lg">+</span>
                Crear nuevo cliente
              </button>
            </div>
          </div>
        </div>

        {/* Contenido principal */}
        <div className="flex-1 overflow-hidden" style={{ height: 'calc(95vh - 200px)' }}>
          {loading ? (
            <div className="flex items-center justify-center h-full">
              <div className="text-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
                <p className="text-gray-600">Cargando clientes...</p>
              </div>
            </div>
          ) : error ? (
            <div className="flex flex-col items-center justify-center h-64 text-red-500">
              <p>{error}</p>
              <button 
                onClick={() => loadClients(searchTerm)}
                className="mt-4 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
              >
                Reintentar
              </button>
            </div>
          ) : (
            <div className="overflow-auto h-full">
              <table className="w-full">
                <thead className="bg-gray-50 sticky top-0">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Cliente
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Email
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Teléfono
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      DUI
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Ubicación
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Estado
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Acción
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {clients.length > 0 ? (
                    clients.map((client) => (
                      <tr key={client.id} className="hover:bg-gray-50 transition-colors cursor-pointer">
                        <td className="px-6 py-4">
                          <div className="flex items-center">
                            <div className="w-10 h-10 rounded-full bg-gradient-to-br from-blue-400 to-purple-600 flex items-center justify-center flex-shrink-0 mr-3">
                              <span className="text-white font-semibold text-sm">
                                {getClientInitials(client.name)}
                              </span>
                            </div>
                            <div>
                              <div className="font-medium text-gray-900">
                                {client.name}
                              </div>
                              {client.total_orders > 0 && (
                                <div className="text-sm text-gray-500">
                                  {client.total_orders} orden{client.total_orders > 1 ? 'es' : ''}
                                </div>
                              )}
                            </div>
                          </div>
                        </td>
                        <td className="px-6 py-4 text-sm text-gray-900">
                          {client.email || 'Sin email'}
                        </td>
                        <td className="px-6 py-4 text-sm text-gray-900">
                          {client.phone || 'Sin teléfono'}
                        </td>
                        <td className="px-6 py-4 text-sm text-gray-900">
                          {client.single_identity_document_number || 'Sin DUI'}
                        </td>
                        <td className="px-6 py-4 text-sm text-gray-900">
                          {getClientDisplayLocation(client)}
                        </td>
                        <td className="px-6 py-4">
                          <div className="flex gap-1">
                            {client.is_vip === 1 && (
                              <span className="inline-flex px-2 py-1 text-xs rounded-full bg-yellow-100 text-yellow-800">
                                VIP
                              </span>
                            )}
                            {client.is_employee === 1 && (
                              <span className="inline-flex px-2 py-1 text-xs rounded-full bg-blue-100 text-blue-800">
                                Empleado
                              </span>
                            )}
                            {client.is_vip === 0 && client.is_employee === 0 && (
                              <span className="inline-flex px-2 py-1 text-xs rounded-full bg-gray-100 text-gray-800">
                                Regular
                              </span>
                            )}
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <button
                            onClick={() => handleSelectClient(client)}
                            className="flex items-center justify-center w-8 h-8 rounded-full transition-colors"
                            style={{
                              backgroundColor: '#0095FF',
                              color: '#FFFFFF'
                            }}
                            onMouseEnter={(e) => {
                              e.target.style.backgroundColor = '#0080E6';
                            }}
                            onMouseLeave={(e) => {
                              e.target.style.backgroundColor = '#0095FF';
                            }}
                          >
                            <span className="text-lg">✓</span>
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan={7} className="px-6 py-8 text-center text-gray-500">
                        {searchTerm ? `No se encontraron clientes para "${searchTerm}"` : 'No hay clientes disponibles'}
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Paginación */}
        {pagination.totalPages > 1 && (
          <div className="px-6 py-4 border-t border-gray-100">
            <div className="flex items-center justify-between">
              <div className="text-sm text-gray-500">
                {pagination.from} - {pagination.to} de {pagination.totalRecords} resultados | Página {currentPage}
              </div>
              
              <div className="flex items-center gap-2">
                <button
                  onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
                  disabled={currentPage === 1}
                  className="w-8 h-8 flex items-center justify-center rounded-lg border transition-colors"
                  style={{
                    backgroundColor: currentPage === 1 ? '#F8F8F8' : '#FFFFFF',
                    border: '1px solid #E5E5E5',
                    color: currentPage === 1 ? '#C4C4C4' : '#424242'
                  }}
                >
                  ‹
                </button>
                
                <div className="w-8 h-8 flex items-center justify-center rounded-lg"
                     style={{
                       backgroundColor: '#0095FF',
                       color: '#FFFFFF'
                     }}>
                  {currentPage}
                </div>
                
                <button
                  onClick={() => setCurrentPage(prev => Math.min(prev + 1, pagination.totalPages))}
                  disabled={currentPage === pagination.totalPages}
                  className="w-8 h-8 flex items-center justify-center rounded-lg border transition-colors"
                  style={{
                    backgroundColor: currentPage === pagination.totalPages ? '#F8F8F8' : '#FFFFFF',
                    border: '1px solid #E5E5E5',
                    color: currentPage === pagination.totalPages ? '#C4C4C4' : '#424242'
                  }}
                >
                  ›
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ClientSelectionModal;