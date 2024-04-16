import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainComponent } from './components/main/main.component';
import { LoginComponent } from './components/login/login.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UserService } from './services/user.service';
import { StoreModule } from '@ngrx/store';
import { SessionService } from './services/session.service';
import { CalendarService } from './services/calendar.service';
import { MessagingService } from './services/messaging.service';
import { AngularFireModule } from '@angular/fire/compat';
import { AngularFireMessagingModule } from '@angular/fire/compat/messaging';
import { environment } from './environments/environment';
import { PrimeNgModule } from './primeng.module';
import { NewEventComponent } from './components/new-event/new-event.component';
import { AuthInterceptor } from './auth.interceptor';
import { ConfirmationService, MessageService } from 'primeng/api';
import { reducers } from './states/reducer/reducers';
import { CookieService } from 'ngx-cookie-service';
import { NewGroupComponent } from './components/new-group/new-group.component';
import { DynamicDialogRef } from 'primeng/dynamicdialog';
import { RegisterComponent } from './components/register/register.component';
import { JoinComponent } from './components/join/join.component';
import { EditEventComponent } from './components/edit-event/edit-event.component';
import { HolidayCheckerComponent } from './components/holiday-checker/holiday-checker.component';

@NgModule({
  declarations: [AppComponent, MainComponent, LoginComponent, NewEventComponent, NewGroupComponent, RegisterComponent, JoinComponent, EditEventComponent, HolidayCheckerComponent],
  imports: [BrowserModule, BrowserAnimationsModule, AppRoutingModule, ReactiveFormsModule, FormsModule, HttpClientModule, StoreModule.forRoot(reducers), AngularFireModule.initializeApp(environment.firebase),
    AngularFireMessagingModule, PrimeNgModule],
  providers: [UserService, SessionService, CalendarService, MessagingService, MessageService, CookieService, DynamicDialogRef, ConfirmationService, {
    provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor,
    multi: true
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
