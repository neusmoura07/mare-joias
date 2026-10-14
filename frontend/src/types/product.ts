export interface Product {
  id: string;
  categoryId: string;
  sku: string;
  name: string;
  slug: string;
  description: string;
  material: string;
  priceCents: number;
  imageUrl: string;
  stockQuantity: number;
  isActive: boolean;
}

// Interface genérica para mapear o Page<T> do Spring Boot
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}