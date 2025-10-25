import LoadingDefault from "@/components/loading-default";

export default function Loading({text }: {text?: string}) {
  return (
    <div className="flex flex-col justify-center items-center h-screen">
      <LoadingDefault texto={text || "Cargando..."}></LoadingDefault>
    </div>
  )
}

/* 

export default function Loading({text }: {text?: string}) {

  const n = Math.floor(Math.random() * 6) + 1;

  return (
    <div className="flex flex-col justify-center items-center h-screen">
      <div>
        <div className="w-150 h-auto mb-10">
          <span className="text-secondary ml-5">Anuncio</span>
          <img src={`/ads/ads${n}.jpeg`} className="rounded-4xl border-primary border-2"></img>
        </div>
      </div>
      <LoadingDefault texto={text || "Cargando..."}></LoadingDefault>
    </div>
  )
}
  */
