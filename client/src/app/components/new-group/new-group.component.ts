import { Component, EventEmitter, Output, inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { CalendarService } from '../../services/calendar.service';
import { Store, select } from '@ngrx/store';
import { selectUserEmail } from '../../states/selector/user.selector';
import { CookieService } from 'ngx-cookie-service';
import { UserService } from '../../services/user.service';
import { setUser } from '../../states/action/user.action';
import { setGroupEvents } from '../../states/action/group.event.action';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-new-group',
  templateUrl: './new-group.component.html',
  styleUrl: './new-group.component.css'
})
export class NewGroupComponent {
  @Output() closeDialog: EventEmitter<void> = new EventEmitter<void>();
  private fb = inject(FormBuilder)
  private calendarService = inject(CalendarService)
  private store = inject(Store)
  private cookieService = inject(CookieService)
  private userService = inject(UserService)
  private messageService = inject(MessageService)

  form!: FormGroup
  email!: string

  ngOnInit(): void {
    this.createForm()
    this.store.pipe(select(selectUserEmail)).subscribe((email: string) => {
      this.email = email;
    });
  }

  createForm() {
    this.form = this.fb.group({
      groupName: this.fb.control<string>('', [Validators.required]),
      // description: this.fb.control<string>('', [Validators.required])
    })
  }

  processForm() {
    this.calendarService.createGroup(this.email, this.form.value.groupName)
      .then(msg => {
        console.log(msg)

        if (msg == 'success') {
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Group added' });
          this.createForm()
        } else {
          this.messageService.add({severity:'error', summary: 'Error', detail: 'Something went wrong'});
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
