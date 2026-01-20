import React, { useState, useEffect } from 'react';
import { COLORS, STYLES } from '../constants/theme';
import ProductCard from '../components/ProductCard';
import MenuCalculadora from '../components/MenuCalculadora';
import SalesSelectionModal from '../components/SalesSelectionModal';
import ClientSelectionModal from '../components/ClientSelectionModal';
import PaymentModal from '../components/PaymentModal';
import { fetchArticlesWithParams, fetchSalePoints } from '../services/api';
import bagIcon from '../assets/img/bag.png';
import downloadIcon from '../assets/icons/download.svg';
import downloadArrowIcon from '../assets/icons/downloadarrowicon.png';
import scannerIcon from '../assets/icons/scannericon.png';
import dashboardIcon from '../assets/icons/dashboardtabicon.png';

const POS = () => {
  // Estados principales POS
  const [isNewSaleActive, setIsNewSaleActive] = useState(false);
  const [cartItems, setCartItems] = useState([]);
  const [customerInfo, setCustomerInfo] = useState({ name: '', email: '' });
  const [selectedProducts, setSelectedProducts] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [showCalculator, setShowCalculator] = useState(false);
  const [activeCategory, setActiveCategory] = useState('Todos');
  const [products, setProducts] = useState([]);
  const [availableCategories, setAvailableCategories] = useState(['Todos']);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showSalesModal, setShowSalesModal] = useState(false);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  
  // Estados para paginación
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [totalRecords, setTotalRecords] = useState(0);
  const [perPage] = useState(10);
  
  // Estados para cliente
  const [selectedClient, setSelectedClient] = useState(null);
  const [showClientModal, setShowClientModal] = useState(false);

  // Estados para punto de venta
  const [salePoints, setSalePoints] = useState([]);
  const [selectedSalePoint, setSelectedSalePoint] = useState(null);
  const [loadingSalePoints, setLoadingSalePoints] = useState(true);

  // Cargar productos de la API con paginación
  const loadProducts = async (page = 1, search = '', category = '') => {
    try {
      setLoading(true);
      const response = await fetchArticlesWithParams(page, perPage, search, category);
      
      setProducts(response.articles);
      setCurrentPage(response.pagination.currentPage);
      setTotalPages(response.pagination.totalPages);
      setTotalRecords(response.pagination.totalRecords);
      
      // Extraer categorías de todos los productos (solo en primera carga)
      if (page === 1 && !search && !category) {
        // Para obtener todas las categorías, hacer una llamada separada sin paginación
        try {
          const allProductsResponse = await fetchArticlesWithParams(1, 1000, '', '');
          const categories = ['Todos', ...new Set(allProductsResponse.articles.map(product => product.category))];
          setAvailableCategories(categories);
        } catch (err) {
          console.error('Error loading categories:', err);
        }
      }
      
      setError(null);
    } catch (err) {
      setError('Error cargando productos');
      console.error('Error loading products:', err);
    } finally {
      setLoading(false);
    }
  };

  // Cargar puntos de venta desde la API
  const loadSalePoints = async () => {
    try {
      setLoadingSalePoints(true);
      const salePointsData = await fetchSalePoints();
      setSalePoints(salePointsData);
      
      // Verificar si hay un punto de venta guardado en localStorage
      const savedSalePointId = localStorage.getItem('selectedSalePointId');
      if (savedSalePointId) {
        const savedSalePoint = salePointsData.find(sp => sp.id === parseInt(savedSalePointId));
        if (savedSalePoint) {
          setSelectedSalePoint(savedSalePoint);
        }
      }
    } catch (error) {
      console.error('Error loading sale points:', error);
    } finally {
      setLoadingSalePoints(false);
    }
  };

  // Manejar selección de punto de venta
  const handleSalePointChange = (salePoint) => {
    setSelectedSalePoint(salePoint);
    localStorage.setItem('selectedSalePointId', salePoint.id.toString());
  };

  // Efecto inicial para cargar productos y puntos de venta
  useEffect(() => {
    loadProducts();
    loadSalePoints();
  }, []);

  // Los productos ya vienen filtrados desde la API
  const filteredProducts = products;

  // Lógica carrito - agregar producto
  const handleAddToCart = (product) => {
    setCartItems(prevItems => {
      const existingItem = prevItems.find(item => item.id === product.id);
      
      if (existingItem) {
        return prevItems.map(item =>
          item.id === product.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        );
      } else {
        return [...prevItems, { ...product, quantity: 1 }];
      }
    });
  };

  const handleUpdateQuantity = (productId, newQuantity) => {
    if (newQuantity === 0) {
      handleRemoveItem(productId);
      return;
    }

    setCartItems(prevItems =>
      prevItems.map(item => {
        if (item.id === productId) {
          return { ...item, quantity: newQuantity };
        }
        return item;
      })
    );
  };

  const handleRemoveItem = (productId) => {
    setCartItems(prevItems => prevItems.filter(item => item.id !== productId));
  };

  const handleClearCart = () => {
    setCartItems([]);
  };

  const handleCategoryChange = (category) => {
    setActiveCategory(category);
    setCurrentPage(1); // Reset to page 1 when changing category
    loadProducts(1, searchTerm, category);
  };

  // Funciones de paginación
  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
    loadProducts(newPage, searchTerm, activeCategory);
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
    setCurrentPage(1); // Reset to page 1 when searching
    loadProducts(1, newSearchTerm, activeCategory);
  };

  const handleProcessSale = () => {
    if (cartItems.length === 0) return;
    
    // Validar punto de venta antes de proceder al pago
    if (!selectedSalePoint) {
      alert('Por favor selecciona un punto de venta antes de continuar.');
      return;
    }
    
    // Abrir modal de pago en lugar de procesar directamente
    setShowPaymentModal(true);
  };

  const handleConfirmPayment = () => {
    // Aquí irá la lógica de procesamiento de pago real
    console.log('Procesando venta:', {
      customer: customerInfo,
      items: cartItems,
      total: cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0) * 1.13
    });
    
    // Reset post-venta
    setCartItems([]);
    setCustomerInfo({ name: '', email: '' });
    setShowPaymentModal(false);
    alert('Venta procesada exitosamente');
  };

  const handleStartNewSale = () => {
    setShowSalesModal(true);
  };

  const handleCreateNewSale = () => {
    setShowSalesModal(false);
    setIsNewSaleActive(true);
  };

  const handleSelectSale = (sale) => {
    console.log('Venta seleccionada:', sale);
    setShowSalesModal(false);
    setIsNewSaleActive(true);
    // Aquí puedes cargar los datos de la venta seleccionada
  };

  const handleCloseSalesModal = () => {
    setShowSalesModal(false);
  };

  const handleBackToEmpty = () => {
    setIsNewSaleActive(false);
    setCartItems([]);
    setCustomerInfo({ name: '', email: '' });
    setSelectedClient(null);
    setSearchTerm('');
  };

  // Funciones para manejo de clientes
  const handleSelectClientClick = () => {
    setShowClientModal(true);
  };

  const handleSelectClient = (client) => {
    setSelectedClient(client);
    setShowClientModal(false);
    console.log('Cliente seleccionado:', client);
  };

  const handleCreateNewClient = () => {
    // TODO: Implementar creación de nuevo cliente
    console.log('Crear nuevo cliente');
    setShowClientModal(false);
  };

  const handleCloseClientModal = () => {
    setShowClientModal(false);
  };
  return (
    <div className="pt-28 lg:pt-32">
      {!isNewSaleActive ? (
        // Estado inicial vacío
        <div className={`${STYLES.card} max-w-none w-full`}>
          {/* Header principal */}
          <div className="flex justify-center items-center px-20 lg:px-110 py-6 lg:py-8 border-b border-gray-100" 
               style={{ backgroundColor: COLORS.cardBackground }}>
            <div className="flex-1"></div>
            <h2 className="text-2xl sm:text-3xl font-semibold" style={{ color: COLORS.black }}>
              Punto de venta (POS)
            </h2>
            <div className="flex-1 flex justify-end">
              <button
                className={`${STYLES.button} text-gray-600 border border-gray-300`}
                style={{ backgroundColor: COLORS.lightBlue }}
              >
              </button>
            </div>
          </div>

          {/* Estado vacío */}
          <div className="flex flex-col items-center justify-center min-h-[500px] px-8 py-16 lg:py-24">
            <div className="mb-12">
              <img 
                src={bagIcon} 
                alt="Bolsa de compras" 
                className="w-32 h-32 lg:w-40 lg:h-40 mx-auto object-contain"
              />
            </div>

            <h3 className="text-2xl lg:text-3xl font-semibold mb-6 text-center" 
                style={{ color: COLORS.primary }}>
              Sin pedidos por el momento.
            </h3>

            <p className="text-gray-500 mb-12 text-center text-lg lg:text-xl max-w-md">
              ¿Quieres realizar uno ahora?
            </p>

            <button
              onClick={handleStartNewSale}
              className="px-8 py-4 rounded-lg font-medium hover:opacity-90 transition touch-manipulation min-h-[48px] flex items-center gap-3 text-white text-lg"
              style={{ backgroundColor: COLORS.primary }}
            >
              <span className="text-xl font-bold">+</span>
              Nueva venta
            </button>
          </div>
        </div>
      ) : (
        // Vista del POS activo
        <div className="flex h-screen pt-0 gap-6 ml-4">
          {/* Panel izquierdo - Productos */}
          <div className="flex-1 flex flex-col" style={{ backgroundColor: '#FFFFFF' }}>
            {/* Header del POS */}
            <div className="bg-white border-b border-gray-200">
              {/* Barra superior con fondo azul */}
              <div className="px-6 py-4" style={{ backgroundColor: '#ECF6FF' }}>
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-4">
                    <h1 className="text-xl font-semibold" style={{ color: '#0095FF' }}>
                      Punto de Venta (POS)
                    </h1>
                  </div>

                  <div className="flex items-center gap-3">
                    {/* Dropdown para selección de punto de venta */}
                    <div className="relative">
                      <select
                        value={selectedSalePoint?.id || ''}
                        onChange={(e) => {
                          const salePoint = salePoints.find(sp => sp.id === parseInt(e.target.value));
                          if (salePoint) {
                            handleSalePointChange(salePoint);
                          }
                        }}
                        className="appearance-none bg-white border border-gray-300 rounded-lg px-4 py-2 pr-8 text-sm font-medium focus:outline-none focus:border-blue-500 transition-colors"
                        style={{
                          minWidth: '200px',
                          color: selectedSalePoint ? '#374151' : '#9CA3AF'
                        }}
                        disabled={loadingSalePoints || salePoints.length === 0}
                      >
                        <option value="" disabled>
                          {loadingSalePoints ? 'Cargando...' : 'Seleccionar punto de venta'}
                        </option>
                        {salePoints.map((salePoint) => (
                          <option key={salePoint.id} value={salePoint.id}>
                            {salePoint.name}
                          </option>
                        ))}
                      </select>
                      
                      {/* Icono de dropdown */}
                      <div className="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                        <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                        </svg>
                      </div>
                    </div>
                    <button className="flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg transition-colors"
                            style={{ 
                              backgroundColor: '#F7FAFF',
                              color: '#7B91B0'
                            }}>
                      <img src={scannerIcon} alt="Scanner" className="w-4 h-4" style={{ filter: 'brightness(0) saturate(100%) invert(48%) sepia(14%) saturate(623%) hue-rotate(189deg) brightness(93%) contrast(86%)' }} />
                      <span className="text-sm font-medium">Scanear artículo</span>
                    </button>
                    
                    <button className="flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg transition-colors"
                            style={{ 
                              backgroundColor: '#F7FAFF',
                              color: '#7B91B0'
                            }}>
                      <img src={dashboardIcon} alt="Dashboard" className="w-4 h-4" style={{ filter: 'brightness(0) saturate(100%) invert(48%) sepia(14%) saturate(623%) hue-rotate(189deg) brightness(93%) contrast(86%)' }} />
                      <span className="text-sm font-medium">Dashboard</span>
                    </button>

                    {/* Botón con icono de descarga */}
                    <button className="flex items-center gap-1 p-2 rounded-lg transition-colors"
                            style={{ 
                              backgroundColor: '#F7FAFF',
                              border: '1.28px solid #1890FF17'
                            }}>
                      <img src={downloadIcon} alt="Download" className="w-5 h-5" style={{ filter: 'brightness(0) saturate(100%) invert(35%) sepia(100%) saturate(3532%) hue-rotate(202deg) brightness(96%) contrast(101%)' }} />
                      <img src={downloadArrowIcon} alt="Download Arrow" className="w-4 h-4" style={{ filter: 'brightness(0) saturate(100%) invert(35%) sepia(100%) saturate(3532%) hue-rotate(202deg) brightness(96%) contrast(101%)' }} />
                    </button>

                    {/* Botón con símbolo + */}
                    <button className="p-2 rounded-lg transition-colors"
                            style={{ 
                              backgroundColor: '#F7FAFF',
                              border: '1.28px solid #1890FF17'
                            }}>
                      <svg className="w-5 h-5" style={{ color: '#0095FF' }} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>

              {/* Sección de búsqueda y tabs */}
              <div className="px-6 py-4 bg-white">
                <div className="flex items-center gap-4">
                  {/* Barra de búsqueda */}
                  <div className="relative max-w-md">
                    <input
                      type="text"
                      placeholder="Buscar artículo"
                      value={searchTerm}
                      onChange={(e) => setSearchTerm(e.target.value)}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          handleSearch(e.target.value);
                        }
                      }}
                      onBlur={(e) => handleSearch(e.target.value)}
                      className="w-full px-4 py-2 pl-10 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                    <svg className="w-5 h-5 text-gray-400 absolute left-3 top-2.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                            d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                    </svg>
                  </div>

                  {/* Tabs de categorías */}
                  <div className="flex gap-2 overflow-x-auto flex-1">
                    {availableCategories.map((category) => (
                      <button
                        key={category}
                        onClick={() => handleCategoryChange(category)}
                        className={`px-4 py-2 text-sm font-medium whitespace-nowrap transition-all ${
                          activeCategory === category 
                            ? 'text-white shadow-sm' 
                            : 'text-opacity-50 hover:bg-gray-200'
                        }`}
                        style={activeCategory === category 
                          ? { 
                              backgroundColor: '#1890FF17',
                              border: '0.95px solid #0095FF',
                              borderRadius: '20px',
                              color: '#0095FF',
                              outline: 'none'
                            } 
                          : { 
                              backgroundColor: '#FFFFFF', 
                              color: '#42424280',
                              border: '0.95px solid #E4E4E4',
                              borderRadius: '20px',
                              outline: 'none'
                            }
                        }
                      >
                        {category}
                      </button>
                    ))}
                  </div>
                </div>
              </div>
            </div>

            {/* Grid de productos */}
            <div className="flex-1 p-4 overflow-y-auto">
              {loading ? (
                <div className="flex flex-col items-center justify-center h-64 text-gray-500">
                  <div className="animate-spin rounded-full h-16 w-16 border-b-2" style={{ borderColor: COLORS.primary }} />
                  <div className="mt-4 text-center">
                    <p className="text-lg font-medium">Cargando productos...</p>
                    <p className="text-sm text-gray-400 mt-1">Conectando con API externa de Decima ERP</p>
                    <div className="flex items-center justify-center mt-3 gap-2">
                      <div className="w-2 h-2 bg-blue-500 rounded-full animate-bounce" style={{ animationDelay: '0ms' }}></div>
                      <div className="w-2 h-2 bg-blue-500 rounded-full animate-bounce" style={{ animationDelay: '150ms' }}></div>
                      <div className="w-2 h-2 bg-blue-500 rounded-full animate-bounce" style={{ animationDelay: '300ms' }}></div>
                    </div>
                  </div>
                </div>
              ) : error ? (
                <div className="flex flex-col items-center justify-center h-64 text-red-500">
                  <svg className="w-16 h-16 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                          d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L4.35 16.5c-.77.833.192 2.5 1.732 2.5z" />
                  </svg>
                  <div className="text-center">
                    <p className="text-lg font-medium">{error}</p>
                    <p className="text-sm text-red-400 mt-1">Error conectando con API de Decima ERP</p>
                  </div>
                  <button 
                    onClick={() => window.location.reload()} 
                    className="mt-4 px-6 py-3 rounded-lg font-medium hover:opacity-90 transition text-white"
                    style={{ backgroundColor: COLORS.primary }}
                  >
                    Reintentar conexión
                  </button>
                </div>
              ) : (
                <>
                  <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
                    {filteredProducts.map((product) => (
                      <ProductCard
                        key={product.id}
                        product={product}
                        onAddToCart={handleAddToCart}
                      />
                    ))}
                  </div>

                  {!loading && filteredProducts.length === 0 && products.length > 0 && (
                    <div className="flex flex-col items-center justify-center h-64 text-gray-500">
                      <svg className="w-16 h-16 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                      </svg>
                      <p>No se encontraron productos que coincidan con la búsqueda</p>
                    </div>
                  )}

                  {!loading && products.length === 0 && !error && (
                    <div className="flex flex-col items-center justify-center h-64 text-gray-500">
                      <svg className="w-16 h-16 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                              d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
                      </svg>
                      <p>No hay productos disponibles</p>
                    </div>
                  )}

                  {/* Controles de paginación */}
                  {!loading && totalPages > 1 && (
                    <div className="flex flex-col sm:flex-row justify-between items-center mt-6 pt-4 border-t border-gray-200 gap-4">
                      {/* Información de paginación */}
                      <div className="text-sm text-gray-600 text-center sm:text-left">
                        Página {currentPage} de {totalPages} - Mostrando {filteredProducts.length} de {totalRecords} productos
                      </div>

                      {/* Controles de navegación */}
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
                              e.target.style.borderColor = '#9CA3AF';
                            }
                          }}
                          onMouseLeave={(e) => {
                            if (currentPage !== 1) {
                              e.target.style.backgroundColor = '#FFFFFF';
                              e.target.style.borderColor = '#D1D5DB';
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
                              e.target.style.borderColor = '#9CA3AF';
                            }
                          }}
                          onMouseLeave={(e) => {
                            if (currentPage !== totalPages) {
                              e.target.style.backgroundColor = '#FFFFFF';
                              e.target.style.borderColor = '#D1D5DB';
                            }
                          }}
                        >
                          Siguiente →
                        </button>
                      </div>
                    </div>
                  )}
                </>
              )}
            </div>
          </div>

          {/* Panel derecho - Calculadora/Carrito */}
          <div className="w-80">
            <MenuCalculadora
              cartItems={cartItems}
              onUpdateQuantity={handleUpdateQuantity}
              onRemoveItem={handleRemoveItem}
              onClearCart={handleClearCart}
              onProcessSale={handleProcessSale}
              customerInfo={customerInfo}
              onCustomerInfoChange={setCustomerInfo}
              selectedClient={selectedClient}
              onSelectClientClick={handleSelectClientClick}
            />
          </div>
        </div>
      )}

      {/* Modal de Selección de Ventas */}
      <SalesSelectionModal
        isOpen={showSalesModal}
        onClose={handleCloseSalesModal}
        onSelectSale={handleSelectSale}
        onCreateNew={handleCreateNewSale}
      />

      {/* Modal de Selección de Clientes */}
      <ClientSelectionModal
        isOpen={showClientModal}
        onClose={handleCloseClientModal}
        onSelectClient={handleSelectClient}
        onCreateNew={handleCreateNewClient}
      />

      {/* Modal de Pago */}
      <PaymentModal
        isOpen={showPaymentModal}
        onClose={() => setShowPaymentModal(false)}
        cartItems={cartItems}
        orderNumber={`POS-${Date.now().toString().slice(-6)}`}
        total={cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0)}
        ivaAmount={cartItems.reduce((sum, item) => {
          const totalWithVat = item.price * item.quantity;
          const totalWithoutVat = totalWithVat / 1.13;
          return sum + (totalWithVat - totalWithoutVat);
        }, 0)}
        onConfirmPayment={handleConfirmPayment}
        selectedClient={selectedClient}
        selectedSalePoint={selectedSalePoint}
      />
    </div>
  );
};

export default POS;