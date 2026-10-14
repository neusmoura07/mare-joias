import { Link } from "@tanstack/react-router";
import { ArrowUpRight } from "lucide-react";
import type { Product } from "@/types/product"; // <-- Confirme se o caminho para a interface está correto
import { formatCurrency } from "@/lib/format";

export function ProductCard({ product, priority = false }: { product: Product; priority?: boolean }) {
  return (
    <article className="group min-w-0">
      <Link to="/produto/$slug" params={{ slug: product.slug }} className="block overflow-hidden bg-ivory">
        <div className="relative aspect-square overflow-hidden">
          {/* Usando imageUrl do Spring Boot */}
          <img src={product.imageUrl} alt={product.name} width={1008} height={1008} loading={priority ? "eager" : "lazy"} className="h-full w-full object-cover transition-transform duration-700 group-hover:scale-[1.035]" />
          <span className="absolute bottom-3 right-3 flex size-10 translate-y-2 items-center justify-center rounded-full bg-background text-foreground opacity-0 transition-all group-hover:translate-y-0 group-hover:opacity-100"><ArrowUpRight className="size-4" /></span>
        </div>
      </Link>
      <div className="flex items-start justify-between gap-3 pt-4">
        <div className="min-w-0">
          <Link to="/produto/$slug" params={{ slug: product.slug }} className="font-serif text-xl leading-none hover:text-gold">{product.name}</Link>
          <p className="mt-2 text-[11px] uppercase tracking-widest text-muted-foreground">{product.material}</p>
        </div>
        {/* Dividindo os cêntimos por 100 para o formatador do frontend */}
        <p className="shrink-0 text-sm font-semibold">{formatCurrency(product.priceCents / 100)}</p>
      </div>
    </article>
  );
}