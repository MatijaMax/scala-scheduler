import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../env/environment';
import { Message } from '../model/message'

@Injectable({
    providedIn: 'root'
  })
  export class MessageService {
    
    constructor(private http: HttpClient) { }
  
    createMessage(message: Message): Observable<Message> {
        return this.http.post<Message>(
            environment.apiHost + 'v1/messages',
            message
        );
    
    }

    getAllMessages(): Observable<Message[]> {
        return this.http.get<any>(environment.apiHost + 'v1/messages');
      }

    getAllMessagesExclusive(username: String): Observable<Message[]> {
        return this.http.get<any>(environment.apiHost + 'v1/messages/exclusive/' + username);
      }

    updateMessage(id: number, message: Message): Observable<Message> {
        return this.http.put<Message>(
          environment.apiHost + 'v1/messages/' + id,
          message
        );
      }

    
  }
