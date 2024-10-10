import { Component, OnInit, ViewChild } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MessageService } from 'src/app/services/message.service';
import { Message } from 'src/app/model/message';
import { LoggedUser } from 'src/app/model/logged-user';
import { Subscription } from 'rxjs';
import { CreateEvent } from 'src/app/model/create-event';
import { EventService } from 'src/app/services/event.service';

@Component({
  selector: 'app-event-creation',
  templateUrl: './event-creation.component.html',
  styleUrls: ['./event-creation.component.css']
})
export class EventCreationComponent implements OnInit {
  hide = true
  items: Message[] = [];
  errorDescription: string = ''
  currentUser: LoggedUser;
  private subscriptions: Subscription[] = [];
  registerForm = new FormGroup({
    id: new FormControl(0),
    name: new FormControl('', [Validators.required]),
    description: new FormControl('', [Validators.required]),
    creator: new FormControl(''),
    startDateTime: new FormControl('', [Validators.required]),  
    duration: new FormControl('', [Validators.required]),
  });

  minDate: Date = new Date();

  constructor(private authService: AuthService, private eventService: EventService){
    
    this.currentUser = this.authService.getCurrentUser()
    console.log(this.currentUser);
  }

  ngOnInit(): void {
  }

  ngOnDestroy() {
    this.subscriptions.forEach((subscription) => {
      subscription.unsubscribe();
    });
  }

  CreateEvent(){
    this.errorDescription = ''
    this.registerForm.value.creator=this.currentUser.username;
    const messageData: CreateEvent = this.registerForm.value as unknown as CreateEvent;
    console.log(messageData)
    this.eventService.createEvent(messageData).subscribe({
      next: (result: any) => {
        console.log("EVENT CREATED")
        console.log(result)
      },
      error: (err: any) => {
        if(err.status == 401){
          this.errorDescription = 'ERROR 401'
        }
      }
  })
  }

}
