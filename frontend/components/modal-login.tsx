"use client"

import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { useState, FormEvent } from "react"

import { Input } from "./ui/input"
import auth from "@/utils/functions/auth-functions/auth"
import tokenFunctions from "@/utils/functions/auth-functions/token-save"

type ModalDefaultProps = {
  onClose: (username: string) => void
}

export default function ModalLogin({ onClose }: ModalDefaultProps) {
  const [isOpen, setIsOpen] = useState(true)
  const [username, setUsernameInput] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")

  const handleClose = () => setIsOpen(false)


  const handleLogin = async (e: FormEvent) => {
    e.preventDefault()
    try {
      // Aquí llamás tu función de auth que hace login y guarda token
      const user = await auth.login(username, password) // ejemplo, retorna username
      console.log("Token: ", localStorage.getItem("token"))
      setIsOpen(false) // cerrás modal
      onClose(user.username) // pasás username al Home
    } catch (err: any) {
      setError("Usuario o contraseña incorrectos")
    }
  }

  return (
    <Dialog open={isOpen} onOpenChange={handleClose}>
      <DialogContent className="flex flex-col gap-5">
        <DialogHeader>
          <DialogTitle className="text-black font-bold">Inicie Sesión</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleLogin} className="flex flex-col gap-2">
          {error && <p className="text-red-500">{error}</p>}
          <Input
            type="text"
            placeholder="Usuario"
            value={username}
            onChange={(e) => setUsernameInput(e.target.value)}
            className="mb-2"
          />
          <Input
            type="password"
            placeholder="Contraseña"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="mb-4"
          />
          <Button type="submit">Iniciar sesión</Button>
        </form>

        <DialogFooter>
          <Button variant="secondary" onClick={handleClose}>Cancelar</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
