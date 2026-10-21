import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/admin/vendas')({
  component: VendasAdmin,
})

function VendasAdmin() {
  return (
    <div className="p-8">
      <h1 className="text-3xl font-bold text-slate-800 mb-6">Gestão de Vendas</h1>
      
      <div className="bg-white rounded-lg shadow border border-slate-200 p-6">
        <p className="text-slate-600 mb-4">Em breve: Aqui você verá a lista de clientes, endereços de entrega e os pedidos gerados no nosso checkout.</p>
        
        {/* Exemplo visual de como será */}
        <div className="border rounded-md overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Pedido</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Cliente</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Total</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              <tr>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">#ORD-1234</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">cliente@teste.com</td>
                <td className="px-6 py-4 whitespace-nowrap"><span className="px-2 py-1 text-xs font-semibold rounded-full bg-yellow-100 text-yellow-800">Processando</span></td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">R$ 129,90</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}