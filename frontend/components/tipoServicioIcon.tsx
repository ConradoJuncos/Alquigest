import { Droplets, FileText, Flame, Landmark, Zap } from "lucide-react";

export default function TipoServicioIcon({tipoServicio, className} : {tipoServicio: number, className?: string}) {
    switch (tipoServicio) {
      case 1:
        return(
            <Droplets className={`${className} text-sky-500 p-1.5 rounded-full bg-muted`} />
        )
        case 2:
        return(
            <Zap className={`${className} text-yellow-500 p-1.5 rounded-full bg-muted`} />
        )
        case 3:
        return(
            <Flame className={`${className} text-orange-500 p-1.5 rounded-full bg-muted`} />
        )
        case 4:
        return(
            <FileText className={`${className} text-purple-500 p-1.5 rounded-full bg-muted`} />
        )
        case 5:
        return(
            <Landmark className={`${className} text-emerald-500 p-1.5 rounded-full bg-muted`} />
        )

    
    }
}