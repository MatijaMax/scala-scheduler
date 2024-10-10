import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../env/environment';
import { Message } from '../model/message'
import { CreateEvent } from '../model/create-event';

@Injectable({
    providedIn: 'root'
  })
  export class EventService {
    
    constructor(private http: HttpClient) { }
  
    createEvent(event: CreateEvent): Observable<CreateEvent> {
        return this.http.post<CreateEvent>(
            environment.apiHost + 'v2/events',
            event
        );   
    }

    getAllEvents(): Observable<CreateEvent[]> {
      return this.http.get<any>(environment.apiHost + 'v2/events');
    }

    cancelEvent(id: number): Observable<any[]> {
      return this.http.delete<any[]>(
        environment.apiHost + 'v3/ZIO/events/' + id
      );
    }
   
  }
