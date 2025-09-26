import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label"


export default function RegistrarNuevoUser(){
    return(
        <div className="min-h-screen bg-background">
            <main className="container mx-auto px-6 py-8 pt-30">
                <h1 className="text-3xl font-bold mb-4">Crear Nuevo Usuario</h1>
                <Card className="max-w-4xl mx-auto">
                    <CardHeader>
                        <CardTitle className="font-sans">Complete con sus datos</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <form>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div className="space-y-2">
                                <Label htmlFor="direccion">Nombre de Usuario</Label>
                                <Input
                                    id="direccion"
                                    placeholder="Ingrese un nombre de usuario"
                                    required
                                />
                                </div>
                            </div>
                        </form>
                    </CardContent>
                </Card>
            </main>
        </div>
    )
}