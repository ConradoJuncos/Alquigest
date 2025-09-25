'use client'

import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { useState } from "react"

type ModalDefaultProps = {
  onClose: () => void
}

export default function ModalLogin({ onClose }: ModalDefaultProps) {
  const [isOpen, setIsOpen] = useState(true)

  const handleClose = () => {
    setIsOpen(false)
    onClose() // Llamar a la función onClose pasada como prop
  }

  return (
    <Dialog open={isOpen} onOpenChange={handleClose}>
      <DialogContent className="flex flex-col gap-5">
        <DialogHeader>
          <DialogTitle className="text-black font-bold">Inicie Sesión</DialogTitle>
        </DialogHeader>
        <p className="font-sans">Inicie sesion</p>
        <DialogFooter>
          <Button onClick={handleClose}>Aceptar</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}