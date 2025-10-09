import { Badge } from "../ui/badge"

 export default function EstadoBadge({estado}: {estado: string}) {
    switch (estado) {
      case "Vigente":
        return <Badge className="bg-green-100 text-green-950 hover:bg-green-100 font-bold">Vigente</Badge>
      case "Por Renovar":
        return <Badge className="bg-orange-100 text-orange-800 hover:bg-orange-100 font-bold">Por Renovar</Badge>
      case "No Vigente":
        return <Badge className="font-bold" variant="secondary">No Vigente</Badge>
      case "Cancelado":
        return <Badge className="bg-red-300 text-gray-800 hover:bg-red-400 font-bold">Rescindido</Badge>
      default:
        return <Badge className="font-bold" variant="secondary">{estado}</Badge>
    }
  }