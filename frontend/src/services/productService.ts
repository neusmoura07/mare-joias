import { Page, Product } from '@/types/product';

const API_BASE_URL = 'http://localhost:8080/api/v1';

export const getProducts = async (page = 0, size = 10): Promise<Page<Product>> => {
  const response = await fetch(`${API_BASE_URL}/products?page=${page}&size=${size}`);
  
  if (!response.ok) {
    throw new Error('Falha ao buscar os produtos da API');
  }
  
  return response.json();
};

export const getProductBySlug = async (slug: string): Promise<ProductDetail> => {
  // A URL correta é /products/${slug} (sem a palavra slug no meio)
  const response = await fetch(`${API_BASE_URL}/products/${slug}`);
  
  if (!response.ok) {
    throw new Error('Joia não encontrada');
  }
  
  return response.json();
};