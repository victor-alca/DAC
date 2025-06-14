import { Injectable } from '@angular/core';
import { Client } from '../../shared/models/client/client';
import { Employee } from '../../shared/models/employee/employee';

const LS_KEY = "USERS"

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  setCurrentUserData(userData: any) {
    localStorage.setItem(LS_KEY, JSON.stringify(userData));
  }

  removeCurrentUserData() {
    localStorage.removeItem(LS_KEY);
  }

  getCurrentUserData() : Client | Employee | null{
    let ls = localStorage.getItem(LS_KEY)
    console.log(ls)
    if (!ls){
      return null
    }
    let user = JSON.parse(ls)
    if(user.tipo == "CLIENTE"){
      return user.usuario as Client;
  
    }else{
      return user.usuario as Employee
    }
  }

  getAccessToken(){
    let ls = localStorage.getItem(LS_KEY)
    if (ls){
      let user = JSON.parse(ls)
      return user.access_token
    }
  }

  getCurrentUserType(){
    let ls = localStorage.getItem(LS_KEY)
    if (ls){
      let user = JSON.parse(ls)
      return user.tipo
    }
  }

  constructor() { }
}
