import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../env/environment';
import { Message } from '../model/message'
import { CreateEvent } from '../model/create-event';
import { UserEvent } from '../model/user-event';

@Injectable({
    providedIn: 'root'
  })
  export class UserEventService {
    
    constructor(private http: HttpClient) { }
  
    createUserEvent(event: UserEvent): Observable<UserEvent> {
        return this.http.post<UserEvent>(
            environment.apiHost + 'v2/event/user',
            event
        );   
    }

    getAllUserEvents(): Observable<UserEvent[]> {
      return this.http.get<any>(environment.apiHost + 'v2/event/user');
    }
   
  }
