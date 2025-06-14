import { CommonModule } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';
import { EmailValidatorDirective } from '../../shared/directives/email-validator.directive';
import { Login } from '../../shared/models/login/login.model';
import { LoginService } from '../../services/login/login.service';
import { AuthService } from '../../services/auth/auth.service';
// import { ParseSourceFile } from '@angular/compiler';
// import { AuthService } from '../../services/autenticador/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, EmailValidatorDirective],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  @ViewChild('formLogin') formLogin!: NgForm;
  login: Login = new Login();
  hasError = false;
  constructor(private loginService: LoginService, private authService: AuthService, private router: Router) {}

  submit(): void {
    if (this.formLogin.form.valid){
      this.loginService.loginUser(this.login).subscribe({
        next: (response) => {
          this.authService.setCurrentUserData(response)
          let userType = this.authService.getCurrentUserType()
          if(userType == "CLIENTE"){
            this.router.navigate(['/customer-home']);
          }else{
            this.router.navigate(['/employee-dashboard'])
          }
        },
        error: (err) => {
          this.hasError = true
        }
      })
    }
  }
}
