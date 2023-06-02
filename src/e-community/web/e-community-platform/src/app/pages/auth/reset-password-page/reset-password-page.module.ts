import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ResetPasswordPageRoutingModule } from './reset-password-page-routing.module';
import { ResetPasswordPageComponent } from './reset-password-page.component';
import { FormsModule } from '@angular/forms';


@NgModule({
  declarations: [
    ResetPasswordPageComponent
  ],
  imports: [
    CommonModule,
    ResetPasswordPageRoutingModule,
    FormsModule
  ]
})
export class ResetPasswordPageModule { }
