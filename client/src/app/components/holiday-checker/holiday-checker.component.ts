import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CalendarService } from '../../services/calendar.service';
import { Holiday } from '../../models';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-holiday-checker',
  templateUrl: './holiday-checker.component.html',
  styleUrl: './holiday-checker.component.css'
})
export class HolidayCheckerComponent {
  private fb = inject(FormBuilder)
  private calendarService = inject(CalendarService)
  private messageService = inject(MessageService)

  form!: FormGroup
  holidays: Holiday[] = [];

  ngOnInit(): void {
    this.createForm()
  }

  createForm() {
    this.form = this.fb.group({
      country: this.fb.control<string>('', [Validators.required])
    })
  }

  processForm() {
    const country = this.form.value['country']

    this.calendarService.getHolidays(country).then(response => {
      if (response != null) {
        this.holidays = response;
      } else {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to retrieve information' });
      }
    })
  }
}
