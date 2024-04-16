import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { firstValueFrom } from "rxjs";

@Injectable()
export class RouteService {
  http = inject(HttpClient)

  loggedIn(username: string): Promise<any> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json; charset=utf-8');
    return firstValueFrom(this.http.post<any>('/api/user/loggedIn', username, { headers }))
  }
}
