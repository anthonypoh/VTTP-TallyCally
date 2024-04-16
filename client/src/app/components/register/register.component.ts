import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { User } from '../../models';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
  private router = inject(Router)
  private fb = inject(FormBuilder)
  private userService = inject(UserService)
  private messageService = inject(MessageService)

  form!: FormGroup

  ngOnInit(): void {
    this.createForm()
  }

  createForm() {
    this.form = this.fb.group({
      name: this.fb.control<string>('', [Validators.required]),
      email: this.fb.control<string>('', [Validators.required]),
      password: this.fb.control<string>('', [Validators.required]),
      mobile: this.fb.control<number | undefined>(undefined, [Validators.required])
    })
  }

  processForm() {
    const user = this.form.value as User
    this.userService.register(user)
      .then(msg => {
        console.log(msg)
        if (msg == 'success') {
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Registration complete' });
          this.router.navigate(['#'])
        } else {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Something went wrong' });
        }
      })
  }
}
