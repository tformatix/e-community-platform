import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ConfirmEmailPageComponent } from './confirm-email-page.component';

const routes: Routes = [
  {
    path: "", component: ConfirmEmailPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ConfirmEmailPageRoutingModule { }
