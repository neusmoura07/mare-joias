import { Link } from '@tanstack/react-router'

export interface Product {
  id: string;
  name: string;
  sku: string;
  priceCents: number;
  stockQuantity: number;
  imageUrl: string;
  isActive: boolean;
  category: { name: string };
}

interface ProductTableProps {
  produtos: Product[];
  loading: boolean;
  onActivate: (id: string) => void;
  onDeactivate: (id: string) => void;
}

export function ProductTable({ produtos, loading, onActivate, onDeactivate }: ProductTableProps) {
  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow border border-slate-200 overflow-hidden p-6">
        <p className="text-slate-500">A carregar catálogo...</p>
      </div>
    )
  }

  return (
    <div className="bg-white rounded-lg shadow border border-slate-200 overflow-hidden">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50">
          <tr>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Joia</th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">SKU</th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Categoria</th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Preço</th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
            <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Ações</th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {produtos.map((produto) => (
            <tr key={produto.id} className="hover:bg-slate-50">
              <td className="px-6 py-4 whitespace-nowrap">
                <div className="flex items-center">
                  <div className="h-10 w-10 flex-shrink-0">
                    <img className="h-10 w-10 rounded-full object-cover border" src={produto.imageUrl || 'https://via.placeholder.com/40'} alt="" />
                  </div>
                  <div className="ml-4">
                    <div className="text-sm font-medium text-gray-900">{produto.name}</div>
                    <div className={`text-sm font-medium ${produto.stockQuantity > 0 ? 'text-gray-500' : 'text-red-500'}`}>
                      Estoque Total: {produto.stockQuantity}
                    </div>
                  </div>
                </div>
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{produto.sku}</td>
              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{produto?.category?.name || 'Sem categoria'}</td>
              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                R$ {(produto.priceCents / 100).toFixed(2).replace('.', ',')}
              </td>
              <td className="px-6 py-4 whitespace-nowrap">
                <span className={`px-2 py-1 text-xs font-semibold rounded-full ${produto.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                  {produto.isActive ? 'Ativo' : 'Inativo'}
                </span>
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-3">
                <Link to={`/admin/produtos/${produto.id}/editar`} className="text-indigo-600 hover:text-indigo-900">
                  Editar
                </Link>
                {produto.isActive ? (
                  <button onClick={() => onDeactivate(produto.id)} className="text-red-600 hover:text-red-900">
                    Inativar
                  </button>
                ) : (
                  <button onClick={() => onActivate(produto.id)} className="text-green-600 hover:text-green-900">
                    Ativar
                  </button>
                )}
              </td>
            </tr>
          ))}
          {produtos.length === 0 && (
            <tr>
              <td colSpan={6} className="px-6 py-4 text-center text-sm text-gray-500">Nenhuma joia encontrada.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  )
}