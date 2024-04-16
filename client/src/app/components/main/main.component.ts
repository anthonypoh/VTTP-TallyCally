import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { Store, select } from '@ngrx/store';
import { selectUser } from '../../states/selector/user.selector';
import { MessagingService } from '../../services/messaging.service';
import { CalendarService } from '../../services/calendar.service';
import { selectGroupEvents } from '../../states/selector/group.event.selector';
import { ConfirmationService, MenuItem, MessageService } from 'primeng/api';
import { Subscription } from 'rxjs';
import { Event as MyEvent, GroupEvent } from '../../models';
import { CookieService } from 'ngx-cookie-service';
import { UserService } from '../../services/user.service';
import { setGroupEvents } from '../../states/action/group.event.action';
import { Router } from '@angular/router';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrl: './main.component.css'
})
export class MainComponent implements OnInit, OnDestroy {
  visible: boolean = false;
  showDialog() {
    this.visible = !this.visible
  }
  // private sessionService = inject(SessionService)
  // user$!: Observable<User>;
  // user!: User
  // constructor(private store: Store<{ user: User }>) { }
  private router = inject(Router)
  private messagingService = inject(MessagingService)
  private calendarService = inject(CalendarService)
  private cookieService = inject(CookieService)
  private userService = inject(UserService)
  private store = inject(Store)
  private confirmationService = inject(ConfirmationService)
  private messageService = inject(MessageService)

  tabMenuItems!: MenuItem
  event = { header: { color: 'blue' } }
  user$ = this.store.pipe(select(selectUser));
  groupEventsSubscription$!: Subscription;
  groupEvents!: GroupEvent[]
  items: MenuItem[] = []
  activeItem!: MenuItem
  selectedGroup: string = "All"

  newGroupDialog: boolean = false
  // joinGroupDialog: boolean = false
  newEventDialog: boolean = false
  generateLinkDialog: boolean = false
  editEventDialog!: boolean
  editedEvent!: MyEvent
  inviteLink!: string
  selectedGroup_Id!: string
  holidaysVisible: boolean = false;

  ngOnInit() {
    this.groupEventsSubscription$ = this.store.pipe(select(selectGroupEvents)).subscribe(groupEvents => {
      this.items = [{ label: 'All', icon: 'pi pi-book' }]
      this.activeItem = this.items[0]
      this.groupEvents = groupEvents
      if (groupEvents.length > 0) {
        groupEvents.forEach(groupEvent => {
          this.items.push({ label: groupEvent.groupName })
        });
      }
      this.items = [...this.items]
    });
  }

  onActiveItemChange(active: MenuItem) {
    this.activeItem = active
    this.selectedGroup = active.label ?? "All"
  }

  createGroup() {
    this.newGroupDialog = !this.newGroupDialog
  }

  joinGroup() {
    // this.joinGroupDialog = !this.joinGroupDialog
    this.router.navigate(['join'])
  }

  createEvent(group_id: string) {
    this.selectedGroup_Id = group_id
    this.newEventDialog = !this.newEventDialog
  }

  generateLink(group_id: string) {
    this.generateLinkDialog = !this.generateLinkDialog
    this.calendarService.generateInviteLink(group_id).then(msg => {
      this.inviteLink = msg
    })
  }

  editEvent(event: MyEvent, group_id: string) {
    this.editedEvent = event
    this.selectedGroup_Id = group_id
    this.editEventDialog = !this.editEventDialog
  }

  confirmDelete(event: Event, myEvent: MyEvent, group_id: string) {
    const _id = myEvent._id

    this.confirmationService.confirm({
      target: event.target ? event.target : undefined,
      message: 'Do you want to delete this event?',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.calendarService.deleteEvent(_id, group_id).then(msg => {
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
        })
      },
      reject: () => {
        //reject action
      }
    });
  }

  copyInviteLink(inputElement: any) {
    inputElement.select();
    document.execCommand('copy');
    inputElement.setSelectionRange(0, 0);
    this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Copied to clipboard' });
  }

  toggleHolidays() {
    this.holidaysVisible = !this.holidaysVisible
  }

  ngOnDestroy(): void {
    this.groupEventsSubscription$.unsubscribe()
  }
}
