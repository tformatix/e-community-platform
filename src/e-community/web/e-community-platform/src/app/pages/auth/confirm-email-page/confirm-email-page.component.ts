import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService, ConfirmEmailModel } from 'src/app/core/api/v1';

@Component({
  selector: 'app-confirm-page',
  templateUrl: './confirm-email-page.component.html',
  styleUrls: ['./confirm-email-page.component.scss'],
})
export class ConfirmEmailPageComponent implements OnInit {
  isFinished = false;
  isSuccess = false;
  email = '';
  password = '';

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
        this.isFinished = true;
        return;
      }

      this.authService
        .authConfirmEmailPost({
          confirmToken: token!,
          memberId: mid!,
        })
        .subscribe(
          () => {
            this.isSuccess = true;
            this.isFinished = true;
          },
          (error) => {
            this.isSuccess = false;
            this.isFinished = true;
            this.setError(error.error)
          }
        );
    });
  }

  resendConfirmationEmail() {
    this.isFinished = false
    this.authService
      .authResendConfirmationEmailPost({
        email: this.email,
        password: this.password,
      })
      .subscribe(
        () => {
          this.isFinished = true
          alert('Email sent successfully!')
        },
        (error) => {
          this.isFinished = true
          this.setError(error.error)
        }
      );
  }

  setError(error: string) {
    var status = ''
    switch (error) {
      case 'invalid_credentials':
        status = 'Invalid Credentials!';
        break;
      case 'email_already_confirmed':
        status = 'Email already confirmed!';
        break;
      case 'couldnt_send_email':
        status = 'Error while sending email!';
        break;
      default:
        status = 'An Error occured!';
    }
    alert(status)
  }
}
