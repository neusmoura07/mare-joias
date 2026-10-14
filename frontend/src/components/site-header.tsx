import { useState } from "react";
import { Link } from "@tanstack/react-router";
import { Menu, Search, ShoppingBag, UserRound, X } from "lucide-react";
import { useCart } from "@/contexts/cart-context";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetTrigger } from "@/components/ui/sheet";

const links = [{ to: "/catalogo" as const, label: "Comprar" }, { to: "/catalogo" as const, label: "Novidades" }, { to: "/catalogo" as const, label: "Anéis" }, { to: "/catalogo" as const, label: "Colares" }];

export function SiteHeader() {
  const [searchOpen, setSearchOpen] = useState(false);
  const { itemCount, setIsOpen } = useCart();
  return (
    <header className="sticky top-0 z-40 bg-background/95 backdrop-blur-md">
      <div className="bg-primary px-4 py-2 text-center text-[10px] font-semibold uppercase tracking-[0.22em] text-primary-foreground">Frete grátis para todo Brasil acima de R$ 399</div>
      <div className="mx-auto flex h-18 max-w-[1440px] items-center justify-between border-b px-4 sm:px-8 lg:px-12">
        <div className="flex flex-1 items-center gap-2 lg:hidden"><Sheet><SheetTrigger asChild><Button variant="ghost" size="icon" aria-label="Abrir menu"><Menu /></Button></SheetTrigger><SheetContent side="left" className="w-full p-8 sm:max-w-sm"><SheetHeader><SheetTitle className="text-left font-serif text-3xl">Menu</SheetTitle></SheetHeader><nav className="mt-10 flex flex-col gap-6">{links.map((item, index) => <Link key={`${item.label}-${index}`} to={item.to} className="border-b pb-4 font-serif text-3xl">{item.label}</Link>)}</nav></SheetContent></Sheet></div>
        <nav className="hidden flex-1 items-center gap-7 lg:flex">{links.map((item, index) => <Link key={`${item.label}-${index}`} to={item.to} className="text-[11px] font-bold uppercase tracking-widest text-muted-foreground transition-colors hover:text-foreground">{item.label}</Link>)}</nav>
         <Link to="/" aria-label="MARÉ JOIAS — início" className="font-serif text-3xl font-semibold leading-none sm:text-4xl">MARÉ JOIAS</Link>
        <div className="flex flex-1 items-center justify-end gap-1 sm:gap-2">
          <Button variant="ghost" size="icon" aria-label={searchOpen ? "Fechar busca" : "Buscar"} onClick={() => setSearchOpen(!searchOpen)}>{searchOpen ? <X /> : <Search />}</Button>
          <Button variant="ghost" size="icon" className="hidden sm:inline-flex" aria-label="Minha conta"><UserRound /></Button>
          <Button variant="ghost" size="icon" className="relative" aria-label={`Carrinho com ${itemCount} itens`} onClick={() => setIsOpen(true)}><ShoppingBag />{itemCount > 0 && <span className="absolute right-0 top-0 flex size-4 items-center justify-center rounded-full bg-gold text-[9px] font-bold text-ink">{itemCount}</span>}</Button>
        </div>
      </div>
      {searchOpen && <form action="/catalogo" className="absolute inset-x-0 top-full border-b bg-background p-4 shadow-lg"><div className="mx-auto flex max-w-2xl items-center gap-3"><Search className="size-5 text-muted-foreground" /><Input name="busca" autoFocus placeholder="O que você procura?" className="h-12 border-0 border-b bg-transparent text-base shadow-none focus-visible:ring-0" /><Button type="submit">Buscar</Button></div></form>}
    </header>
  );
}