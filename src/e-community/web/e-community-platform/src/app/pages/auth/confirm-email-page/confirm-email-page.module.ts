import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ConfirmEmailPageRoutingModule } from './confirm-email-page-routing.module';
import { ConfirmEmailPageComponent } from './confirm-email-page.component';
import { FormsModule } from '@angular/forms';


@NgModule({
  declarations: [
    ConfirmEmailPageComponent
  ],
  imports: [
    CommonModule,
    ConfirmEmailPageRoutingModule,
    FormsModule
  ]
})
export class ConfirmEmailPageModule { }
