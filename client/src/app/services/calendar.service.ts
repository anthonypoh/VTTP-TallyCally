import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Event, GroupEvent, Holiday } from "../models";
import { lastValueFrom } from "rxjs";

@Injectable()
export class CalendarService {
  http = inject(HttpClient)

  getUserGroups(email: string): Promise<Array<string>> {
    const params = new HttpParams()
      .set("email", email)
    return lastValueFrom(
      this.http.put<Array<string>>('/api/calendar/get-user-groups', { params })
    )
  }

  createGroup(email: string, groupName: string): Promise<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const requestBody = { "email": email, "groupName": groupName };
    return lastValueFrom(
      this.http.post<any>('/api/calendar/create-group', requestBody, { headers })
    ).then(response => { return response.msg })
  }

  createEvent(event: Event, group_id: string): Promise<string> {
    return lastValueFrom(
      this.http.put<any>(`/api/calendar/create-event/${group_id}`, event)
    ).then(response => { return response.msg })
  }

  editEvent(event: Event, group_id: string): Promise<string> {
    return lastValueFrom(
      this.http.put<any>(`/api/calendar/edit-event/${group_id}`, event)
    ).then(response => { return response.msg })
  }

  deleteEvent(_id: string, group_id: string): Promise<string> {
    return lastValueFrom(
      this.http.delete<any>(`/api/calendar/delete-event/${group_id}/${_id}`)
    ).then(response => { return response.msg })
  }

  getGroupEvents(email: string, token: string): Promise<GroupEvent[]> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const requestBody = { "email": email, "token": token };
    return lastValueFrom(
      this.http.post<any>('/api/calendar/get-group-events', requestBody, { headers })
    )
  }

  generateInviteLink(group_id: string): Promise<string> {
    const params = new HttpParams()
      .set("group_id", group_id)
    return lastValueFrom(
      this.http.get<any>('/api/calendar/generate-invite-link', { params })
    ).then(response => { return response.msg })
  }

  joinGroup(group_id: string, email: string) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const requestBody = { "email": email };
    return lastValueFrom(
      this.http.post<any>(`/api/calendar/join-group/${group_id}`, requestBody, { headers })
    ).then(response => { return response.msg })
  }

  getHolidays(country: string): Promise<Holiday[]> {
    return lastValueFrom(
      this.http.get<any>(`https://date.nager.at/api/v3/NextPublicHolidays/${country}`,)
    )
  }

  // getCalendar(start: string, end: string): Promise<Array<Day>> {
  //   const params = new HttpParams()
  //     .set("start", start)
  //     .set("end", end)
  //   return lastValueFrom(
  //     this.http.get<Array<Day>>('/api/calendar', { params })
  //   )
  // }

  // getGoogleEvents(): Promise<any> {
  //   return lastValueFrom(this.http.get<any>('/api/events'))
  // }
}