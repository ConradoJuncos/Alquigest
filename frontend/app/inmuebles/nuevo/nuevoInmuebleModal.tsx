"use client";
import { Propietario } from "@/types/Propietario";
import BACKEND_URL from "@/utils/backendURL";
import { fetchWithToken } from "@/utils/functions/auth-functions/fetchWithToken";
import { useEffect, useState } from "react";

import { Button } from "@/components/ui/button"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Plus, Save } from "lucide-react"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import auth from "@/utils/functions/auth-functions/auth";
import NuevoPropietarioModal from "@/app/propietarios/nuevoPropietarioModal";
import ModalError from "@/components/modal-error";

type NuevoInmuebleModalProps = {
  text?: string
  disabled?: boolean
  onInmuebleCreado?: (nuevo: any) => void
  open?: boolean
  onOpenChange?: (open: boolean) => void
  showTrigger?: boolean
}

export default function NuevoInmuebleModal(props: NuevoInmuebleModalProps) {
    const { text = "Nuevo Inmueble", onInmuebleCreado, disabled, open, onOpenChange, showTrigger = true } = props;
    const [errorCarga, setErrorCarga] = useState("")
    const [mostrarError, setMostrarError] = useState(false)
    const [loadingCreacion, setLoadingCreacion] = useState(false) // nuevo estado para loading
    const [isNuevoInmuebleOpen, setIsNuevoInmuebleOpen] = useState(false)
    const isControlled = open !== undefined
    const isOpen = isControlled ? !!open : isNuevoInmuebleOpen
    const setOpenSafe = (value: boolean) => {
        if (isControlled) {
            onOpenChange?.(value)
            } else {
            setIsNuevoInmuebleOpen(value)
            }
    }

    //VALIDACIONES DE PERMISOS
    const [puedeCrear, setPuedeCrear] = useState(false)

    useEffect(() => {
        setPuedeCrear(auth.tienePermiso("crear_propietario"));
    }, []);
  
    // Modal de confirmación por dirección duplicada
    const [mostrarConfirmacion, setMostrarConfirmacion] = useState(false)
    const [continuarRegistro, setContinuarRegistro] = useState(false)
  
    // PARA DATOS PROPIETARIOS
    const [propietariosBD, setPropietariosBD] = useState<Propietario[]>([]);
  
    const [formData, setFormData] = useState({
      propietarioId: "",
      direccion: "",
      estado: "1", // valor por defecto
      tipoInmuebleId: "",
      superficie: "",
      esActivo: "true",
      esAlquilado: "false",
    });
  
    // Traer propietarios (solo fetch, sin mutar formData directamente)
    useEffect(() => {
      console.log("Ejecutando fetch de propietarios...");
      fetchWithToken(`${BACKEND_URL}/propietarios/activos`)
        .then((data) => {
          console.log("Datos parseados del backend:", data);
          setPropietariosBD(data);
        })
        .catch((err) => {
          console.error("Error al traer propietarios:", err);
        });
    }, []);
  
    // Mantener esActivo / esAlquilado consistentes cuando cambie estado
    useEffect(() => {
      setFormData(prev => {
        const estado = prev.estado || "1";
        return {
          ...prev,
          esActivo: estado !== "3" ? "true" : "false",
          esAlquilado: estado === "4" ? "true" : "false",
        };
      });
    }, [formData.estado]);
  
      // Verificar dirección antes de crear
    const verificarDireccion = async () => {
      try {
        const params = new URLSearchParams({ direccion: formData.direccion })
        const url = `${BACKEND_URL}/inmuebles/buscar-direccion?${params.toString()}`
        
        const result = await fetchWithToken(url, { method: "GET" })
        console.log("Resultado del verificar: ", result)
  
        // Si el endpoint devuelve algo → existe
        if (result.length > 0) {
          console.log("Llego a la validacion >0")
          setMostrarConfirmacion(true)
          return false
        }
        return true
      } catch (err) {
        console.warn("Error al verificar dirección, continuando...", err)
        return true
      }
    }
  
  
    const handleNewInmueble = async () => {
      setLoadingCreacion(true); // Activar loading
      try {
        const createdInmueble = await fetchWithToken(`${BACKEND_URL}/inmuebles`, {
          method: "POST",
          body: JSON.stringify(formData),
        });
        console.log("Inmueble creado con éxito:", createdInmueble);
  
        // Llamar al callback si existe
        if (onInmuebleCreado) {
          onInmuebleCreado(createdInmueble);
        }
  
        // Limpiar formulario y cerrar modal
        setFormData({
          propietarioId: "",
          direccion: "",
          tipoInmuebleId: "",
          estado: "1",
          superficie: "",
          esActivo: "true",
          esAlquilado: "false",
        });
        setOpenSafe(false);
      } catch (error: any) {
        console.error("Error al crear Inmueble:", error);
        setErrorCarga(error.message || "No se pudo conectar con el servidor");
        setMostrarError(true);
      } finally {
        setLoadingCreacion(false); // Desactivar loading
      }
    };
  
    const handleInputChange = (field: string, value: string) => {
      setFormData((prev) => ({
        ...prev,
        [field]: value,
      }));
    };
  
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
  
        if (!formData.direccion || !formData.tipoInmuebleId || !formData.estado || !formData.propietarioId) {
          setErrorCarga("Por favor, complete todos los campos obligatorios.");
          setMostrarError(true);
          return;
        }
  
        if (!continuarRegistro) {
          const puedeContinuar = await verificarDireccion()
          if (!puedeContinuar) return // se abre modal de confirmación
        }
  
        handleNewInmueble();
        setContinuarRegistro(false) // reset
      };


  return(
     <div>
      <Dialog open={isOpen} onOpenChange={setOpenSafe}>
        {showTrigger && (
          <DialogTrigger asChild>
            <Button 
              type="button"
              disabled={!puedeCrear || disabled}
              onClick={(e) => e.stopPropagation()}
            > 
              <Plus />
              {text}
            </Button>
          </DialogTrigger>
        )}

        <DialogContent className="w-full sm:max-w-2xl">
          <DialogHeader>
            <DialogTitle>Registrar Nuevo Inmueble</DialogTitle>
          </DialogHeader>

          {/* FORMULARIO */}
          <form onSubmit={handleSubmit} className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-2">
                  <Label htmlFor="direccion">Dirección *</Label>
                  <Input
                    id="direccion"
                    placeholder="Ej: Calle Mayor 123, Madrid"
                    value={formData.direccion}
                    onChange={(e) => handleInputChange("direccion", e.target.value)}
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="tipoInmueble">Tipo de Inmueble *</Label>
                  <Select 
                    required
                    value={formData.tipoInmuebleId} 
                    onValueChange={(value) => handleInputChange("tipoInmuebleId", value)}>
                    <SelectTrigger>
                      <SelectValue placeholder="Seleccionar tipo" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="1">Departamento</SelectItem>
                      <SelectItem value="2">Casa</SelectItem>
                      <SelectItem value="3">Local Comercial</SelectItem>
                      <SelectItem value="4">Oficina</SelectItem>
                      <SelectItem value="5">Depósito</SelectItem>
                      <SelectItem value="6">Terreno</SelectItem>
                      <SelectItem value="7">Otro</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="superficie">Superficie (m²)</Label>
                  <Input
                    id="superficie"
                    type="number"
                    min={0}
                    placeholder="Ej: 85"
                    value={formData.superficie}
                    onChange={(e) => handleInputChange("superficie", e.target.value)}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="propietario">Locador *</Label>
                  <div className="flex flex-1 min-w-0 gap-2 ">
                    <Select
                      required
                      value={formData.propietarioId}
                      onValueChange={(value) => handleInputChange("propietarioId", value)}
                    >
                      <SelectTrigger className="w-55">
                        <SelectValue className="overflow-hidden text-ellipsis" placeholder="Seleccionar Locador" />
                      </SelectTrigger>
                      <SelectContent className="">
                        {propietariosBD.map((propietario) => (
                          <SelectItem
                            key={propietario.id}
                            value={propietario.id.toString()}
                            className="overflow-auto text-ellipsis"
                          >
                            {propietario.nombre} {propietario.apellido} | CUIL: {propietario.cuil}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>

                    {/* BOTON PARA ABRIR MODAL NUEVO PROPIETARIO */}
                    <NuevoPropietarioModal 
                      text="" 
                      onPropietarioCreado={(nuevo) => {
                        // agrego a la lista y selecciono el nuevo propietario automáticamente
                        setPropietariosBD(prev => [...prev, nuevo]);
                        setFormData(prev => ({ ...prev, propietarioId: nuevo.id.toString() }));
                        setErrorCarga("");
                        setMostrarError(false);
                      }}
                    /> 
                  </div>
                </div>
              </div>

              {/* Botones */}
              <div className="flex gap-4 pt-6">
                <Button 
                  onClick={() => setOpenSafe(false)} 
                  type="button" 
                  variant="outline" 
                  className="flex-1 bg-transparent"
                  disabled={loadingCreacion}
                >
                  Cancelar
                </Button>
                <Button 
                  type="submit" 
                  className="flex-1"
                  loading={loadingCreacion}
                >
                  <Save className="h-4 w-4 mr-2" />
                  Registrar Inmueble
                </Button>
              </div>
            </form>
        </DialogContent>
      </Dialog>

      {/* Modal de error */}
      {mostrarError && (
        <ModalError
          titulo="Error al crear Propietario"
          mensaje={errorCarga}
          onClose={() => setMostrarError(false)} // Restablecer el estado al cerrar el modal
        />
      )}
    </div>
  )}
