import Link from "next/link"
import PildoraUsuario from "./user-pill"

export default function HeaderAlquigest({ tituloPagina="", username, toggleTheme, isDarkMode }: string | any) {
  const urlLogoAlquigest = isDarkMode? "/alquigest-white.png" : "/alquigest-dark.png"
  const gradientVar = "bg-gradient-to-l from-yellow-300 via-[var(--amarillo-alqui)] to-[var(--background)] animate-gradient-x"
  const gradientAlqui = "bg-gradient-to-l from-[var(--amarillo-alqui)] to-[var(--background)]"

  return (
    <header className={`border-b border-border ${gradientVar} shadow-lg fixed w-full p-1 z-40`}>
      <div className="container mx-auto px-6 py-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <Link href="/" className="flex items-center space-x-3">
              <div className="flex items-center space-x-2 ">
                <img src={urlLogoAlquigest} className="p-2 h-10 md:h-12 contrast-70"/>
                <p className="text-lg md:text-xl text-muted-foreground mt-1">| {tituloPagina}</p>
              </div>
            </Link>
          </div>
          <div className="">
            <PildoraUsuario
              username={username}
              isDarkMode={isDarkMode}
              toggleTheme={toggleTheme}
            />
          </div>

          
        </div>
      </div>
    </header>
  )
}