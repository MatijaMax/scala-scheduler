import { Component, OnInit, ViewChild } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MessageService } from 'src/app/services/message.service';
import { Message } from 'src/app/model/message';
import { LoggedUser } from 'src/app/model/logged-user';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.css']
})
export class MessageComponent implements OnInit {
  hide = true
  items: Message[] = [];
  errorDescription: string = ''
  currentUser: LoggedUser;
  private subscriptions: Subscription[] = [];
  registerForm = new FormGroup({
    id: new FormControl(0),
    content: new FormControl('', [Validators.required]),
    receiver: new FormControl('', [Validators.required]),
    read: new FormControl(false)
  });

  constructor(private authService: AuthService, private messageService: MessageService){
    
    this.currentUser = this.authService.getCurrentUser()
    console.log(this.currentUser);
  }

  ngOnInit(): void {
    this.loadItems();
  }

  loadItems() {
    if(this.currentUser.username != null){
    const subscription = this.messageService.getAllMessagesExclusive(this.currentUser.username).subscribe(
      (data) => {
        this.items = data;
      },
      (error) => {
        console.error('Error loading messages:', error);
      }
    );
  }
}

  readMessage(message: Message){
    message.read = true;
    this.items = this.items.filter(item => item.id !== message.id)
    this.messageService.updateMessage(message.id, message).subscribe({
      next: () => { 
        //alert('You just updated yolo')
      
       },
       error: (data) => {
        if(data.status == 404){
          //alert('NOT FOUND')
        }
       }
    });
  }

  sendMessage(){
    this.errorDescription = ''
    const messageData: Message = this.registerForm.value as Message;
    console.log(messageData)
    this.messageService.createMessage(messageData).subscribe({
      next: (result: any) => {
        console.log("MESSAGE SENT")
        console.log(result)
      },
      error: (err: any) => {
        if(err.status == 401){
          this.errorDescription = 'ERROR 401'
        }
      }
  })
  }

  ngOnDestroy() {
    this.subscriptions.forEach((subscription) => {
      subscription.unsubscribe();
    });
  }

}
