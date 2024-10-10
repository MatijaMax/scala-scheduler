import { Component, OnInit, ViewChild } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MessageService } from 'src/app/services/message.service';
import { Message } from 'src/app/model/message';
import { LoggedUser } from 'src/app/model/logged-user';
import { Subscription } from 'rxjs';
import { CreateEvent } from 'src/app/model/create-event';
import { EventService } from 'src/app/services/event.service';
import { UserEvent } from 'src/app/model/user-event';
import { UserEventService } from 'src/app/services/userEvent.service';

@Component({
  selector: 'app-event-list',
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.css']
})
export class EventListComponent implements OnInit {
  backupEvents: CreateEvent[] = [];
  viewEvents: CreateEvent[] = [];
  userEvents: UserEvent[] = [];
  currentUser: LoggedUser;
  private subscriptions: Subscription[] = [];
  role? = 0;
  isCreated:boolean = true;
  constructor( private authService : AuthService, private eventService : EventService, private userEventService : UserEventService) {
    this.currentUser = this.authService.getCurrentUser()
    this.loadEvents();
    this.loadUserEvents();
  }

  ngOnInit(): void {
  }

  loadEvents() {
    const subscription = this.eventService.getAllEvents().subscribe(
      (data) => {
        this.backupEvents = data;
        this.viewEvents = this.backupEvents.filter((item) => item.creator === this.currentUser.username);
      },
      (error) => {
        console.error('Error loading events:', error);
      }
    ); 
  
  }

  loadUserEvents() {
    const subscription = this.userEventService.getAllUserEvents().subscribe(
      (data) => {
        this.userEvents = data;
        this.userEvents = data.filter((item) => item.username === this.currentUser.username);
      },
      (error) => {
        console.error('Error loading user events:', error);
      }
    ); 
  }


  toggleIsCreated(){
    this.isCreated = true;
    this.viewEvents = this.backupEvents.filter((item) => item.creator === this.currentUser.username);

  }

  toggleIsJoined(){
    this.isCreated = false;
    console.log(this.userEvents);
    this.viewEvents = this.backupEvents.filter(event => this.userEvents.some(userEvent => userEvent.idEvent === event.id));
  }


  zioReload(id: number){
    this.backupEvents = this.backupEvents.filter(event => event.id !== id);
    this.viewEvents = this.viewEvents.filter(event => event.id !== id);
    this.viewEvents = this.viewEvents = this.viewEvents.filter((item) => item.creator === this.currentUser.username);
  }

  cancelEvent(id: number) {
    console.log(id);
    this.eventService.cancelEvent(id).subscribe(
      (response) => {
        console.log('Event canceled successfully:', response);
        this.zioReload(id);
        this.loadEvents;
      },
      (error) => {
        console.error('Error canceling event:', error);
        this.zioReload(id);
        this.loadEvents;
      }
    );
  }

  ngOnDestroy() {
    this.subscriptions.forEach((subscription) => {
      subscription.unsubscribe();
    });
  }

}
