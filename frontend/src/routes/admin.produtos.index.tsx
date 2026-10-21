import { createFileRoute, Link } from '@tanstack/react-router'
import { useEffect, useState } from 'react'
import { ProductTable, Product } from '../components/admin/ProductTable'

export const Route = createFileRoute('/admin/produtos/')({
  component: ListaProdutosAdmin,
})

function ListaProdutosAdmin() {
  const [produtos, setProdutos] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchProdutos = async () => {
      try {
        const token = localStorage.getItem('mare_token')
        const response = await fetch('http://localhost:8080/api/v1/products/todos', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })
        if (response.ok) {
          const data = await response.json()
          setProdutos(data.content || data)
        }
      } catch (error) {
        console.error('Erro ao buscar produtos:', error)
      } finally {
        setLoading(false)
      }
    }

    fetchProdutos()
  }, [])

  const handleDeactivate = async (productId: string) => {
    if (!window.confirm('Tem a certeza que deseja inativar esta joia? Ela deixará de aparecer na loja.')) return
    const token = localStorage.getItem('mare_token')
    
    try {
      const response = await fetch(`http://localhost:8080/api/v1/products/${productId}/deactivate`, {
        method: 'PATCH',
        headers: { 'Authorization': `Bearer ${token}` }
      })

      if (response.ok) {
        setProdutos(produtos.map(p => p.id === productId ? { ...p, isActive: false } : p))
      } else {
        alert('Erro ao inativar a joia.')
      }
    } catch (error) {
      alert('Erro de comunicação com o servidor.')
    }
  }

  const handleActivate = async (productId: string) => {
    const token = localStorage.getItem('mare_token')
    
    try {
      const response = await fetch(`http://localhost:8080/api/v1/products/${productId}/activate`, {
        method: 'PATCH',
        headers: { 'Authorization': `Bearer ${token}` }
      })

      if (response.ok) {
        setProdutos(produtos.map(p => p.id === productId ? { ...p, isActive: true } : p))
      } else {
        alert('Erro ao ativar a joia.')
      }
    } catch (error) {
      alert('Erro de comunicação com o servidor.')
    }
  }

  return (
    <div className="p-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-slate-800">Catálogo de Joias</h1>
        <Link 
          to="/admin/produtos/novo" 
          className="bg-slate-900 text-white px-4 py-2 rounded-md hover:bg-slate-800 transition-colors"
        >
          + Nova Joia
        </Link>
      </div>

      {/* O Componente Isolado */}
      <ProductTable 
        produtos={produtos} 
        loading={loading} 
        onActivate={handleActivate} 
        onDeactivate={handleDeactivate} 
      />
    </div>
  )
}