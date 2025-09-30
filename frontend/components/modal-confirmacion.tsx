"use client"

import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"

type ModalConfirmacionProps = {
  open: boolean
  titulo: string
  mensaje: string
  onConfirm: () => void
  onCancel: () => void
}

export default function ModalConfirmacion({
  open,
  titulo,
  mensaje,
  onConfirm,
  onCancel,
}: ModalConfirmacionProps) {
  return (
    <Dialog open={open} onOpenChange={onCancel}>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>{titulo}</DialogTitle>
          <DialogDescription>{mensaje}</DialogDescription>
        </DialogHeader>

        <div className="flex gap-4 pt-4">
          <Button variant="outline" className="flex-1" onClick={onCancel}>
            No
          </Button>
          <Button className="flex-1" onClick={onConfirm}>
            Sí
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  )
}
