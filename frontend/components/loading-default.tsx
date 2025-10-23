export default function LoadingDefault({texto }: {texto?: string}) {
  return (
    <div className="flex flex-col justify-center items-center h-screen">
      <div className="animate-spin rounded-full h-12 w-12 border-t-4 border-primary border-solid"></div>
      <p className="mt-3 text-primary">{texto || "Cargando..."}</p>
    </div>
  )
}