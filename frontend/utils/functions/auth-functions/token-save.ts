import { isTokenValid } from "./isTokenValid"

function isTokenSave(){
    if(localStorage.getItem('token')){
        return true
    }else{
        return false
    }
}

function saveToken(token: string){
    localStorage.setItem('token', token)
}

function removeToken(){
    localStorage.removeItem('token')
}

function getToken(){
    return localStorage.getItem('token')
}



export default {
    isTokenSave,
    saveToken,
    removeToken,
    getToken,
}
