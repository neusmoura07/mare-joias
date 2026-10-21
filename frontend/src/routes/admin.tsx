import { createFileRoute, Link, Outlet, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/admin')({
  beforeLoad: () => {
    // Trava de segurança para o Server-Side Rendering (SSR)
    if (typeof window !== 'undefined') {
      const token = localStorage.getItem('mare_token')
      if (!token) {
        throw redirect({ to: '/' })
      }
    }
  },
  component: AdminLayout,
})

function AdminLayout() {
  return (
    <div className="flex min-h-screen bg-slate-50">
      {/* Menu Lateral (Sidebar) */}
      <aside className="w-64 bg-slate-900 text-white flex flex-col">
        <div className="p-6">
          <h2 className="text-2xl font-bold text-yellow-500 tracking-widest">MARÉ</h2>
          <p className="text-slate-400 text-sm">Painel Administrativo</p>
        </div>
        
        <nav className="flex-1 px-4 space-y-2 mt-4">
          <Link to="/admin" className="block px-4 py-2 rounded-md hover:bg-slate-800 [&.active]:bg-slate-800 [&.active]:text-yellow-500 transition-colors">
            Dashboard
          </Link>
          <Link to="/admin/vendas" className="block px-4 py-2 rounded-md hover:bg-slate-800 [&.active]:bg-slate-800 [&.active]:text-yellow-500 transition-colors">
            Gestão de Vendas
          </Link>
          <div className="pt-4 pb-2">
            <p className="px-4 text-xs font-semibold text-slate-400 uppercase tracking-wider">Catálogo</p>
          </div>
          <Link to="/admin/produtos" className="block px-4 py-2 rounded-md hover:bg-slate-800 [&.active]:bg-slate-800 [&.active]:text-yellow-500 transition-colors">
            Produtos
            </Link>
        </nav>

        <div className="p-4">
          <button 
            onClick={() => {
              localStorage.removeItem('mare_token')
              window.location.href = '/'
            }}
            className="w-full text-left px-4 py-2 text-slate-400 hover:text-red-400 transition-colors"
          >
            Sair do sistema
          </button>
        </div>
      </aside>

      {/* Área Central Dinâmica (Onde as telas aparecem) */}
      <main className="flex-1 overflow-y-auto">
        <Outlet /> 
      </main>
    </div>
  )
}