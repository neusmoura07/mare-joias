import ringAura from "@/assets/ring-aura.jpg";
import necklaceElo from "@/assets/necklace-elo.jpg";
import earringsLume from "@/assets/earrings-lume.jpg";
import braceletFluxo from "@/assets/bracelet-fluxo.jpg";
import ringNorte from "@/assets/ring-norte.jpg";
import necklaceGota from "@/assets/necklace-gota.jpg";
import type { CheckoutPayload, Order, Product } from "@/types";

export const products: Product[] = [
  { id: "1", slug: "anel-aura", name: "Anel Aura", material: "Prata 925", category: "Anéis", price: 289, description: "Volume, brilho e movimento em uma peça que muda qualquer composição. Modelado em prata 925 com acabamento espelhado.", image: ringAura, sizes: ["12", "14", "16", "18", "20"], isNew: true },
  { id: "2", slug: "colar-elo", name: "Colar Elo", material: "Prata 925", category: "Colares", price: 649, description: "Elos orgânicos e robustos, polidos à mão para refletir luz por todos os ângulos.", image: necklaceElo, sizes: ["40 cm", "45 cm", "50 cm"], isNew: true },
  { id: "3", slug: "brinco-lume", name: "Brinco Lume", material: "Prata 925", category: "Brincos", price: 329, description: "Um par assimétrico de linhas fluidas para usar junto, separado ou do seu jeito.", image: earringsLume, sizes: ["Único"], isNew: true },
  { id: "4", slug: "bracelete-fluxo", name: "Bracelete Fluxo", material: "Prata 925 + Ouro 18k", category: "Pulseiras", price: 489, description: "Prata polida com um encontro sutil em ouro. Minimal no traço, marcante na presença.", image: braceletFluxo, sizes: ["P", "M", "G"], isNew: true },
  { id: "5", slug: "anel-norte", name: "Anel Norte", material: "Prata 925", category: "Anéis", price: 219, description: "Sinete delicado com estrela gravada, feito para acompanhar todos os seus caminhos.", image: ringNorte, sizes: ["12", "14", "16", "18", "20"], isNew: false },
  { id: "6", slug: "colar-gota", name: "Colar Gota", material: "Prata 925", category: "Colares", price: 359, description: "Uma gota de prata líquida suspensa em corrente fina e leve.", image: necklaceGota, sizes: ["40 cm", "45 cm", "50 cm"], isNew: false },
];

const wait = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

export async function getProducts(): Promise<Product[]> {
  await wait(1000);
  return products;
}

export async function getProduct(slug: string): Promise<Product | undefined> {
  await wait(1000);
  return products.find((product) => product.slug === slug);
}

export async function processCheckout(payload: CheckoutPayload): Promise<Order> {
  await wait(1000);
  const subtotal = payload.items.reduce((sum, item) => sum + item.product.price * item.quantity, 0);
  return {
    id: `PR-${Date.now().toString().slice(-6)}`,
    items: payload.items,
    subtotal,
    shipping: 0,
    total: subtotal,
    status: "confirmed",
    createdAt: new Date().toISOString(),
  };
}