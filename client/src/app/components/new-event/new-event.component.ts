import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { CalendarService } from '../../services/calendar.service';
import { UserService } from '../../services/user.service';
import { CookieService } from 'ngx-cookie-service';
import { Store } from '@ngrx/store';
import { setGroupEvents } from '../../states/action/group.event.action';
import { Event } from '../../models';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-new-event',
  templateUrl: './new-event.component.html',
  styleUrl: './new-event.component.css'
})
export class NewEventComponent implements OnInit {
  @Input() group_id!: string
  @Output() closeDialog: EventEmitter<void> = new EventEmitter<void>();
  private fb = inject(FormBuilder)
  private calendarService = inject(CalendarService)
  private userService = inject(UserService)
  private cookieService = inject(CookieService)
  private store = inject(Store)
  private messageService = inject(MessageService)

  form!: FormGroup

  ngOnInit(): void {
    this.createForm()
  }

  createForm() {
    this.form = this.fb.group({
      title: this.fb.control<string>('', [Validators.required]),
      description: this.fb.control<string>('', [Validators.required]),
      location: this.fb.control<string>('', [Validators.required]),
      start: new FormControl<Date>(new Date, [Validators.required]),
      end: new FormControl<Date>(new Date, [Validators.required])
    })
  }

  processForm() {
    const event = this.form.value as Event

    this.calendarService.createEvent(event, this.group_id)
      .then(msg => {
        console.log(msg)

        if (msg == 'success') {
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Event added' });
        } else {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Something went wrong' });
        }

        const token = this.cookieService.get('user_token')
        this.userService.checkToken(token).then(user => {
          return user
        }).then(user => {
          const email = user.email
          return this.calendarService.getGroupEvents(email, token)
        }).then(groupEvents => {
          this.store.dispatch(setGroupEvents({ groupEvents: groupEvents }))
        })

        this.closeDialog.emit();
      })
  }
}
