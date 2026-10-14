import { Link } from "@tanstack/react-router";
import { Minus, Plus, ShoppingBag, Trash2 } from "lucide-react";
import { useCart } from "@/contexts/cart-context";
import { formatCurrency } from "@/lib/format";
import { Button } from "@/components/ui/button";
import { Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle } from "@/components/ui/sheet";

export function CartDrawer() {
  const { items, isOpen, setIsOpen, subtotal, updateQuantity, removeItem } = useCart();
  
  return (
    <Sheet open={isOpen} onOpenChange={setIsOpen}>
      <SheetContent className="flex w-full flex-col p-0 sm:max-w-md">
        <SheetHeader className="border-b px-6 py-6 text-left">
          <SheetTitle className="font-serif text-3xl font-medium">Sua seleção</SheetTitle>
          <SheetDescription>{items.length ? `${items.length} peça${items.length > 1 ? "s" : ""} reservada${items.length > 1 ? "s" : ""}` : "Seu carrinho está vazio"}</SheetDescription>
        </SheetHeader>
        
        {items.length === 0 ? (
          <div className="flex flex-1 flex-col items-center justify-center px-8 text-center">
            <ShoppingBag className="mb-5 size-10 stroke-1 text-gold" />
            <h3 className="font-serif text-2xl">Encontre sua próxima peça</h3>
            <p className="mt-2 text-sm text-muted-foreground">Joias feitas para marcar todos os seus momentos.</p>
            <Button asChild className="mt-6" onClick={() => setIsOpen(false)}><Link to="/catalogo">Explorar coleção</Link></Button>
          </div>
        ) : (
          <>
            <div className="min-h-0 flex-1 space-y-5 overflow-y-auto px-6 py-5">
              {items.map((item) => (
                <div key={`${item.product.id}-${item.size}`} className="flex gap-4 border-b pb-5">
                  {/* ATUALIZADO: item.product.imageUrl */}
                  <img src={item.product.imageUrl} alt={item.product.name} width={112} height={112} className="size-28 object-cover bg-ivory rounded-md" />
                  
                  <div className="flex min-w-0 flex-1 flex-col">
                    <div className="flex justify-between gap-2">
                      <div>
                        <h3 className="font-serif text-xl leading-none">{item.product.name}</h3>
                        <p className="mt-2 text-[11px] uppercase tracking-widest text-muted-foreground">Tamanho {item.size}</p>
                      </div>
                      <Button variant="ghost" size="icon" aria-label={`Remover ${item.product.name}`} onClick={() => removeItem(item.product.id, item.size)}><Trash2 /></Button>
                    </div>
                    
                    <div className="mt-auto flex items-center justify-between">
                      <div className="flex h-8 items-center border">
                        <Button variant="ghost" size="icon" className="size-8" aria-label="Diminuir quantidade" onClick={() => updateQuantity(item.product.id, item.size, item.quantity - 1)}><Minus /></Button>
                        <span className="w-7 text-center text-xs">{item.quantity}</span>
                        <Button variant="ghost" size="icon" className="size-8" aria-label="Aumentar quantidade" onClick={() => updateQuantity(item.product.id, item.size, item.quantity + 1)}><Plus /></Button>
                      </div>
                      {/* ATUALIZADO: priceCents / 100 na linha do item */}
                      <strong className="text-sm">{formatCurrency((item.product.priceCents / 100) * item.quantity)}</strong>
                    </div>
                  </div>
                </div>
              ))}
            </div>
            
            <div className="shrink-0 border-t bg-ivory p-6">
              <div className="mb-2 flex justify-between font-semibold">
                <span>Subtotal</span>
                <span>{formatCurrency(subtotal)}</span>
              </div>
              <p className="mb-5 text-xs text-muted-foreground">Frete calculado no checkout.</p>
              <Button asChild size="lg" className="w-full">
                <Link to="/checkout" onClick={() => window.setTimeout(() => setIsOpen(false), 50)}>Ir para checkout</Link>
              </Button>
            </div>
          </>
        )}
      </SheetContent>
    </Sheet>
  );
}