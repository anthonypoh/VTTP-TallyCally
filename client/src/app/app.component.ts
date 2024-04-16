import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FcmObject, GroupEvent, User } from './models';
import { MessagingService } from './services/messaging.service';
import { MenuItem } from 'primeng/api/menuitem';
import { Store, select } from '@ngrx/store';
import { selectUser } from './states/selector/user.selector';
import { CookieService } from 'ngx-cookie-service';
import { NavigationEnd, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { UserService } from './services/user.service';
import { clearUser, setUser } from './states/action/user.action';
import { CalendarService } from './services/calendar.service';
import { setGroupEvents } from './states/action/group.event.action';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'client';
  private messagingService = inject(MessagingService)
  private cookieService = inject(CookieService)
  private userService = inject(UserService)
  private calendarService = inject(CalendarService)
  private router = inject(Router)
  private store = inject(Store)
  private messageService = inject(MessageService)

  user!: User
  items!: MenuItem[]
  routerListener$!: Subscription
  user$ = this.store.pipe(select(selectUser))
  checked: boolean = false
  fcmObject!: FcmObject
  messageSubscription!: Subscription;

  ngOnInit(): void {
    this.routerListener$ = this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        // this.isLoggedIn()
        if (!this.router.url.includes('/login') && !this.router.url.includes('/register')) {
          this.isLoggedIn()
        }
      }
    })

    this.items = [
      { label: 'TallyCally', icon: 'pi pi-fw pi-home', routerLink: ['#'] },
    ];

    if ('serviceWorker' in navigator) {
      navigator.serviceWorker.register('/firebase-messaging-sw.js', { scope: 'firebase-cloud-messaging-push-scope' })
        .then((registration) => {
          console.log('fcm worker state:', registration?.active?.state);
          this.startReceivingMessages();
          // this.messagingService.requestToken()
          // this.messagingService.receiveMessages()
          //   .then(fcmObject => {
          //     this.fcmObject = fcmObject
          //     console.log(fcmObject)
          //     this.messageService.add({ severity: 'info', summary: fcmObject.title, detail: fcmObject.body });
          //   })
        })
        .catch((err) => {
          console.error('fcm worker registration failed:', err);
        });
    }
  }

  ngOnDestroy(): void {
    this.routerListener$.unsubscribe()
    if (this.messageSubscription) {
      this.messageSubscription.unsubscribe();
    }
  }

  isLoggedIn() {
    const token = this.cookieService.get('user_token')
    this.userService.checkToken(token).then(async user => {
      this.store.dispatch(setUser({ user }))
      this.user = user
      console.log(user)
      if (user.msg == 'unauthenticated') {
        this.router.navigate(['login'])
        throw new Error('User unauthenticated')
      }
      if (user.fcmToken && user.fcmToken != '') {
        this.messagingService.requestToken(this.user.email)
        this.checked = true
      }
      return user
    }).then(user => {
      const email = user.email
      return this.calendarService.getGroupEvents(email, token)
    }).then(groupEvents => {
      this.store.dispatch(setGroupEvents({ groupEvents: groupEvents }))
    }).catch(error => {
      console.error(error.message);
    });
  }

  logout(): void {
    // TODO: delete token from sql
    this.store.dispatch(clearUser())
    this.cookieService.delete('user_token')
    this.router.navigate(['login'])
  }

  notificationToggle(): void {
    if (this.checked) {
      this.messagingService.requestToken(this.user.email)
      // this.messagingService.receiveMessages()
    }
    console.log(this.checked)
  }

  startReceivingMessages() {
    this.messageSubscription = this.messagingService.receiveMessages()
      .subscribe(
        (fcmObject: FcmObject) => {
          this.fcmObject = fcmObject;
          console.log(fcmObject);
          this.messageService.add({ severity: 'info', summary: fcmObject.title, detail: fcmObject.body, sticky: true });
          this.messagingService.requestToken(this.user.email)
        },
        error => {
          console.error('Error receiving message.', error);
        }
      );
  }

}
