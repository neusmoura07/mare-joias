import { useState } from 'react'

export function ProductImageUpload({ productId, currentImageUrl }: { productId: string, currentImageUrl: string }) {
  const [imageFile, setImageFile] = useState<File | null>(null)
  const [uploadingImage, setUploadingImage] = useState(false)
  const [status, setStatus] = useState('')

  const handleImageUpload = async () => {
    if (!imageFile) return
    setUploadingImage(true)
    setStatus('A enviar imagem para a nuvem...')
    const token = localStorage.getItem('mare_token')

    const formUpload = new FormData()
    formUpload.append('file', imageFile)

    try {
      const response = await fetch(`http://localhost:8080/api/v1/products/${productId}/image`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` },
        body: formUpload
      })

      if (response.ok) {
        alert('Imagem atualizada com sucesso!')
        window.location.reload()
      } else {
        setStatus('Erro ao fazer upload da imagem.')
      }
    } catch (error) {
      setStatus('Erro de comunicação ao enviar imagem.')
    } finally {
      setUploadingImage(false)
    }
  }

  return (
    <div className="border border-slate-200 rounded-md p-4 bg-slate-50 mt-6">
      <label className="block text-sm font-medium text-gray-700 mb-2">Imagem da Joia</label>
      <div className="flex items-center space-x-6">
        <div className="h-24 w-24 flex-shrink-0 border-2 border-dashed border-gray-300 rounded-md overflow-hidden bg-white flex items-center justify-center">
          {currentImageUrl ? (
            <img src={currentImageUrl} alt="Joia" className="h-full w-full object-cover" />
          ) : (
            <span className="text-xs text-gray-400">Sem foto</span>
          )}
        </div>
        <div className="flex-1">
          <input 
            type="file" 
            accept="image/*"
            onChange={(e) => setImageFile(e.target.files?.[0] || null)}
            className="block w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-yellow-50 file:text-yellow-700 hover:file:bg-yellow-100 mb-3" 
          />
          <button
            type="button" 
            onClick={handleImageUpload}
            disabled={!imageFile || uploadingImage}
            className="bg-slate-200 text-slate-700 px-4 py-2 rounded-md text-sm font-medium hover:bg-slate-300 disabled:opacity-50 transition-colors"
          >
            {uploadingImage ? 'A enviar...' : 'Fazer Upload da Imagem'}
          </button>
          {status && <p className="mt-2 text-sm text-red-500">{status}</p>}
        </div>
      </div>
    </div>
  )
}