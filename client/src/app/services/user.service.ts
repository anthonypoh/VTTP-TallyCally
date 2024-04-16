import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { User } from "../models";
import { lastValueFrom } from "rxjs";

@Injectable()
export class UserService {
  http = inject(HttpClient)

  login(email: string, password: string): Promise<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const requestBody = { "email": email, "password": password };
    return lastValueFrom(
      this.http.post<any>('/api/user/login', requestBody, { headers })
    ).then(response => response.msg)
  }

  checkToken(token: string): Promise<User> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const requestBody = { "token": token };
    return lastValueFrom(
      this.http.post<any>('/api/user/check-token', requestBody, { headers }))
  }

  register(user: User): Promise<string> {
    return lastValueFrom(
      this.http.post<any>('/api/user/register', user)).then(response => response.msg)
  }

  updateFcmToken(email: string, token: string): Promise<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const requestBody = { "email": email, "token": token };
    return lastValueFrom(
      this.http.post<any>('/api/user/update-fcm-token', requestBody, { headers })).then(response => response.msg)
  }
}
