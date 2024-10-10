import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LoggedUser } from 'src/app/model/logged-user';
import { RegisterUser } from 'src/app/model/register-user.model';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit{
  hide = true
  user: RegisterUser = {username: '', password: ''}
  repeatPassword: string
  registerForm = new FormGroup({
    firstName: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required]),
    repeatPassword: new FormControl('', [Validators.required]),
  });

  constructor( private authService: AuthService, private router: Router){}
  ngOnInit(): void {
  }
  
  RegisterUser(){
    this.fillUser()
    if(!this.IsValid()){
      alert('You must fill all fields')
      return;
    }
    if(this.user.password != this.repeatPassword){
      alert('passwords must match!')
      return;
    }
  
    this.authService.registerUser(this.user).subscribe({
      next: () => { 
        alert('User successfully registered')
        this.router.navigate(['/login'])
       },
       error: (data) => {
        if(data.status == 409){
          alert('This username is already in use, please use a different one')
        }
       }
    });
  }

  fillUser(){
    this.user.username = this.registerForm.value.firstName || ""
    this.user.password = this.registerForm.value.password || ""
    this.repeatPassword = this.registerForm.value.repeatPassword || ""
  }
  IsValid(){
    if(!this.user.username)
      return false;
    else
      return true;
  }
}
