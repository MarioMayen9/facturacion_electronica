// Test script to verify payment forms API
import axios from 'axios';

const testPaymentFormsAPI = async () => {
  try {
    console.log('Testing payment forms API...');
    
    const response = await axios.get('https://erp-api.decima.la/api/catalogs/payment-form', {
      headers: {
        'Authorization': 'Bearer 38655|LilZOOhaimGAk55Mclyu9xlAkwd2hCyJlF4k2evf',
        'Platform-Id': '4',
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });
    
    console.log('Raw API response:', response.data);
    
    if (response.data && response.data.data && Array.isArray(response.data.data)) {
      const paymentForms = response.data.data
        .filter(item => item.attributes.is_visible_on_sales === 1)
        .map(item => ({
          id: item.attributes.id,
          name: item.attributes.name,
          value: item.attributes.id,
          label: item.attributes.name
        }));
      
      console.log('Filtered payment forms:', paymentForms);
      console.log('Number of forms:', paymentForms.length);
    } else {
      console.log('Unexpected response structure');
    }
    
  } catch (error) {
    console.error('API test failed:', error.message);
    if (error.response) {
      console.error('Status:', error.response.status);
      console.error('Data:', error.response.data);
    }
  }
};

testPaymentFormsAPI();