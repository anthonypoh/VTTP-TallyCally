import { NgModule } from '@angular/core';
import { StyleClassModule } from 'primeng/styleclass';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextModule } from 'primeng/inputtext';
import { RippleModule } from 'primeng/ripple';
import { MenubarModule } from 'primeng/menubar';
import { CardModule } from 'primeng/card'
import { CalendarModule } from 'primeng/calendar';
import { DialogModule } from 'primeng/dialog';
import { MessagesModule } from 'primeng/messages';
import { TabMenuModule } from 'primeng/tabmenu';
import { DividerModule } from 'primeng/divider';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmPopupModule } from 'primeng/confirmpopup';
import { InputSwitchModule } from 'primeng/inputswitch';
import { ToastModule } from 'primeng/toast';
import { SidebarModule } from 'primeng/sidebar';
import { TableModule } from 'primeng/table';

@NgModule({
  declarations: [],
  imports: [StyleClassModule, CheckboxModule, ButtonModule, InputTextModule, RippleModule, MenubarModule, CardModule, CalendarModule, DialogModule, MessagesModule, TabMenuModule, DividerModule,
    ProgressSpinnerModule, TooltipModule, ConfirmPopupModule, InputSwitchModule, ToastModule, SidebarModule, TableModule],
  exports: [StyleClassModule, CheckboxModule, ButtonModule, InputTextModule, RippleModule, MenubarModule, CardModule, CalendarModule, DialogModule, MessagesModule, TabMenuModule, DividerModule,
    ProgressSpinnerModule, TooltipModule, ConfirmPopupModule, InputSwitchModule, ToastModule, SidebarModule, TableModule]
})
export class PrimeNgModule { }
