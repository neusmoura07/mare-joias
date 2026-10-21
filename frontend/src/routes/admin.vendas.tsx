import { createFileRoute } from '@tanstack/react-router'
import { useEffect, useState } from 'react'

export const Route = createFileRoute('/admin/vendas')({
  component: VendasAdmin,
})

interface OrderItem {
  id: string
  productNameSnapshot: string
  unitPriceCentsSnapshot: number
  size: string
  quantity: number
}

interface Order {
  id: string
  status: string
  totalAmountCents: number
  shippingFeeCents: number
  shippingAddress: string
  createdAt: string
  items: OrderItem[]
}

function VendasAdmin() {
  const [orders, setOrders] = useState<Order[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchOrders = async () => {
      const token = localStorage.getItem('mare_token')
      try {
        const response = await fetch('http://localhost:8080/api/v1/admin/orders', {
          headers: { 'Authorization': `Bearer ${token}` }
        })
        if (response.ok) {
          const data = await response.json()
          setOrders(data)
        }
      } catch (error) {
        console.error('Erro ao buscar pedidos', error)
      } finally {
        setLoading(false)
      }
    }
    fetchOrders()
  }, [])

  const handleStatusChange = async (orderId: string, newStatus: string) => {
    const token = localStorage.getItem('mare_token')
    try {
      const response = await fetch(`http://localhost:8080/api/v1/admin/orders/${orderId}/status`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ status: newStatus })
      })

      if (response.ok) {
        setOrders(orders.map(order => 
          order.id === orderId ? { ...order, status: newStatus } : order
        ))
        alert('Status atualizado com sucesso!')
      } else {
        alert('Erro ao atualizar status.')
      }
    } catch (error) {
      alert('Erro de comunicação.')
    }
  }

  if (loading) return <div className="p-8 text-slate-500">A carregar vendas...</div>

  return (
    <div className="p-8">
      <h1 className="text-3xl font-bold text-slate-800 mb-6">Gestão de Vendas</h1>

      <div className="bg-white rounded-lg shadow border border-slate-200 overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Data / ID</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Itens</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Total</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Ação</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {orders.map((order) => (
              <tr key={order.id} className="hover:bg-slate-50">
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="text-sm font-medium text-gray-900">
                    {new Date(order.createdAt).toLocaleDateString('pt-BR')}
                  </div>
                  <div className="text-xs text-gray-500 font-mono">{order.id.split('-')[0]}...</div>
                </td>
                <td className="px-6 py-4 text-sm text-gray-500">
                  <ul className="list-disc list-inside">
                    {order.items.map(item => (
                      <li key={item.id}>{item.quantity}x {item.productNameSnapshot} (Tam: {item.size})</li>
                    ))}
                  </ul>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-medium">
                  R$ {(order.totalAmountCents / 100).toFixed(2).replace('.', ',')}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className="px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">
                    {order.status}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <select 
                    value={order.status}
                    onChange={(e) => handleStatusChange(order.id, e.target.value)}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-1 text-sm"
                  >
                    <option value="PENDING">Pendente</option>
                    <option value="PAID">Pago</option>
                    <option value="SHIPPED">Enviado</option>
                    <option value="DELIVERED">Entregue</option>
                    <option value="CANCELED">Cancelado</option>
                  </select>
                </td>
              </tr>
            ))}
            {orders.length === 0 && (
              <tr>
                <td colSpan={5} className="px-6 py-4 text-center text-sm text-gray-500">Nenhuma venda registada.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}