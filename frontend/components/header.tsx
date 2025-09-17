import Link from "next/link"

export default function HeaderAlquigest({ tituloPagina }: string | any) {
  return (
    <header className="border-b border-border bg-gradient-to-l from-[var(--amarillo-alqui)] to-white shadow-lg fixed w-full px-4">
      <div className="container mx-auto px-6 py-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <Link href="/" className="flex items-center space-x-3">
              <div className="flex items-center space-x-3 ">
                <img src="/alquigest-transparente.png" className="h-10 md:h-15"/>
                <p className="text-lg md:text-xl text-muted-foreground">| {tituloPagina}</p>
              </div>
            </Link>
          </div>
          
        </div>
      </div>
    </header>
  )
}