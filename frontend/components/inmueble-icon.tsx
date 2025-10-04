import { TIPOS_INMUEBLES } from "@/utils/constantes"
import { Building2Icon, BuildingIcon, HomeIcon, MapPinned, Store, Warehouse } from "lucide-react"

export default function InmuebleIcon({tipoInmuebleId, className, tipoInmuebleString}: {tipoInmuebleId?: number, className?: string, tipoInmuebleString?: string}){
    
    if(tipoInmuebleString){
        tipoInmuebleId = TIPOS_INMUEBLES.find(tipo => tipo.nombre.toLowerCase() === tipoInmuebleString.toLowerCase())?.id || 1
    }
    
    switch(tipoInmuebleId){
        case 1:
            return <BuildingIcon className={`${className} text-yellow-700`} />
        case 2:
            return <HomeIcon className={`${className}  text-yellow-700`} />
        case 3:
            return <Store className={`${className}  text-yellow-700`} />
        case 4:
            return <Building2Icon className={`${className}  text-yellow-700`} />
        case 5:
            return <Warehouse className={`${className}  text-yellow-700`} />
        case 6:
            return <MapPinned className={`${className}  text-yellow-700`} />
        case 7:
            return <BuildingIcon className={`${className}  text-yellow-700`} />
        default:
            return <BuildingIcon className={`${className} text-yellow-700`} />
    }
}