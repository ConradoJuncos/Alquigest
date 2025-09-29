import { Moon, Sun, UserCircle2Icon } from "lucide-react"
import Link from "next/link"

const userMock = 
  {
    id: 1,
    name: "Lucas Brollo",
    email: "brollo@gmail.com"
  }


export default function HeaderAlquigest({ tituloPagina="", username, toggleTheme, isDarkMode }: string | any) {
  const urlLogoAlquigest = isDarkMode? "/alquigest-white.png" : "/alquigest-dark.png"

  return (
    <header className="border-b border-border bg-gradient-to-l from-[var(--amarillo-alqui)] to-[var(--background)] shadow-lg fixed w-full p-1 z-90">
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
          <div className="flex items-center gap-2">
            <div className="hidden md:flex flex-col p-1 rounded-4xl bg-muted text-primary hover:bg-accent hover:text-muted transition">
              <Link href={"/login"}>
                <div className="flex items-center space-x-2 cursor-pointer">
                  <UserCircle2Icon className="h-8 w-8 rounded-full"/>
                  <p className=" font-bold pr-2" >{username}</p>
                </div>
              </Link>
            </div>

            <div>
              <button
              onClick={toggleTheme}
              className="p-2 rounded-full bg-muted hover:bg-muted-foreground transition">
              {!isDarkMode ? <Sun className="h-5 w-5 text-yellow-500" /> : <Moon className="h-5 w-5 text-yellow-500" />}
            </button>
            </div>
          </div>

          
        </div>
      </div>
    </header>
  )
}