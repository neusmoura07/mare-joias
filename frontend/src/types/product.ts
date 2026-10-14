// Mapeia o ProductSizeDTO do Java
export interface ProductSize {
  id: string;
  sizeName: string;
  stockQuantity: number;
}

// Mapeia o ProductSummaryDTO do Java (Usado no Catálogo/Página Inicial)
export interface Product {
  id: string;
  sku: string;
  name: string;
  slug: string;
  description: string;
  material: string;
  priceCents: number;
  imageUrl: string;
  stockQuantity: number;
  isActive: boolean;
  categoryName: string;
  categorySlug: string;
}

// Mapeia o ProductDetailDTO do Java (Usado na Página de Detalhes da Joia)
export interface ProductDetail extends Product {
  sizes: ProductSize[];
}

// Interface genérica para mapear o Page<T> do Spring Boot
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}