import { useEffect, useMemo, useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { SlidersHorizontal } from "lucide-react";
// IMPORTANTE: Remova a importação estática do Lovable (import { products } from "@/services/api";)
import { getProducts } from "@/services/productService"; // <-- Importamos o nosso serviço real
import type { Product } from "@/types/product"; // <-- A interface que criámos
import { ProductCard } from "@/components/product-card";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetTrigger } from "@/components/ui/sheet";

export const Route = createFileRoute("/catalogo")({ head: () => ({ meta: [{ title: "Coleção de Joias em Prata — MARÉ" }, { name: "description", content: "Explore anéis, colares, brincos e pulseiras em prata 925." }, { property: "og:title", content: "Coleção de Joias em Prata — MARÉ" }, { property: "og:description", content: "Joias autorais em prata 925 para usar do seu jeito." }, { property: "og:type", content: "website" }, { name: "twitter:card", content: "summary_large_image" }] }), component: CatalogPage });

function Filters({ selected, toggle }: { selected: string[]; toggle: (category: string) => void }) {
  return <div><p className="mb-5 text-[11px] font-bold uppercase tracking-widest">Categoria</p><div className="space-y-4">{["Anéis", "Colares", "Brincos", "Pulseiras"].map((category) => <div key={category} className="flex items-center gap-3"><Checkbox id={category} checked={selected.includes(category)} onCheckedChange={() => toggle(category)} /><Label htmlFor={category} className="cursor-pointer font-normal">{category}</Label></div>)}</div><div className="mt-9 border-t pt-7"><p className="mb-4 text-[11px] font-bold uppercase tracking-widest">Preço</p><p className="text-sm text-muted-foreground">De R$ 0 a R$ 700</p><div className="mt-4 h-0.5 bg-border"><div className="h-full w-4/5 bg-primary" /></div></div></div>;
}

function CatalogPage() {
  const [categories, setCategories] = useState<string[]>([]);
  const [sort, setSort] = useState("featured");
  
  // NOVOS ESTADOS PARA A API DO SPRING BOOT
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  // BUSCA OS DADOS AO INICIAR A PÁGINA
  useEffect(() => {
    getProducts(0, 50) // Traz até 50 produtos da página 0
      .then((page) => setProducts(page.content))
      .catch((err) => console.error("Erro ao carregar produtos:", err))
      .finally(() => setIsLoading(false));
  }, []);

  const toggle = (category: string) => setCategories((value) => value.includes(category) ? value.filter((item) => item !== category) : [...value, category]);
  
  // ADAPTADO PARA USAR OS PRODUTOS DA NOSSA API
  const visible = useMemo(() => {
    // Por enquanto, mostra todos os produtos da API. 
    // A filtragem exata por categoria pode ser implementada no Spring Boot via query params depois.
    const filtered = products; 
    
    return [...filtered].sort((a, b) => {
      if (sort === "low") return a.priceCents - b.priceCents;
      if (sort === "high") return b.priceCents - a.priceCents;
      return 0; // featured
    });
  }, [products, sort]);

  if (isLoading) {
    return <div className="flex h-screen items-center justify-center font-serif text-2xl text-gold">A carregar a coleção MARÉ...</div>;
  }

  return <main><section className="bg-primary px-5 py-14 text-primary-foreground sm:px-8 sm:py-20 lg:px-12"><div className="mx-auto max-w-[1440px]"><p className="text-[11px] font-bold uppercase tracking-[0.25em] text-gold">O Ritual MARÉ</p><h1 className="mt-4 max-w-4xl text-6xl font-medium leading-[0.9] sm:text-8xl lg:text-9xl">Toda a coleção</h1></div></section><section className="mx-auto max-w-[1440px] px-5 py-10 sm:px-8 lg:px-12"><div className="mb-8 flex items-center justify-between gap-4 border-b pb-5"><p className="text-sm text-muted-foreground">{visible.length} peças</p><div className="flex items-center gap-3"><Sheet><SheetTrigger asChild><Button variant="outline" className="lg:hidden"><SlidersHorizontal /> Filtrar</Button></SheetTrigger><SheetContent side="left"><SheetHeader><SheetTitle className="font-serif text-3xl">Filtros</SheetTitle></SheetHeader><div className="mt-8"><Filters selected={categories} toggle={toggle} /></div></SheetContent></Sheet><Select value={sort} onValueChange={setSort}><SelectTrigger className="w-40"><SelectValue /></SelectTrigger><SelectContent><SelectItem value="featured">Destaques</SelectItem><SelectItem value="low">Menor preço</SelectItem><SelectItem value="high">Maior preço</SelectItem></SelectContent></Select></div></div><div className="grid gap-10 lg:grid-cols-[220px_1fr]"><aside className="hidden lg:block"><Filters selected={categories} toggle={toggle} /></aside><div className="grid grid-cols-2 gap-x-4 gap-y-10 md:grid-cols-3 lg:gap-x-6">{visible.map((product) => <ProductCard key={product.id} product={product} />)}</div></div></section></main>;
}