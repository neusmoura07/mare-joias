import { createFileRoute, useNavigate } from '@tanstack/react-router'
import { useState } from 'react'

export const Route = createFileRoute('/admin/produtos/novo')({
  component: NovoProdutoAdmin,
})

function NovoProdutoAdmin() {
  const navigate = useNavigate() // Adicionado para controlar o redirecionamento
  
  const [formData, setFormData] = useState({
    categoryId: '11111111-1111-1111-1111-111111111111', // Categoria Anéis por defeito
    sku: '',
    name: '',
    slug: '',
    description: '',
    material: 'Prata 925',
    priceCents: 0,
    stockQuantity: 0,
    imageUrl: '', // Vai vazio, a imagem será adicionada no passo 2
  })

  const [status, setStatus] = useState('')

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'priceCents' || name === 'stockQuantity' ? Number(value) : value,
    }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setStatus('A criar joia...')

    const token = localStorage.getItem('mare_token')

    try {
      const response = await fetch('http://localhost:8080/api/v1/products', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(formData)
      })

      if (response.ok) {
        // Captura o produto recém-criado devolvido pelo Spring Boot
        const produtoCriado = await response.json()
        
        alert('Joia criada! A redirecionar para adicionar imagem e tamanhos...')
        
        // Redireciona automaticamente para a tela de edição usando o novo ID
        navigate({ 
          to: '/admin/produtos/$id/editar', 
          params: { id: produtoCriado.id } 
        })
      } else {
        const errorData = await response.json()
        setStatus(`Erro: ${errorData.message || 'Falha ao guardar'}`)
      }
    } catch (error) {
      setStatus('Erro de ligação ao servidor.')
    }
  }

  return (
    <div className="max-w-2xl mx-auto p-6 bg-white rounded-lg shadow-md mt-10">
      <h1 className="text-2xl font-bold mb-2 text-slate-800">Adicionar Nova Joia</h1>
      <p className="text-sm text-slate-500 mb-6">
        Passo 1 de 2: Preencha as informações básicas. Poderá fazer o upload da foto e adicionar a grelha de tamanhos no próximo ecrã.
      </p>
      
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Nome da Joia</label>
            <input required type="text" name="name" value={formData.name} onChange={handleChange} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">SKU</label>
            <input required type="text" name="sku" value={formData.sku} onChange={handleChange} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2" />
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Slug (URL amigável)</label>
            <input required type="text" name="slug" value={formData.slug} onChange={handleChange} placeholder="anel-sol-lua" className="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Categoria</label>
            <select name="categoryId" value={formData.categoryId} onChange={handleChange} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2">
              <option value="11111111-1111-1111-1111-111111111111">Anéis</option>
              <option value="22222222-2222-2222-2222-222222222222">Colares</option>
              <option value="33333333-3333-3333-3333-333333333333">Brincos</option>
            </select>
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Descrição</label>
          <textarea required name="description" value={formData.description} onChange={handleChange} rows={3} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2" />
        </div>

        <div className="grid grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Preço (em cêntimos)</label>
            <input required type="number" name="priceCents" value={formData.priceCents} onChange={handleChange} placeholder="12990" className="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Quantidade Inicial</label>
            <input required type="number" name="stockQuantity" value={formData.stockQuantity} onChange={handleChange} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Material</label>
            <input required type="text" name="material" value={formData.material} onChange={handleChange} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2" />
          </div>
        </div>

        <div className="pt-4 border-t border-slate-200">
          <button type="submit" className="w-full flex justify-center py-3 px-4 border border-transparent rounded-md shadow-sm text-sm font-bold text-white bg-slate-900 hover:bg-slate-800">
            Criar Joia e Avançar
          </button>
        </div>

        {status && <p className="mt-4 text-center font-medium text-slate-700">{status}</p>}
      </form>
    </div>
  )
}