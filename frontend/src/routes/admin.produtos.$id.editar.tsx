import { createFileRoute, Link, useNavigate } from '@tanstack/react-router'
import { useEffect, useState } from 'react'
import { ProductInfoForm } from '../components/admin/ProductInfoForm'
import { ProductImageUpload } from '../components/admin/ProductImageUpload'
import { ProductSizesManager } from '../components/admin/ProductSizesManager'

export const Route = createFileRoute('/admin/produtos/$id/editar')({
  component: EditarProdutoAdmin,
})

function EditarProdutoAdmin() {
  const { id } = Route.useParams()
  const navigate = useNavigate()

  const [loading, setLoading] = useState(true)
  const [status, setStatus] = useState('')
  const [initialSizes, setInitialSizes] = useState([])
  const [formData, setFormData] = useState({
    categoryId: '11111111-1111-1111-1111-111111111111', 
    name: '',
    description: '',
    material: '',
    priceCents: 0,
    imageUrl: '', // Mantemos aqui apenas para repassar ao componente de imagem
  })

  // 1. Carregar Dados da Joia
  useEffect(() => {
    const fetchProduto = async () => {
      const token = localStorage.getItem('mare_token')
      try {
        const response = await fetch(`http://localhost:8080/api/v1/products/id/${id}`, {
          headers: { 'Authorization': `Bearer ${token}` }
        })
        if (response.ok) {
          const data = await response.json()
          setFormData({
            categoryId: data.categoryId, 
            name: data.name,
            description: data.description || '',
            material: data.material || '',
            priceCents: data.priceCents,
            imageUrl: data.imageUrl || '',
          })
          setInitialSizes(data.sizes || [])
        }
      } catch (error) {
        setStatus('Erro de conexão.')
      } finally {
        setLoading(false)
      }
    }
    fetchProduto()
  }, [id])

  // 2. Manipuladores do Formulário Principal
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'priceCents' ? Number(value) : value,
    }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setStatus('A guardar alterações...')
    const token = localStorage.getItem('mare_token')

    try {
      const response = await fetch(`http://localhost:8080/api/v1/products/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(formData)
      })

      if (response.ok) {
        alert('Joia atualizada com sucesso!')
        navigate({ to: '/admin/produtos' }) 
      } else {
        const errorData = await response.json()
        setStatus(`Erro: ${errorData.message || 'Falha ao atualizar'}`)
      }
    } catch (error) {
      setStatus('Erro no servidor.')
    }
  }

  if (loading) return <div className="p-8 text-slate-500">A carregar dados da joia...</div>

  return (
    <div className="max-w-2xl mx-auto p-6 bg-white rounded-lg shadow-md mt-10 mb-10">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-slate-800">Editar Joia</h1>
        <Link to="/admin/produtos" className="text-sm text-slate-500 hover:text-slate-800">
          Voltar para Catálogo
        </Link>
      </div>
      
      {/* 1. Formulário de Informações Principais */}
      <ProductInfoForm 
        formData={formData} 
        handleChange={handleChange} 
        handleSubmit={handleSubmit} 
        status={status} 
      />

      {/* 2. Componente Isolado de Imagem */}
      <ProductImageUpload 
        productId={id} 
        currentImageUrl={formData.imageUrl} 
      />

      {/* 3. Componente Isolado de Tamanhos e Estoque */}
      <ProductSizesManager 
        productId={id} 
        initialSizes={initialSizes} 
      />
    </div>
  )
}