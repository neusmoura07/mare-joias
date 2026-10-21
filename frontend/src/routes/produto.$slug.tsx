import { useState, useEffect } from "react";
import { createFileRoute, Link, notFound } from "@tanstack/react-router";
import { Minus, Plus, ShieldCheck, Truck } from "lucide-react";
import { useCart } from "@/contexts/cart-context";
import { formatCurrency } from "@/lib/format";
import { Button } from "@/components/ui/button";
import { getProductBySlug, getProducts } from "@/services/productService";
import { ProductCard } from "@/components/product-card";

export const Route = createFileRoute("/produto/$slug")({ 
  loader: async ({ params }) => { 
    try {
      // 1. Tenta buscar o produto principal. Se falhar, vai direto pro catch e mostra 404.
      const product = await getProductBySlug(params.slug);
      
      // 2. Tenta buscar os produtos sugeridos de forma isolada e segura.
      let relatedProducts = [];
      try {
        const relatedPage = await getProducts(0, 4);
        // Garante que pega a lista de produtos, quer o backend devolva uma Page ou uma Lista direta
        relatedProducts = Array.isArray(relatedPage) ? relatedPage : (relatedPage?.content || []);
      } catch (suggestionError) {
        console.error("Aviso: Falha ao carregar a vitrine de sugestões.", suggestionError);
      }

      return { product, relatedProducts };
      
    } catch (error) {
      console.error("Erro Crítico: Produto principal não encontrado ou erro de API.", error);
      throw notFound(); 
    }
  }, 
  head: ({ loaderData }) => ({ 
    meta: [
      { title: loaderData ? `${loaderData.product.name} — MARÉ` : "Produto não encontrado — MARÉ" }, 
      { name: "description", content: loaderData?.product.description ?? "Peça indisponível." }, 
      { property: "og:title", content: loaderData ? `${loaderData.product.name} — MARÉ` : "Produto não encontrado — MARÉ" }, 
      { property: "og:description", content: loaderData?.product.description ?? "Peça indisponível." }, 
      { property: "og:type", content: "website" }, 
      { name: "twitter:card", content: "summary_large_image" }
    ] 
  }), 
  component: ProductPage, 
  notFoundComponent: () => (
    <div className="px-6 py-32 text-center">
      <h1 className="font-serif text-5xl">Joia não encontrada</h1>
      <Button asChild className="mt-8"><Link to="/catalogo">Voltar à coleção</Link></Button>
    </div>
  ) 
});

function ProductPage() {
  const { product, relatedProducts } = Route.useLoaderData();
  
  const initialSize = product.sizes && product.sizes.length > 0 ? product.sizes[0].sizeName : "Único";
  const [size, setSize] = useState(initialSize);
  
  // 1. Lógica de Stock: Encontra o stock do tamanho selecionado
  const selectedSizeObj = product.sizes?.find((s) => s.sizeName === size);
  const availableStock = selectedSizeObj ? selectedSizeObj.stockQuantity : 0;
  
  // 2. Inicia com 1 se tiver stock, 0 se estiver esgotado
  const [quantity, setQuantity] = useState(availableStock > 0 ? 1 : 0);
  const { addItem } = useCart();

  // 3. Atualiza a quantidade limite ao trocar de tamanho
  useEffect(() => {
    setQuantity(availableStock > 0 ? 1 : 0);
  }, [size, availableStock]);

  const suggestions = relatedProducts
    .filter((item: any) => item.id !== product.id)
    .slice(0, 3);
  
  return (
    <main>
      <div className="mx-auto grid max-w-[1440px] lg:grid-cols-[1.15fr_0.85fr]">
        <div className="bg-ivory">
          <img src={product.imageUrl} alt={product.name} width={1008} height={1008} className="aspect-square h-full w-full object-cover" />
        </div>
        <div className="flex items-center px-5 py-12 sm:px-12 lg:px-16">
          <div className="w-full max-w-lg">
            <p className="text-[11px] font-bold uppercase tracking-[0.25em] text-gold">{product.material}</p>
            <h1 className="mt-4 text-6xl font-medium leading-none sm:text-7xl">{product.name}</h1>
            
            <p className="mt-5 text-xl font-semibold">{formatCurrency(product.priceCents / 100)}</p>
            
            <p className="mt-8 leading-7 text-muted-foreground">{product.description}</p>
            
            <div className="mt-9">
              <div className="mb-4 flex justify-between">
                <span className="text-[11px] font-bold uppercase tracking-widest">Escolha o tamanho</span>
                <button className="text-xs underline underline-offset-4">Guia de medidas</button>
              </div>
              <div className="flex flex-wrap gap-2">
                {product.sizes?.map((sizeObj: any) => (
                  <Button 
                    key={sizeObj.id} 
                    type="button" 
                    variant={size === sizeObj.sizeName ? "default" : "outline"} 
                    onClick={() => setSize(sizeObj.sizeName)}
                  >
                    {sizeObj.sizeName}
                  </Button>
                ))}
              </div>
              {/* Aviso visual de baixo stock */}
              {availableStock > 0 && availableStock <= 3 && (
                <p className="mt-3 text-xs text-red-500">Restam apenas {availableStock} unidades deste tamanho!</p>
              )}
            </div>
            
            <div className="mt-8 flex gap-3">
              <div className="flex h-12 items-center border">
                <Button 
                  variant="ghost" 
                  size="icon" 
                  aria-label="Diminuir quantidade" 
                  onClick={() => setQuantity((value) => Math.max(1, value - 1))}
                  disabled={quantity <= 1 || availableStock === 0}
                >
                  <Minus />
                </Button>
                <span className="w-8 text-center text-sm">{quantity}</span>
                <Button 
                  variant="ghost" 
                  size="icon" 
                  aria-label="Aumentar quantidade" 
                  onClick={() => setQuantity((value) => Math.min(availableStock, value + 1))}
                  disabled={quantity >= availableStock}
                >
                  <Plus />
                </Button>
              </div>
              
              {/* Botão desativa e muda texto se não houver stock */}
              <Button 
                size="lg" 
                className="flex-1" 
                disabled={availableStock === 0}
                onClick={() => addItem(product, size, selectedSizeObj?.id || "", quantity)}
              >
                {availableStock === 0 ? "Esgotado" : "Adicionar ao carrinho"}
              </Button>
            </div>
            
            <div className="mt-8 grid gap-4 border-t pt-7 text-xs sm:grid-cols-2">
              <div className="flex items-center gap-3">
                <Truck className="size-5 text-gold" />
                <span>Frete grátis acima de R$ 399</span>
              </div>
              <div className="flex items-center gap-3">
                <ShieldCheck className="size-5 text-gold" />
                <span>Garantia de 1 ano</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      {suggestions.length > 0 && (
        <section className="mx-auto max-w-[1440px] px-5 py-20 sm:px-8 lg:px-12">
          <div className="mb-10 flex items-end justify-between">
            <h2 className="text-4xl sm:text-5xl">Combine com</h2>
            <Link to="/catalogo" className="text-xs font-bold uppercase tracking-widest">Ver coleção</Link>
          </div>
          <div className="grid grid-cols-2 gap-4 md:grid-cols-3">
            {suggestions.map((item: any) => (
              <ProductCard key={item.id} product={item} />
            ))}
          </div>
        </section>
      )}
    </main>
  );
}