export default function LoadingDefault() {
  return (
    <div className="flex justify-center items-center h-screen">
      <div className="animate-spin rounded-full h-12 w-12 border-t-4 border-primary border-solid"></div>
      <p className="ml-3 text-primary">Cargando...</p>
    </div>
  )
}