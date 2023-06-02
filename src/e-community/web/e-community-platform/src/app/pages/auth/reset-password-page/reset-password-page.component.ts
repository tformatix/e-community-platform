import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService, ResetPasswordModel } from 'src/app/core/api/v1';

@Component({
  selector: 'app-reset-page',
  templateUrl: './reset-password-page.component.html',
  styleUrls: ['./reset-password-page.component.scss'],
})
export class ResetPasswordPageComponent implements OnInit {
  isSuccess = false;
  isLoading = false;
  password = '';
  repeatPassword = '';

  resetPasswordModel: ResetPasswordModel = {
    newPassword: '',
    resetToken: '-',
    memberId: '-',
  };

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      const mid = params.get('mid');
      const token = params.get('t');

      if (!mid || !token) {
        this.isSuccess = false;
        this.isLoading = false;
        this.resetPasswordModel.newPassword = '-';
        return;
      }

      this.resetPasswordModel = {
        resetToken: token,
        memberId: mid,
        newPassword: '',
      };
    });
  }

  resetPassword() {
    this.isLoading = true
    if (this.password == this.repeatPassword) {
      this.resetPasswordModel.newPassword = this.password;
      this.authService.authResetPasswordPost(this.resetPasswordModel).subscribe(
        () => {
          this.isSuccess = true
          this.isLoading = false
        },
        (error) => {
          this.isSuccess = false
          this.isLoading = false
        }
      );
    } else {
      this.isLoading = false
      alert('Passwords must match!')
    }
  }
}
