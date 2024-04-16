import { Injectable, inject } from '@angular/core';
import { AngularFireMessaging } from '@angular/fire/compat/messaging';
import { UserService } from './user.service';
import { Store } from '@ngrx/store';
import { FcmObject } from '../models';
import { Observable } from 'rxjs';

@Injectable()
export class MessagingService {

  constructor(private angularFireMessaging: AngularFireMessaging, private userService: UserService) { }
  store = inject(Store)

  requestToken(email: string): void {
    this.angularFireMessaging.requestToken.subscribe({
      next: token => {
        if (token != null) {
          this.userService.updateFcmToken(email, token).then(msg => {
            console.log(msg)
          })
        }
      },
      error: err => {
        console.error('Fetching FCM token failed: ', err)
      }
    })
  }

  async returnToken(): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      this.angularFireMessaging.requestToken.subscribe({
        next: token => {
          if (token != null) {
            resolve(token); // Resolve the promise with the token value
          } else {
            resolve('NOT_SET'); // Resolve with 'NOT_SET' if token is null
          }
        },
        error: err => {
          console.error('Fetching FCM token failed: ', err);
          reject(err); // Reject the promise if there's an error
        }
      });
    });
  }


  // receiveMessages(): void {
  //   this.angularFireMessaging.messages.subscribe({
  //     next(payload) {
  //       console.log('Message received. ', payload);

  //     },
  //     error(err) {
  //       console.error('Error receiving message.', err);
  //     },
  //   });
  // }

  receiveMessages(): Observable<FcmObject> {
    return new Observable<FcmObject>((observer) => {
      const subscription = this.angularFireMessaging.messages.subscribe({
        next(payload) {
          if (payload.data) {
            const fcmObject: FcmObject = {
              title: payload.data['title'],
              body: payload.data['body']
            };
            observer.next(fcmObject);
          }
          console.log('Message received. ', payload);
        },
        error(err) {
          console.error('Error receiving message.', err);
          observer.error(err);
        },
      });

      // Cleanup subscription when unsubscribed
      return () => subscription.unsubscribe();
    });
  }
}
