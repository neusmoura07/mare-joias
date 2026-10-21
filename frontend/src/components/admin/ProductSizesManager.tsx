import { useState, useEffect } from 'react'

interface Size {
  id: string
  sizeName: string
  stockQuantity: number
}

export function ProductSizesManager({ productId, initialSizes }: { productId: string, initialSizes: Size[] }) {
  const [sizes, setSizes] = useState<Size[]>([])
  const [newSizeName, setNewSizeName] = useState('')
  const [newSizeStock, setNewSizeStock] = useState(0)

  // Atualiza o estado local caso initialSizes mude
  useEffect(() => setSizes(initialSizes || []), [initialSizes])

  const handleAddSize = async () => {
    if (!newSizeName.trim()) return
    const token = localStorage.getItem('mare_token')

    try {
      const response = await fetch(`http://localhost:8080/api/v1/products/${productId}/sizes`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
        body: JSON.stringify({ sizeName: newSizeName, stockQuantity: newSizeStock })
      })

      if (response.ok) {
        window.location.reload()
      } else {
        alert('Erro ao adicionar tamanho.')
      }
    } catch (error) {
      alert('Erro de comunicação.')
    }
  }

  const handleUpdateStock = async (sizeId: string, currentStock: number) => {
    const newStock = window.prompt('Digite a nova quantidade em estoque:', currentStock.toString())
    if (newStock === null || isNaN(Number(newStock))) return

    const token = localStorage.getItem('mare_token')
    try {
      const response = await fetch(`http://localhost:8080/api/v1/products/${productId}/sizes/${sizeId}/stock`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
        body: JSON.stringify({ stockQuantity: Number(newStock) })
      })

      if (response.ok) {
        setSizes(sizes.map(s => s.id === sizeId ? { ...s, stockQuantity: Number(newStock) } : s))
      } else {
        alert('Erro ao atualizar estoque.')
      }
    } catch (error) {
      alert('Erro de comunicação.')
    }
  }

  return (
    <div className="border border-slate-200 rounded-md p-4 bg-slate-50 mt-6 mb-6">
      <h3 className="block text-sm font-medium text-gray-700 mb-4">Gestão de Tamanhos e Estoque</h3>
      <div className="flex gap-3 mb-6">
        <div className="flex-1">
          <input type="text" placeholder="Ex: 14, 16, Único..." value={newSizeName} onChange={e => setNewSizeName(e.target.value)} className="w-full rounded-md border-gray-300 shadow-sm border p-2 text-sm" />
        </div>
        <div className="w-24">
          <input type="number" min="0" placeholder="Qtd" value={newSizeStock} onChange={e => setNewSizeStock(Number(e.target.value))} className="w-full rounded-md border-gray-300 shadow-sm border p-2 text-sm" />
        </div>
        <button type="button" onClick={handleAddSize} className="bg-slate-900 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-slate-800 transition-colors">Adicionar</button>
      </div>

      {sizes.length > 0 ? (
        <div className="border rounded-md overflow-hidden bg-white">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Tamanho</th>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Em Estoque</th>
                <th className="px-4 py-2 text-right text-xs font-medium text-gray-500 uppercase">Ação</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {sizes.map((size) => (
                <tr key={size.id}>
                  <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-900 font-medium">{size.sizeName}</td>
                  <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-500">
                    <span className={`px-2 py-1 rounded-full text-xs font-semibold ${size.stockQuantity > 0 ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                      {size.stockQuantity} un.
                    </span>
                  </td>
                  <td className="px-4 py-2 whitespace-nowrap text-right text-sm font-medium">
                    <button type="button" onClick={() => handleUpdateStock(size.id, size.stockQuantity)} className="text-indigo-600 hover:text-indigo-900">Ajustar Estoque</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        <p className="text-sm text-gray-500 text-center py-2">Nenhum tamanho cadastrado para esta joia.</p>
      )}
    </div>
  )
}