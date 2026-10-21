import React from 'react'

interface ProductInfoFormProps {
  formData: {
    categoryId: string
    name: string
    description: string
    material: string
    priceCents: number
  }
  handleChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => void
  handleSubmit: (e: React.FormEvent) => void
  status: string
}

export function ProductInfoForm({ formData, handleChange, handleSubmit, status }: ProductInfoFormProps) {
  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700">Nome da Joia</label>
          <input required type="text" name="name" value={formData.name} onChange={handleChange} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2" />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Categoria</label>
          <select required name="categoryId" value={formData.categoryId} onChange={handleChange} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2">
            <option value="11111111-1111-1111-1111-111111111111">Anéis</option>
            <option value="22222222-2222-2222-2222-222222222222">Colares</option>
            <option value="33333333-3333-3333-3333-333333333333">Brincos</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Descrição</label>
          <textarea required name="description" value={formData.description} onChange={handleChange} rows={3} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2" />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Preço (em cêntimos)</label>
            <input required type="number" name="priceCents" value={formData.priceCents} onChange={handleChange} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Material</label>
            <input required type="text" name="material" value={formData.material} onChange={handleChange} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2" />
          </div>
        </div>
      </div>

      <div className="pt-4 border-t border-slate-200">
        <button type="submit" className="w-full flex justify-center py-3 px-4 border border-transparent rounded-md shadow-sm text-sm font-bold text-white bg-slate-900 hover:bg-slate-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-slate-900">
          Salvar Alterações da Joia
        </button>
        {status && <p className="mt-4 text-center font-medium text-red-500">{status}</p>}
      </div>
    </form>
  )
}