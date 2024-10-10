import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoggedUser } from 'src/app/model/logged-user';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit{
  constructor(private authService: AuthService, private router: Router){}
  user: LoggedUser | undefined
  ngOnInit(): void {
    this.authService.currentUser.subscribe(user => {
      this.user = user;
      if(this.user.username == ''){
        this.user = undefined
      }
    });
  }
  logout(){
    this.authService.logout()
    this.router.navigate(['/login']);
    this.user = undefined
  }
}
