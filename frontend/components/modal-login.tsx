"use client"

import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { useState, FormEvent } from "react"
import { Input } from "./ui/input"
import auth from "@/utils/functions/auth-functions/auth"
import { DialogDescription } from "@radix-ui/react-dialog"
import Link from "next/link"

type ModalDefaultProps = {
  onClose: (username: string) => void,
  isDarkMode: Boolean
}

export default function ModalLogin({ onClose, isDarkMode}: ModalDefaultProps) {
  const [isOpen, setIsOpen] = useState(true)
  const [username, setUsernameInput] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")

  const urlLogoAlquigest = isDarkMode? "/alquigest-white.png" : "/alquigest-dark.png"

  const handleClose = () => {
    setIsOpen(false)
    setError("") // Limpia el mensaje de error al cerrar el modal
  }

  const handleLogin = async (e: FormEvent) => {
    e.preventDefault()
    try {
      // Llamar a la función de autenticación
      const user = await auth.login(username, password) // Ejemplo, retorna username
      console.log("Token: ", localStorage.getItem("token"))
      setIsOpen(false) // Cerrar el modal
      onClose(user.username) // Pasar el username al componente padre
      // Refrescar la página para actualizar los permisos
      window.location.reload()
    } catch (err: any) {
      setError("Usuario o contraseña incorrectos")
    }
  }

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogContent className="flex flex-col gap-12">
        <DialogHeader className="flex flex-col justify-center items-center">
          <img src={urlLogoAlquigest} className="h-7 object-contain md:h-12"></img>
          <DialogTitle className="text-foreground font-bold text-2xl">¡Bienvenido!</DialogTitle>
          <DialogDescription className="text-lg">Inicie Sesión para continuar</DialogDescription>
        </DialogHeader>

        <form onSubmit={handleLogin} className="flex flex-col gap-3">
          <Input
            type="text"
            required
            placeholder="Usuario"
            value={username}
            onChange={(e) => setUsernameInput(e.target.value)}
            className="mb-2"
          />
          <Input 
            type="password"
            required
            placeholder="Contraseña"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="mb-4 "
          />
          {error && <p className="text-red-500">{error}</p>}
          <Button
            type="submit"
            onClick={() => setError("")} // Limpia el error al hacer clic
            className="bg-accent hover:bg-accent/80 text-gray-900 text-md"
          >
            Iniciar sesión
          </Button>
        </form>

        <div className="flex items-center justify-center">
          <Link href={"/404"}>
            <p className="hover:text-amber-900">¿Olvidó su Contraseña?</p>
          </Link>
        </div>
      </DialogContent>
    </Dialog>
  )
}