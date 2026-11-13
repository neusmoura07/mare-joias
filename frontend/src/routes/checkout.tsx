import { useState, type ComponentProps, type FormEvent } from "react";
import { createFileRoute, Link } from "@tanstack/react-router";
import { ShieldCheck, LoaderCircle, LockKeyhole, ExternalLink } from "lucide-react";
import { useCart } from "@/contexts/cart-context";
import { formatCurrency } from "@/lib/format";
import { createCustomerAddress, createCheckoutSession } from "@/services/api"; 
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

export const Route = createFileRoute("/checkout")({
  head: () => ({
    meta: [
      { title: "Checkout seguro — MARÉ" },
      { name: "description", content: "Finalize seu pedido de joias com segurança." },
    ],
  }),
  component: CheckoutPage,
});

const Field = ({ label, ...props }: { label: string } & ComponentProps<typeof Input>) => (
  <div className="space-y-2">
    <Label>{label}</Label>
    <Input required className="h-12" {...props} />
  </div>
);

function CheckoutPage() {
  const { items, subtotal, clearCart } = useCart();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!items.length) return;

    setLoading(true);
    setError(null);

    const token = localStorage.getItem("mare_token");

    if (!token) {
      setError("Sua sessão expirou ou você não está logado. Faça login para continuar.");
      setLoading(false);
      return;
    }

    const form = new FormData(event.currentTarget);

    try {
      // 1. Cria o endereço de entrega
      const addressId = await createCustomerAddress(
        {
          zipCode: String(form.get("zip")).trim(),
          street: `${form.get("address")} - ${form.get("city")}, ${form.get("state")}`,
        },
        token
      );

      // 2. Cria o pedido e gera a preferência no Mercado Pago
      const { orderId, paymentUrl } = await createCheckoutSession(
        {
          addressId,
          items: items.map((item) => ({
            sizeId: item.sizeId,
            quantity: item.quantity,
          })),
        },
        token
      );

      // Guarda o ID do pedido para referência nas telas de retorno e limpa o carrinho
      localStorage.setItem("mare_last_order_id", orderId);
      clearCart();

      // 3. Redireciona o cliente para o Checkout Pro do Mercado Pago
      window.location.href = paymentUrl;
    } catch (err: any) {
      setError(err.message || "Ocorreu um erro inesperado ao processar seu pedido.");
      setLoading(false); // Só desativa o loading se der erro (evita clique duplo durante o redirect)
    }
  };

  if (!items.length) {
    return (
      <main className="mx-auto flex min-h-[60vh] max-w-xl flex-col items-center justify-center px-6 text-center">
        <h1 className="text-5xl">Seu carrinho está vazio</h1>
        <p className="mt-4 text-muted-foreground">Escolha suas peças antes de seguir para o checkout.</p>
        <Button asChild className="mt-8">
          <Link to="/catalogo">Explorar coleção</Link>
        </Button>
      </main>
    );
  }

  return (
    <main className="bg-ivory">
      <form
        onSubmit={submit}
        className="mx-auto grid max-w-[1240px] gap-12 px-5 py-12 sm:px-8 lg:grid-cols-[1fr_400px] lg:px-12 lg:py-20"
      >
        <div>
          <div className="mb-10">
            <p className="text-[11px] font-bold uppercase tracking-[0.25em] text-gold">Checkout seguro</p>
            <h1 className="mt-3 text-5xl sm:text-7xl">Finalizar pedido</h1>
          </div>

          {error && (
            <div className="mb-6 rounded-md border border-red-200 bg-red-50 p-4 font-medium text-red-600">
              {error}
            </div>
          )}

          <section className="bg-background p-5 sm:p-8">
            <h2 className="mb-6 text-3xl">Entrega</h2>
            <div className="grid gap-5 sm:grid-cols-2">
              <div className="sm:col-span-2">
                <Field label="Nome completo" name="name" placeholder="Quem irá receber?" />
              </div>
              <Field label="CEP" name="zip" placeholder="00000-000" />
              <Field label="Estado" name="state" placeholder="AL" maxLength={2} />
              <div className="sm:col-span-2">
                <Field label="Endereço (Rua e número)" name="address" placeholder="Ex: Av. Silvio Carlos Viana, 123" />
              </div>
              <div className="sm:col-span-2">
                <Field label="Cidade" name="city" placeholder="Maceió" />
              </div>
            </div>
          </section>

          <section className="mt-5 bg-background p-5 sm:p-8">
            <div className="mb-4 flex items-center justify-between">
              <h2 className="text-3xl">Pagamento via Mercado Pago</h2>
              <ShieldCheck className="size-6 text-gold" />
            </div>
            <p className="text-sm leading-relaxed text-muted-foreground">
              Ao clicar em <strong>Ir para o pagamento</strong>, você será redirecionado para o ambiente seguro do{" "}
              <strong>Mercado Pago</strong>, onde poderá escolher pagar com <strong>Pix</strong>,{" "}
              <strong>Cartão de Crédito</strong> (em até 12x) ou <strong>Boleto</strong>.
            </p>
          </section>
        </div>

        <aside className="lg:sticky lg:top-32 lg:self-start">
          <div className="rounded-lg bg-primary p-6 text-primary-foreground sm:p-8">
            <h2 className="mb-6 text-3xl">Seu pedido</h2>
            <div className="space-y-5">
              {items.map((item) => (
                <div key={`${item.product.id}-${item.size}`} className="flex gap-4">
                  <img src={item.product.imageUrl} alt="" className="size-20 rounded bg-white/10 object-cover" />
                  <div className="min-w-0 flex-1">
                    <p className="font-serif text-lg">{item.product.name}</p>
                    <p className="mt-1 text-xs text-primary-foreground/55">
                      Tam. {item.size} · Qtd. {item.quantity}
                    </p>
                    <p className="mt-2 text-xs font-semibold">
                      {formatCurrency((item.product.priceCents / 100) * item.quantity)}
                    </p>
                  </div>
                </div>
              ))}
            </div>
            <div className="mt-7 border-t border-primary-foreground/15 pt-5">
              <div className="flex justify-between text-sm">
                <span>Subtotal</span>
                <span>{formatCurrency(subtotal)}</span>
              </div>
              <div className="mt-3 flex justify-between text-sm">
                <span>Frete</span>
                <span className="text-gold">Grátis</span>
              </div>
              <div className="mt-5 flex justify-between border-t border-primary-foreground/15 pt-5 text-lg font-bold">
                <span>Total</span>
                <span>{formatCurrency(subtotal)}</span>
              </div>
            </div>
            <Button
              type="submit"
              variant="gold"
              size="lg"
              className="mt-7 w-full text-black hover:bg-gold/90"
              disabled={loading}
            >
              {loading ? (
                <>
                  <LoaderCircle className="mr-2 animate-spin" /> Redirecionando...
                </>
              ) : (
                <>
                  Ir para o pagamento <ExternalLink className="ml-2 size-4" />
                </>
              )}
            </Button>
            <p className="mt-4 flex items-center justify-center gap-2 text-[10px] uppercase tracking-widest text-primary-foreground/50">
              <LockKeyhole className="size-3" /> Ambiente protegido · Mercado Pago
            </p>
          </div>
        </aside>
      </form>
    </main>
  );
}