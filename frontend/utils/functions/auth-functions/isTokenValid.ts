import { jwtDecode } from 'jwt-decode'

type JwtPayload = {
  exp: number
}
export function isTokenValid(token: string): boolean {
  try {
    const decoded = jwtDecode<JwtPayload>(token)
    if (decoded.exp) {
      const currentTime = Math.floor(Date.now() / 1000) // Tiempo actual en segundos
      return decoded.exp > currentTime // El token es válido si la fecha de expiración es mayor que el tiempo actual
    }
    return false // Si no hay campo exp, consideramos el token inválido
  } catch (error) {
    console.error('Error al decodificar el token:', error)
    return false // Si ocurre un error al decodificar, consideramos el token inválido
  }
}