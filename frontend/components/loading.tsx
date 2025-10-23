import LoadingDefault from "@/components/loading-default";

export default function Loading({text }: {text?: string}) {
  return (
    <LoadingDefault texto={text || "Cargando..."}></LoadingDefault>
  )
}
