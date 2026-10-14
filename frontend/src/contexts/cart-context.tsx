import { createContext, useContext, useMemo, useState, type ReactNode } from "react";
import type { CartItem, Product } from "@/types";

interface CartContextValue {
  items: CartItem[];
  isOpen: boolean;
  itemCount: number;
  subtotal: number;
  addItem: (product: Product, size: string, quantity?: number) => void;
  removeItem: (productId: string, size: string) => void;
  updateQuantity: (productId: string, size: string, quantity: number) => void;
  clearCart: () => void;
  setIsOpen: (open: boolean) => void;
}

const CartContext = createContext<CartContextValue | undefined>(undefined);

export function CartProvider({ children }: { children: ReactNode }) {
  const [items, setItems] = useState<CartItem[]>([]);
  const [isOpen, setIsOpen] = useState(false);

  const addItem = (product: Product, size: string, quantity = 1) => {
    setItems((current) => {
      const match = current.find((item) => item.product.id === product.id && item.size === size);
      if (match) return current.map((item) => item === match ? { ...item, quantity: item.quantity + quantity } : item);
      return [...current, { product, size, quantity }];
    });
    setIsOpen(true);
  };

  const removeItem = (productId: string, size: string) => setItems((current) => current.filter((item) => !(item.product.id === productId && item.size === size)));
  const updateQuantity = (productId: string, size: string, quantity: number) => setItems((current) => current.map((item) => item.product.id === productId && item.size === size ? { ...item, quantity: Math.max(1, quantity) } : item));
  const clearCart = () => setItems([]);
  const value = useMemo(() => ({ items, isOpen, itemCount: items.reduce((sum, item) => sum + item.quantity, 0), subtotal: items.reduce((sum, item) => sum + item.product.price * item.quantity, 0), addItem, removeItem, updateQuantity, clearCart, setIsOpen }), [items, isOpen]);

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  const context = useContext(CartContext);
  if (!context) throw new Error("useCart precisa estar dentro de CartProvider");
  return context;
}