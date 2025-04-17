import { CommonModule } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';
import { EmailValidatorDirective } from '../../shared/directives/email-validator.directive';
import { Login } from '../../shared/models/login.model';
// import { ParseSourceFile } from '@angular/compiler';
// import { AuthService } from '../../services/autenticador/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, EmailValidatorDirective],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
    @ViewChild('formLogin') formLogin!: NgForm;
    login: Login = new Login();

    constructor(private router: Router) {}

    submit(): void {
      if (this.formLogin.form.valid)
        console.log(this.login)

      // Simulação de autenticação
      if (this.login.email === 'funcionario@empresa.com') {
        this.login.role = 'employee';
      } else {
        this.login.role = 'user';
      }

      // Redireciona com base no tipo de usuário
      if (this.login.role === 'employee') {
        this.router.navigate(['/employee-dashboard']);
      } else {
        this.router.navigate(['/customer-home']);
      }
    }

}
