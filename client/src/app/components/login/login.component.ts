import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { Store, select } from '@ngrx/store';
import { selectMessage } from '../../states/selector/message.selector';
import { Observable, Subscription } from 'rxjs';
import { MessageService } from 'primeng/api';
import { CookieService } from 'ngx-cookie-service';
import { setMessage } from '../../states/action/message.action';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  private router = inject(Router)
  private fb = inject(FormBuilder)
  private store = inject(Store)
  private userService = inject(UserService)
  private messageService = inject(MessageService)
  private cookieService = inject(CookieService)

  form!: FormGroup

  message$!: Observable<string>
  message: string = ''
  messageSubscription!: Subscription;

  ngOnInit(): void {
    this.createForm()

    this.message$ = this.store.pipe(
      select(selectMessage)
    )
    this.messageSubscription = this.message$.subscribe(message => {
      switch (message) {
        case "Incorrect username or password.":
          this.addSingle('warn', message)
          break;
        default:
          break;
      }
    });
  }

  createForm() {
    this.form = this.fb.group({
      email: this.fb.control<string>('', [Validators.required]),
      password: this.fb.control<string>('', [Validators.required])
    })
  }

  processForm() {
    const email = this.form.get('email')?.value
    const password = this.form.get('password')?.value
    this.userService.login(email, password)
      .then(token => {
        // if auth fail, 401 intercepted
        this.cookieService.set('user_token', token);
        this.router.navigate(['#'])
      })
  }

  addSingle(severity: string, msg: string) {
    this.message = msg
    this.messageService.add({ severity: severity, summary: this.message });
    this.store.dispatch(setMessage({ message: '' }))
  }

  register() {
    this.router.navigate(['register'])
  }

  ngOnDestroy() {
    this.messageSubscription.unsubscribe();
  }
}
