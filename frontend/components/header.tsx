import { UserCircle2Icon } from "lucide-react"
import Link from "next/link"

const userMock = 
  {
    id: 1,
    name: "Lucas Brollo",
    email: "brollo@gmail.com"
  }


export default function HeaderAlquigest({ tituloPagina="", username }: string | any) {
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
            <div className="hidden md:flex flex-col ">
              <Link href={"/login"}>
                <div className="flex items-center space-x-2 cursor-pointer">
                  <UserCircle2Icon className="h-8 w-8 text-gray-800 "/>
                  <p className="text-gray-800 font-bold" >{username}</p>
                </div>
              </Link>
            </div>

          
        </div>
      </div>
    </header>
  )
}