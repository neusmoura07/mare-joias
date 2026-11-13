

const API_BASE_URL = "http://localhost:8080/api/v1";

export interface AddressCreatePayload {
  zipCode: string;
  street: string;
}

export interface CheckoutRequestPayload {
  addressId: string;
  items: {
    sizeId: string;
    quantity: number;
  }[];
}

export interface CheckoutResponseData {
  orderId: string;
  paymentUrl: string;
}

export async function createCustomerAddress(
  payload: AddressCreatePayload,
  token: string
): Promise<string> {
  const response = await fetch(`${API_BASE_URL}/customers/me/addresses`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error("Erro ao salvar o endereço de entrega.");
  }

  const data = await response.json();
  // Suporta tanto { addressId: "..." } quanto { id: "..." }
  const createdId = data.addressId || data.id;

  if (!createdId) {
    throw new Error("ID do endereço não retornado pelo servidor.");
  }

  return createdId;
}

export async function createCheckoutSession(
  payload: CheckoutRequestPayload,
  token: string
): Promise<CheckoutResponseData> {
  const response = await fetch(`${API_BASE_URL}/checkout`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => null);
    throw new Error(errorData?.message || "Erro ao iniciar o pagamento com o Mercado Pago.");
  }

  return response.json();
}