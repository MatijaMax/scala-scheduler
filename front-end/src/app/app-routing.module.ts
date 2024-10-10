import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterComponent } from './components/register/register.component';
import { LoginComponent } from './components/login/login.component';
import { AuthGuardService } from './ActivateGuard/AuthGuardService';
import { MessageComponent } from './components/message/message.component';
import { CalendarComponent } from './components/calendar/calendar.component';
import { EventListComponent } from './components/event-list/event-list.component';
import { EventCreationComponent } from './components/event-creation/event-creation.component';
import { JoinEventListComponent } from './components/join-event-list/join-event-list.component';


const routes: Routes = [
  { path: '', redirectTo: 'register', pathMatch: 'full' },

  {
    path: 'register',
    component: RegisterComponent,
    canActivate: [AuthGuardService],
  },

  { path: 'login', component: LoginComponent, canActivate: [AuthGuardService] },

  {
    path: 'message',
    component: MessageComponent,
    canActivate: [AuthGuardService],
  },

  {
    path: 'calendar',
    component: CalendarComponent,
    canActivate: [AuthGuardService],
  },

  {
    path: 'event-list',
    component: EventListComponent,
    canActivate: [AuthGuardService],
  },

  {
    path: 'join-event-list',
    component: JoinEventListComponent,
    canActivate: [AuthGuardService],
  },

  {
    path: 'event-creation',
    component: EventCreationComponent,
    canActivate: [AuthGuardService],
  },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
