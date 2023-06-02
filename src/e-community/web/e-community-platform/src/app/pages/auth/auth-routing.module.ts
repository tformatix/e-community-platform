import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'confirmEmail',
    loadChildren: () =>
      import('./confirm-email-page/confirm-email-page.module').then(
        (m) => m.ConfirmEmailPageModule
      ),
  },
  {
    path: 'resetPassword',
    loadChildren: () =>
      import('./reset-password-page/reset-password-page.module').then(
        (m) => m.ResetPasswordPageModule
      ),
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule { }
