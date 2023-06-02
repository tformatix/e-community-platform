import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ResetPasswordPageComponent } from './reset-password-page.component';

const routes: Routes = [
  {
    path: "", component: ResetPasswordPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ResetPasswordPageRoutingModule { }
