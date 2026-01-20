// API service for fetching articles using Axios
import axios from 'axios';

// Configuración específica: Login usa backend del droplet, resto usa Decima
const LOGIN_API_BASE_URL = 'http://104.248.215.77:8080/api';
const DECIMA_API_BASE_URL = 'http://api-decima-url/api'; // Para otros catálogos

console.log('Login API URL:', LOGIN_API_BASE_URL);

// Create axios instance with default configuration para LOGIN
const apiClient = axios.create({
  baseURL: LOGIN_API_BASE_URL,
  timeout: 10000, // 10 seconds timeout
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Add request interceptor for authentication
apiClient.interceptors.request.use(
  (config) => {
    console.log(`Making ${config.method?.toUpperCase()} request to: ${config.url}`);
    
    // Add auth token if available (for authenticated endpoints)
    const token = localStorage.getItem('token');
    console.log('Token found in localStorage:', token ? 'Yes' : 'No');
    console.log('Requires auth:', config.requiresAuth);
    
    if (token && config.requiresAuth) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('Auth header added:', config.headers.Authorization);
    }
    
    return config;
  },
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('Response error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

// Fetch articles from Decima ERP API with pagination and search
export const fetchArticlesWithParams = async (page = 1, perPage = 50, search = '', category = '') => {
  try {
    const params = {
      pager: true,
      rows: perPage,
      page: page,
      withMasterData: true,
      withOperationData: true,
      orderBy: JSON.stringify([{'field':'id','sort':'desc'}])
    };
    
    // Add search parameter if provided
    if (search && search.trim() !== '') {
      params.search = search.trim();
    }
    
    // Add category filter if provided
    if (category && category.trim() !== '' && category !== 'Todos') {
      params.category = category.trim();
    }

    const response = await axios.get('https://erp-api.decima.la/api/catalogs/article', {
      headers: {
        'Authorization': 'Bearer 38655|LilZOOhaimGAk55Mclyu9xlAkwd2hCyJlF4k2evf',
        'Platform-Id': '4',
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      params: params
    });
    
    // Transform external API data to match our POS component structure
    const articles = response.data.data.map(item => {
      const price = item.attributes.master_retail_price || item.attributes.master_final_price || 0;
      
      console.log('PRICE DEBUG:', {
        name: item.attributes.name,
        master_retail_price: item.attributes.master_retail_price,
        master_final_price: item.attributes.master_final_price,
        mapped_price: price
      });
      
      return {
        id: item.attributes.id,
        name: item.attributes.name || 'Artículo sin nombre',
        price: price,
      stock: item.attributes.master_available_balance || 0,
      image: item.attributes.image_url || null,
      category: item.attributes.category_name || 'General',
      brand: item.attributes.brand_name || 'Sin marca',
      sku: item.attributes.internal_reference || '',
      description: item.attributes.description || '',
      // Campos adicionales de la API externa
      barcode: item.attributes.barcode,
      unit_measure: item.attributes.unit_measure_name,
      unit_symbol: item.attributes.unit_measure_symbol,
      discount: item.attributes.master_discount || 0,
      offer_price: item.attributes.master_offer_price || 0,
      retail_price: item.attributes.master_retail_price || 0,
      balance: item.attributes.master_balance || 0,
      reserved: item.attributes.master_reserved || 0,
      is_active: item.attributes.is_active,
      is_scannable: item.attributes.is_scannable,
      category_class: item.attributes.category_class,
      category_class_label: item.attributes.category_class_label,
      guarantee: item.attributes.guarantee,
      dimensions: item.attributes.dimensions
    };
    });
    
    console.log(`Artículos cargados desde API externa Decima (página ${page}):`, articles.length);
    console.log('Primeros 3 artículos transformados:', articles.slice(0, 3));
    console.log('Parámetros de búsqueda:', params);
    
    return {
      articles: articles,
      meta: response.data.meta,
      pagination: {
        currentPage: response.data.meta?.page || 1,
        totalPages: Math.ceil((response.data.meta?.records || 0) / perPage),
        totalRecords: response.data.meta?.records || 0,
        perPage: perPage,
        from: response.data.meta?.from || 0,
        to: response.data.meta?.to || 0
      }
    };
  } catch (error) {
    console.error('Error fetching articles with params from external API:', error);
    console.error('Error details:', {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data
    });
    
    return {
      articles: [],
      meta: null,
      pagination: {
        currentPage: 1,
        totalPages: 0,
        totalRecords: 0,
        perPage: perPage,
        from: 0,
        to: 0
      }
    };
  }
};

// TEMPORARY DEBUG FUNCTION - Remove after fixing
export const debugFetchArticles = async () => {
  try {
    console.log('🐛 DEBUG: Starting direct API call...');
    
    // Try different parameter combinations to get pricing data
    const testUrls = [
      'https://erp-api.decima.la/api/catalogs/article?per_page=10&page=1',
      'https://erp-api.decima.la/api/catalogs/article?per_page=10&include=pricing',
      'https://erp-api.decima.la/api/catalogs/article?per_page=10&with=master_pricing'
    ];
    
    for (let i = 0; i < testUrls.length; i++) {
      console.log(`🐛 DEBUG: Testing URL ${i + 1}: ${testUrls[i]}`);
      
      try {
        const response = await axios.get(testUrls[i], {
          headers: {
            'Authorization': 'Bearer 38655|LilZOOhaimGAk55Mclyu9xlAkwd2hCyJlF4k2evf',
            'Platform-Id': '4',
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          }
        });
        
        if (response.data.data && response.data.data.length > 0) {
          const firstItem = response.data.data[0];
          console.log(`🐛 DEBUG: Result ${i + 1} - First item attributes keys:`, Object.keys(firstItem.attributes));
          
          const hasPricing = firstItem.attributes.master_retail_price !== undefined || 
                           firstItem.attributes.master_final_price !== undefined;
          
          if (hasPricing) {
            console.log(`🎉 FOUND PRICING in URL ${i + 1}!`, {
              master_retail_price: firstItem.attributes.master_retail_price,
              master_final_price: firstItem.attributes.master_final_price,
              master_offer_price: firstItem.attributes.master_offer_price
            });
            return response.data.data;
          }
        }
      } catch (error) {
        console.log(`🐛 DEBUG: Error with URL ${i + 1}:`, error.response?.status);
      }
    }
    
    console.log('🐛 DEBUG: No pricing data found in any URL');
    return [];
  } catch (error) {
    console.error('🐛 DEBUG: Error in direct API call:', error);
    throw error;
  }
};

// Fetch articles from Decima ERP API
export const fetchArticles = async () => {
  try {
    console.log('🔥 DETAILED API CALL DEBUG');
    
    const url = 'https://erp-api.decima.la/api/catalogs/article';
    const headers = {
      'Authorization': 'Bearer 38655|LilZOOhaimGAk55Mclyu9xlAkwd2hCyJlF4k2evf',
      'Platform-Id': '4',
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    };
    const params = {
      pager: true,
      rows: 10,
      page: 1,
      withMasterData: true,
      withOperationData: true,
      orderBy: JSON.stringify([{'field':'id','sort':'desc'}])
    };
    
    console.log('🔥 URL:', url);
    console.log('🔥 Headers:', headers);
    console.log('🔥 Params:', params);
    
    // Using Decima ERP external API
    const response = await axios.get(url, {
      headers: headers,
      params: params
    });
    
    console.log('🔥 Response Status:', response.status);
    console.log('🔥 Response Data Meta:', response.data.meta);
    console.log('🔥 Response Data Length:', response.data.data.length);
    console.log('🔥 FIRST ITEM FULL:', response.data.data[0]);
    console.log('🔥 FIRST ITEM HAS PRICING?', {
      has_master_retail_price: 'master_retail_price' in response.data.data[0].attributes,
      has_master_final_price: 'master_final_price' in response.data.data[0].attributes,
      master_retail_price_value: response.data.data[0].attributes.master_retail_price,
      master_final_price_value: response.data.data[0].attributes.master_final_price
    });
    
    // Transform external API data to match our POS component structure
    const articles = response.data.data.map(item => {
      console.log('SIMPLE FETCH - RAW ITEM:', item);
      
      const retailPrice = item.attributes.master_retail_price;
      const finalPrice = item.attributes.master_final_price;
      const price = retailPrice || finalPrice || 0;
      
      console.log('SIMPLE FETCH - DETAILED PRICE MAPPING:', {
        name: item.attributes.name,
        retailPrice: retailPrice,
        finalPrice: finalPrice,
        selectedPrice: price,
        retailType: typeof retailPrice,
        finalType: typeof finalPrice
      });
      
      return {
        id: item.attributes.id,
        name: item.attributes.name || 'Artículo sin nombre',
        price: item.attributes.master_retail_price || item.attributes.master_final_price || 0,
        stock: item.attributes.master_available_balance || 0,
      image: item.attributes.image_url || null,
      category: item.attributes.category_name || 'General',
      brand: item.attributes.brand_name || 'Sin marca',
      sku: item.attributes.internal_reference || '',
      description: item.attributes.description || '',
      // Campos adicionales de la API externa
      barcode: item.attributes.barcode,
      unit_measure: item.attributes.unit_measure_name,
      unit_symbol: item.attributes.unit_measure_symbol,
      discount: item.attributes.master_discount || 0,
      offer_price: item.attributes.master_offer_price || 0,
      retail_price: item.attributes.master_retail_price || 0,
      balance: item.attributes.master_balance || 0,
      reserved: item.attributes.master_reserved || 0,
      is_active: item.attributes.is_active,
      is_scannable: item.attributes.is_scannable,
      category_class: item.attributes.category_class,
      category_class_label: item.attributes.category_class_label,
      guarantee: item.attributes.guarantee,
      dimensions: item.attributes.dimensions
    };
    });
    
    console.log('Artículos cargados desde API externa Decima:', articles.length);
    console.log('Estructura de respuesta API externa:', {
      meta: response.data.meta,
      totalRecords: response.data.meta?.records,
      currentPage: response.data.meta?.page
    });
    
    return articles;
  } catch (error) {
    console.error('Error fetching articles from external API:', error);
    console.error('Error details:', {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data
    });
    
    // Return empty array if external API fails
    return [];
  }
};

// Create payment term (requires authentication)
export const createPaymentTerm = async (paymentTermData) => {
  try {
    const response = await apiClient.post('/payment-terms', paymentTermData, {
      requiresAuth: true
    });
    
    console.log('Condición de pago creada:', response.data);
    return response.data;
  } catch (error) {
    console.error('Error creating payment term:', error);
    throw error;
  }
};

// Create payment form (requires authentication)
export const createPaymentForm = async (paymentFormData) => {
  try {
    const response = await apiClient.post('/payment-forms', paymentFormData, {
      requiresAuth: true
    });
    
    console.log('Forma de pago creada:', response.data);
    return response.data;
  } catch (error) {
    console.error('Error creating payment form:', error);
    throw error;
  }
};

// Fetch sales from external Decima ERP API
export const fetchSales = async () => {
  try {
    const response = await axios.get('https://erp-api.decima.la/api/income/sale', {
      headers: {
        'Authorization': 'Bearer 38655|LilZOOhaimGAk55Mclyu9xlAkwd2hCyJlF4k2evf',
        'Platform-Id': '4',
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      params: {
        pager: true,
        rows: 15,
        returnPlainData: true,
        indexed: true,
        orderBy: JSON.stringify([{"field":"id","sort":"desc"}])
      }
    });
    
    console.log('Respuesta completa de la API de ventas:', response.data);
    
    // Check if response has the expected structure
    if (!response.data || !response.data.data || !Array.isArray(response.data.data)) {
      console.warn('Estructura de respuesta inesperada:', response.data);
      return [];
    }
    
    // Transform external API data to match our sales table structure
    const sales = response.data.data.map(item => {
      console.log('Procesando venta:', item);
      return {
        id: item.attributes?.id || item.id,
        registration_date: item.attributes?.registration_date || item.attributes?.created_at || new Date().toISOString().split('T')[0],
        sale_order_number: item.attributes?.sale_order_number || item.attributes?.correlative_number || item.attributes?.document_number || item.attributes?.id || 'N/A',
        emission_date: item.attributes?.emission_date || item.attributes?.created_at || new Date().toISOString().split('T')[0],
        client_id: item.attributes?.client_id || 0,
        client_name: item.attributes?.client_name || `Cliente ${item.attributes?.client_id || 'Sin ID'}`,
        payment_condition: item.attributes?.payment_term_name || item.attributes?.payment_condition || 'Término 1',
        description: item.attributes?.description || item.attributes?.comment || item.attributes?.remark || 'Venta de consultoría y productos',
        document_type: item.attributes?.document_type_name || 'Tipo 1',
        document_number: item.attributes?.document_number || item.attributes?.correlative_number || item.attributes?.id || 'N/A',
        payment_date: item.attributes?.payment_date || 'N/A',
        collection_date: item.attributes?.collection_date || 'N/A',
        status: item.attributes?.status_name || 'Emitida',
        total: item.attributes?.total || item.attributes?.sales_total || item.attributes?.grand_total || 0,
        // Datos adicionales de la API externa
        subject_amount_sum: item.attributes?.subject_amount_sum || 0,
        exempt_amount_sum: item.attributes?.exempt_amount_sum || 0,
        collected_tax_amount_sum: item.attributes?.collected_tax_amount_sum || 0,
        payment_term_id: item.attributes?.payment_term_id || 1,
        payment_form_id: item.attributes?.payment_form_id || 1,
        // Datos originales para referencia
        original: item.attributes
      };
    });
    
    console.log('Ventas transformadas:', sales);
    console.log('Ventas cargadas desde API externa:', sales.length);
    return sales;
  } catch (error) {
    console.error('Error fetching sales from external API:', error);
    console.error('Response:', error.response?.data);
    console.error('Status:', error.response?.status);
    // Return empty array if API fails
    return [];
  }
};

// Fetch sales from external Decima ERP API with pagination
export const fetchSalesWithParams = async (page = 1, perPage = 10, search = '') => {
  try {
    const params = {
      pager: true,
      rows: perPage,
      page: page,
      returnPlainData: true,
      indexed: true,
      orderBy: JSON.stringify([{"field":"id","sort":"desc"}])
    };
    
    // Add search parameter if provided
    if (search && search.trim() !== '') {
      params.search = search.trim();
    }

    const response = await axios.get('https://erp-api.decima.la/api/income/sale', {
      headers: {
        'Authorization': 'Bearer 38655|LilZOOhaimGAk55Mclyu9xlAkwd2hCyJlF4k2evf',
        'Platform-Id': '4',
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      params: params
    });
    
    console.log(`Respuesta de API de ventas (página ${page}):`, response.data);
    
    // Check if response has the expected structure
    if (!response.data || !response.data.data || !Array.isArray(response.data.data)) {
      console.warn('Estructura de respuesta inesperada:', response.data);
      return {
        sales: [],
        pagination: {
          currentPage: 1,
          totalPages: 0,
          totalRecords: 0,
          perPage: perPage
        }
      };
    }
    
    // Transform external API data to match our sales table structure
    const sales = response.data.data.map(item => {
      return {
        id: item.attributes?.id || item.id,
        registration_date: item.attributes?.registration_date || item.attributes?.created_at || new Date().toISOString().split('T')[0],
        sale_order_number: item.attributes?.sale_order_number || item.attributes?.correlative_number || item.attributes?.document_number || item.attributes?.id || 'N/A',
        emission_date: item.attributes?.emission_date || item.attributes?.created_at || new Date().toISOString().split('T')[0],
        client_id: item.attributes?.client_id || 0,
        client_name: item.attributes?.client_name || `Cliente ${item.attributes?.client_id || 'Sin ID'}`,
        payment_condition: item.attributes?.payment_term_name || item.attributes?.payment_condition || 'Término 1',
        description: item.attributes?.description || item.attributes?.comment || item.attributes?.remark || 'Venta de consultoría y productos',
        document_type: item.attributes?.document_type_name || 'Tipo 1',
        document_number: item.attributes?.document_number || item.attributes?.correlative_number || item.attributes?.id || 'N/A',
        payment_date: item.attributes?.payment_date || 'Invalid Date',
        collection_date: item.attributes?.collection_date || 'Invalid Date',
        status: item.attributes?.status_name || 'Emitida',
        total: item.attributes?.total || item.attributes?.sales_total || item.attributes?.grand_total || 0,
        // Datos adicionales de la API externa
        subject_amount_sum: item.attributes?.subject_amount_sum || 0,
        exempt_amount_sum: item.attributes?.exempt_amount_sum || 0,
        collected_tax_amount_sum: item.attributes?.collected_tax_amount_sum || 0,
        payment_term_id: item.attributes?.payment_term_id || 1,
        payment_form_id: item.attributes?.payment_form_id || 1,
        // Datos originales para referencia
        original: item.attributes
      };
    });
    
    console.log(`Ventas cargadas desde API externa Decima (página ${page}):`, sales.length);
    console.log('Primeras 3 ventas transformadas:', sales.slice(0, 3));
    
    return {
      sales: sales,
      meta: response.data.meta,
      pagination: {
        currentPage: response.data.meta?.page || 1,
        totalPages: Math.ceil((response.data.meta?.records || 0) / perPage),
        totalRecords: response.data.meta?.records || 0,
        perPage: perPage,
        from: response.data.meta?.from || 0,
        to: response.data.meta?.to || 0
      }
    };
  } catch (error) {
    console.error('Error fetching sales with params from external API:', error);
    console.error('Error details:', {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data
    });
    
    return {
      sales: [],
      meta: null,
      pagination: {
        currentPage: 1,
        totalPages: 0,
        totalRecords: 0,
        perPage: perPage,
        from: 0,
        to: 0
      }
    };
  }
};

// Fetch clients from API (public endpoint)
export const fetchClients = async (organizationId = 1) => {
  try {
    const response = await apiClient.get(`/clients/organization/${organizationId}`, {
      requiresAuth: true // Esta consulta requiere autenticación
    });
    
    console.log('Clientes cargados desde API real:', response.data.length);
    return response.data;
  } catch (error) {
    console.error('Error fetching clients:', error);
    return [];
  }
};

// Fetch document types from external Decima ERP API
export const fetchDocumentTypes = async () => {
  try {
    const response = await axios.get('https://erp-api.decima.la/api/catalogs/document-type', {
      headers: {
        'Authorization': 'Bearer 38655|LilZOOhaimGAk55Mclyu9xlAkwd2hCyJlF4k2evf',
        'Platform-Id': '4',
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });
    
    console.log('Respuesta de API de tipos de documento:', response.data);
    
    // Check if response has the expected structure
    if (!response.data || !response.data.data || !Array.isArray(response.data.data)) {
      console.warn('Estructura de respuesta inesperada:', response.data);
      return [];
    }
    
    // Transform external API data to match our component structure
    const documentTypes = response.data.data.map(item => ({
      value: item.attributes.id,
      label: item.attributes.name,
      id: item.attributes.id,
      name: item.attributes.name,
      code: item.attributes.code,
      is_electronic_invoice: item.attributes.is_electronic_invoice,
      // Original data for reference
      original: item.attributes
    }));
    
    console.log('Tipos de documento cargados desde API externa:', documentTypes.length);
    console.log('Primeros 3 tipos transformados:', documentTypes.slice(0, 3));
    
    return documentTypes;
  } catch (error) {
    console.error('Error fetching document types from external API:', error);
    console.error('Error details:', {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data
    });
    
    // Return default document types if API fails
    return [
      { value: 1, label: 'Factura de Consumidor Final', id: 1, name: 'Factura de Consumidor Final' },
      { value: 2, label: 'Comprobante de Crédito Fiscal (CCF)', id: 2, name: 'Comprobante de Crédito Fiscal (CCF)' },
      { value: 3, label: 'Nota de Crédito', id: 3, name: 'Nota de Crédito' },
      { value: 4, label: 'Nota de Débito', id: 4, name: 'Nota de Débito' }
    ];
  }
};

// Fetch payment terms from external Decima ERP API
export const fetchPaymentTerms = async () => {
  try {
    const response = await axios.get('https://erp-api.decima.la/api/catalogs/payment-term', {
      headers: {
        'Authorization': 'Bearer 38655|LilZOOhaimGAk55Mclyu9xlAkwd2hCyJlF4k2evf',
        'Platform-Id': '4',
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });
    
    console.log('Respuesta de API de términos de pago:', response.data);
    
    // Check if response has the expected structure
    if (!response.data || !response.data.data || !Array.isArray(response.data.data)) {
      console.warn('Estructura de respuesta inesperada para payment terms:', response.data);
      return [];
    }
    
    // Transform external API data to match our component structure
    const paymentTerms = response.data.data.map(item => ({
      id: item.attributes.id,
      name: item.attributes.name,
      label: item.attributes.label || item.attributes.name,
      paymentPeriod: item.attributes.payment_period,
      value: item.attributes.value,
      // Display format with days
      displayText: item.attributes.payment_period === 0 
        ? item.attributes.name 
        : `${item.attributes.name}`,
      // Original data for reference
      original: item.attributes
    }));
    
    console.log('Términos de pago cargados desde API externa:', paymentTerms.length);
    console.log('Primeros 3 términos transformados:', paymentTerms.slice(0, 3));
    
    return paymentTerms;
  } catch (error) {
    console.error('Error fetching payment terms from external API:', error);
    console.error('Error details:', {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data
    });
    
    // Return default payment terms if API fails
    return [
      { id: 1, name: 'Contado', label: 'Contado', paymentPeriod: 0, value: 1, displayText: 'Contado' },
      { id: 2, name: 'Crédito 7 días', label: 'Crédito 7 días', paymentPeriod: 7, value: 2, displayText: 'Crédito 7 días' },
      { id: 3, name: 'Crédito 15 días', label: 'Crédito 15 días', paymentPeriod: 15, value: 3, displayText: 'Crédito 15 días' },
      { id: 4, name: 'Crédito 30 días', label: 'Crédito 30 días', paymentPeriod: 30, value: 4, displayText: 'Crédito 30 días' }
    ];
  }
};

// Fetch sale points from external Decima ERP API
export const fetchSalePoints = async () => {
  try {
    const response = await axios.get('https://erp-api.decima.la/api/catalogs/sale-point', {
      headers: {
        'Authorization': 'Bearer 38655|LilZOOhaimGAk55Mclyu9xlAkwd2hCyJlF4k2evf',
        'Platform-Id': '4',
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });
    
    console.log('Respuesta de API de puntos de venta:', response.data);
    
    // Check if response has the expected structure
    if (!response.data || !response.data.data || !Array.isArray(response.data.data)) {
      console.warn('Estructura de respuesta inesperada para sale points:', response.data);
      return [];
    }
    
    // Transform external API data to match our component structure
    const salePoints = response.data.data.map(item => ({
      id: item.attributes.id,
      name: item.attributes.name,
      value: item.attributes.value,
      street: item.attributes.street,
      controlCode: item.attributes.control_code,
      // Original data for reference
      original: item.attributes
    }));
    
    console.log('Puntos de venta cargados desde API externa:', salePoints.length);
    console.log('Primeros puntos transformados:', salePoints.slice(0, 2));
    
    return salePoints;
  } catch (error) {
    console.error('Error fetching sale points from external API:', error);
    console.error('Error details:', {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data
    });
    
    // Return default sale points if API fails
    return [
      { id: 1, name: 'Oficina central', value: 1, street: 'Calle Siemens #10', controlCode: 'S001P001' },
      { id: 3, name: 'Sucursal Santa Elena', value: 3, street: 'Blvd. Santa Elena #10', controlCode: 'M001P001' }
    ];
  }
};

// Fetch payment forms from external Decima ERP API
export const fetchPaymentForms = async () => {
  try {
    const response = await axios.get('https://erp-api.decima.la/api/catalogs/payment-form', {
      headers: {
        'Authorization': 'Bearer 38655|LilZOOhaimGAk55Mclyu9xlAkwd2hCyJlF4k2evf',
        'Platform-Id': '4',
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });
    
    console.log('Respuesta de API de formas de pago:', response.data);
    
    // Check if response has the expected structure
    if (!response.data || !response.data.data || !Array.isArray(response.data.data)) {
      console.warn('Estructura de respuesta inesperada:', response.data);
      return [];
    }
    
    // Transform external API data to match our component structure
    const paymentForms = response.data.data
      .filter(item => item.attributes.is_visible_on_sales === 1) // Only show forms visible on sales
      .map(item => ({
        id: item.attributes.id,
        name: item.attributes.name,
        value: item.attributes.id,
        label: item.attributes.name,
        bank_account_is_requested: item.attributes.bank_account_is_requested,
        is_cash: item.attributes.is_cash,
        // Original data for reference
        original: item.attributes
      }));
    
    console.log('Formas de pago cargadas desde API externa:', paymentForms.length);
    console.log('Primeras 3 formas transformadas:', paymentForms.slice(0, 3));
    
    return paymentForms;
  } catch (error) {
    console.error('Error fetching payment forms from external API:', error);
    console.error('Error details:', {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data
    });
    
    // Return default payment forms if API fails
    return [
      { id: 1, name: 'Efectivo', value: 1, label: 'Efectivo' },
      { id: 2, name: 'Transferencia bancaria', value: 2, label: 'Transferencia bancaria' },
      { id: 4, name: 'Cheque recepción', value: 4, label: 'Cheque recepción' }
    ];
  }
};

// Fetch clients from external Decima ERP API
export const fetchClientsFromExternalAPI = async (page = 1, perPage = 50, search = '') => {
  try {
    const params = {
      page: page,
      per_page: perPage
    };
    
    // Add search parameter if provided
    if (search && search.trim() !== '') {
      params.search = search.trim();
    }

    const response = await axios.get('https://erp-api.decima.la/api/catalogs/client', {
      headers: {
        'Authorization': 'Bearer 38655|LilZOOhaimGAk55Mclyu9xlAkwd2hCyJlF4k2evf',
        'Platform-Id': '4',
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      params: params
    });

    // Transform external API data to match our client structure
    const clients = response.data.data.map(item => ({
      id: item.attributes.id,
      name: item.attributes.name || 'Cliente sin nombre',
      email: item.attributes.email || '',
      phone: item.attributes.phone_number || '',
      tax_id: item.attributes.tax_id || '',
      single_identity_document_number: item.attributes.single_identity_document_number || '',
      city: item.attributes.city_name || '',
      state: item.attributes.state_name || '',
      address: item.attributes.street1 || '',
      is_vip: item.attributes.is_vip || 0,
      is_employee: item.attributes.is_employee || 0,
      total_orders: item.attributes.total_order_number || 0,
      last_order_date: item.attributes.last_order_date || null,
      registration_date: item.attributes.registration_date_formatted || '',
      // Original data for reference
      original: item.attributes
    }));

    // Pagination info
    const pagination = {
      currentPage: response.data.meta.page || page,
      totalPages: Math.ceil((response.data.meta.records || 0) / perPage),
      totalRecords: response.data.meta.records || 0,
      perPage: perPage,
      from: response.data.meta.from || 0,
      to: response.data.meta.to || 0
    };

    console.log('Clientes cargados desde API externa:', clients.length);
    return {
      clients: clients,
      pagination: pagination
    };
  } catch (error) {
    console.error('Error fetching clients from external API:', error);
    // Return empty result if API fails
    return {
      clients: [],
      pagination: {
        currentPage: page,
        totalPages: 0,
        totalRecords: 0,
        perPage: perPage,
        from: 0,
        to: 0
      }
    };
  }
};

// Crear venta en ERP externo
export const createSale = async (saleData) => {
  const url = 'https://erp-api.decima.la/api/income/sale';
  
  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Platform-Id': '4',
        'Content-Type': 'application/json',
        'Authorization': 'Bearer 38655|LilZOOhaimGAk55Mclyu9xlAkwd2hCyJlF4k2evf'
      },
      body: JSON.stringify(saleData)
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Error ${response.status}: ${errorText}`);
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error creating sale:', error);
    throw error;
  }
};

// Procesar factura electrónica DTE
export const processElectronicInvoice = async (saleId) => {
  const url = 'https://erp-api.decima.la/api/slsv/electronic-invoice/preparer';
  
  const requestBody = {
    data: {
      type: 'electronic-invoice',
      attributes: {
        id: saleId,
        type: 'S',
        operation: 'issue'
      }
    }
  };

  try {
    const response = await fetch(url, {
      method: 'PUT',
      headers: {
        'Accept': 'application/json',
        'Platform-Id': '4',
        'Content-Type': 'application/json',
        'Authorization': 'Bearer 38655|LilZOOhaimGAk55Mclyu9xlAkwd2hCyJlF4k2evf'
      },
      body: JSON.stringify(requestBody)
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Error ${response.status}: ${errorText}`);
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error processing electronic invoice:', error);
    throw error;
  }
};

export default {
  fetchArticles,
  fetchArticlesWithParams,
  fetchPaymentTerms,
  fetchSalePoints,
  fetchPaymentForms,
  createPaymentTerm,
  createPaymentForm,
  fetchSales,
  fetchSalesWithParams,
  fetchClients,
  fetchClientsFromExternalAPI,
  fetchDocumentTypes,
  createSale,
  processElectronicInvoice,
  apiClient
};