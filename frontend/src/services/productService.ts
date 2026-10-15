const API_URL = "http://localhost:8080/api/v1";

export async function getProducts(page = 0, size = 10) {
  const response = await fetch(`${API_URL}/products?page=${page}&size=${size}`);
  
  if (!response.ok) {
    throw new Error("Erro ao carregar o catálogo");
  }
  
  return response.json(); 
}

export async function getProductBySlug(slug: string) {
  const response = await fetch(`${API_URL}/products/${slug}`);
  
  if (response.status === 404) {
    throw new Error("NOT_FOUND");
  }
  
  if (!response.ok) {
    throw new Error("Erro ao buscar a joia");
  }
  
  return response.json();
}