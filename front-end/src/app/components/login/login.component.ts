import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  hide = true
  errorDescription: string = ''
  registerForm = new FormGroup({
    email: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required])
  });
  constructor(private authService: AuthService){}
  LoginUser(){
    this.errorDescription = ''
    this.authService.login(this.registerForm.value).subscribe({
      next: (result: any) => {
        console.log("I AM THE KING")
      },
      error: (err: any) => {
        if(err.status == 401){
          this.errorDescription = 'Wrong username and/or password'
        }
      }
  })
  }
}
