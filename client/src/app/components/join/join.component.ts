import { Component, EventEmitter, OnInit, Output, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Store, select } from '@ngrx/store';
import { CalendarService } from '../../services/calendar.service';
import { UserService } from '../../services/user.service';
import { CookieService } from 'ngx-cookie-service';
import { selectUser } from '../../states/selector/user.selector';
import { User } from '../../models';
import { Observable } from 'rxjs';
import { setGroupEvents } from '../../states/action/group.event.action';

@Component({
  selector: 'app-join',
  templateUrl: './join.component.html',
  styleUrl: './join.component.css'
})
export class JoinComponent implements OnInit {
  @Output() closeDialog: EventEmitter<void> = new EventEmitter<void>();
  private activatedRoute = inject(ActivatedRoute)
  private fb = inject(FormBuilder)
  private router = inject(Router)
  private store = inject(Store)
  private calendarService = inject(CalendarService)
  private userService = inject(UserService)
  private cookieService = inject(CookieService)

  form!: FormGroup
  invited_group_id!: string
  user!: User

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      this.invited_group_id = params['group_id'];
    });

    this.isLoggedIn().then(response => {
      if (!response) {
        this.router.navigate(['#'])
      }
    }).then(() => {
      if (this.invited_group_id) {
        this.joinGroup(this.invited_group_id, this.user.email)
      }
    })

    this.createForm()
  }

  createForm() {
    this.form = this.fb.group({
      link: this.fb.control<string>('', [Validators.required])
    })
  }

  processForm() {
    this.joinGroup(this.form.value.link, this.user.email)
  }

  joinGroup(link: string, email: string) {
    const parts = link.split('/');
    const group_id = parts[parts.length - 1];
    this.calendarService.joinGroup(group_id, email).then(msg => {
      console.log(msg)

      const token = this.cookieService.get('user_token')
      this.userService.checkToken(token).then(user => {
        return user
      }).then(user => {
        const email = user.email
        return this.calendarService.getGroupEvents(email, token)
      }).then(groupEvents => {
        this.store.dispatch(setGroupEvents({ groupEvents: groupEvents }))
      })

      this.router.navigate(['#'])
      this.closeDialog.emit();
    })
  }

  async isLoggedIn(): Promise<boolean> {
    const token = this.cookieService.get('user_token');
    try {
      const user = await this.userService.checkToken(token);
      this.user = user;
      if (user.email !== '') {
        return true;
      } else {
        return false;
      }
    } catch (error) {
      console.error('Error checking token:', error);
      return false;
    }
  }
}
