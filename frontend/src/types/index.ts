export type ProductCategory = "Anéis" | "Colares" | "Brincos" | "Pulseiras";

export interface Product {
  id: string;
  slug: string;
  name: string;
  material: string;
  category: ProductCategory;
  price: number;
  description: string;
  image: string;
  sizes: string[];
  isNew: boolean;
}

export interface CartItem {
  product: Product;
  quantity: number;
  size: string;
}

export interface User {
  id: string;
  name: string;
  email: string;
}

export interface Order {
  id: string;
  user?: User;
  items: CartItem[];
  subtotal: number;
  shipping: number;
  total: number;
  status: "processing" | "confirmed";
  createdAt: string;
}

export interface CheckoutPayload {
  customerName: string;
  email: string;
  address: string;
  city: string;
  state: string;
  zipCode: string;
  items: CartItem[];
}