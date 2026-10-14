import { useEffect, useState } from "react";
import { createFileRoute, Link } from "@tanstack/react-router";
import { ArrowRight, Recycle, ShieldCheck, Sparkles } from "lucide-react";
import heroSilver from "@/assets/hero-silver.jpg";

// Importações atualizadas para usar a sua API
import { getProducts } from "@/services/productService";
import type { Product } from "@/types/product";
import { ProductCard } from "@/components/product-card";
import { Button } from "@/components/ui/button";

export const Route = createFileRoute("/")({ head: () => ({ meta: [{ title: "PRATA — Joias em Prata 925" }, { name: "description", content: "Joias de prata autorais, marcantes e feitas para usar do seu jeito." }, { property: "og:title", content: "PRATA — Joias em Prata 925" }, { property: "og:description", content: "Prata que acompanha seu ritmo e expressa quem você é." }, { property: "og:type", content: "website" }, { name: "twitter:card", content: "summary_large_image" }] }), component: Index });

function Index() {
  // Estado para armazenar as joias que vêm do banco de dados
  const [recentProducts, setRecentProducts] = useState<Product[]>([]);

  // Busca as 4 primeiras joias ao abrir a página
  useEffect(() => {
    getProducts(0, 4)
      .then((page) => setRecentProducts(page.content))
      .catch((err) => console.error("Erro ao carregar lançamentos:", err));
  }, []);

  return <main><section className="relative min-h-[calc(100svh-7rem)] overflow-hidden bg-primary text-primary-foreground"><img src={heroSilver} alt="Mulher usando colares e brincos de prata" width={1200} height={1504} className="absolute inset-0 h-full w-full object-cover object-center sm:object-[center_32%] lg:left-auto lg:w-[58%]" /><div className="absolute inset-0 bg-gradient-to-r from-primary via-primary/75 to-transparent lg:right-[42%]" /><div className="relative mx-auto flex min-h-[calc(100svh-7rem)] max-w-[1440px] items-end px-5 pb-16 pt-24 sm:px-8 lg:items-center lg:px-12 lg:pb-20"><div className="max-w-2xl"><p className="mb-5 text-[11px] font-bold uppercase tracking-[0.3em] text-gold">Coleção 01 · Estado líquido</p><h1 className="text-balance text-7xl font-medium leading-[0.82] sm:text-8xl lg:text-[9rem]">Prata em movimento.</h1><p className="mt-7 max-w-md text-base leading-7 text-primary-foreground/75">Formas livres, brilho intenso e peças que acompanham cada versão de você.</p><Button asChild variant="gold" size="lg" className="mt-8"><Link to="/catalogo">Conhecer coleção <ArrowRight /></Link></Button></div></div></section><section className="mx-auto max-w-[1440px] px-5 py-20 sm:px-8 lg:px-12 lg:py-28"><div className="mb-12 flex items-end justify-between gap-6"><div><p className="text-[11px] font-bold uppercase tracking-[0.25em] text-gold">Chegaram agora</p><h2 className="mt-3 text-5xl font-medium sm:text-7xl">Lançamentos</h2></div><Link to="/catalogo" className="hidden items-center gap-2 text-xs font-bold uppercase tracking-widest sm:flex">Ver tudo <ArrowRight className="size-4" /></Link></div><div className="grid grid-cols-2 gap-x-4 gap-y-10 lg:grid-cols-4 lg:gap-6">
    
    {/* Loop usando os produtos reais vindos do estado recentProducts */}
    {recentProducts.map((product, index) => <ProductCard key={product.id} product={product} priority={index < 2} />)}
    
    </div></section><section className="grid bg-primary text-primary-foreground lg:grid-cols-2"><div className="flex min-h-[520px] flex-col justify-between px-6 py-14 sm:px-12 lg:p-20"><span className="font-serif text-8xl leading-none text-gold/45">“</span><div><h2 className="text-balance text-5xl leading-[0.95] sm:text-7xl">Não é sobre combinar. É sobre expressar.</h2><p className="mt-7 max-w-md leading-7 text-primary-foreground/65">Prata 925, design autoral e liberdade para criar camadas, contrastes e histórias.</p></div></div><div className="grid grid-cols-3 divide-x divide-primary-foreground/15 border-t border-primary-foreground/15 lg:border-l lg:border-t-0">{[{ icon: Sparkles, title: "Prata 925", text: "Brilho que permanece" }, { icon: ShieldCheck, title: "1 ano", text: "Garantia em todas as peças" }, { icon: Recycle, title: "Circular", text: "Embalagem consciente" }].map(({ icon: Icon, title, text }) => <div key={title} className="flex min-h-52 flex-col justify-end p-5 sm:p-8"><Icon className="mb-auto size-6 text-gold" /><strong className="text-xs uppercase tracking-widest">{title}</strong><span className="mt-2 text-xs leading-5 text-primary-foreground/55">{text}</span></div>)}</div></section></main>;
}