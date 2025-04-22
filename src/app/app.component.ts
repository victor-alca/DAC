import { Component } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';
import { Login } from './shared';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'], // corrigido: era 'styleUrl' (deveria ser plural)
})
export class AppComponent {
  title = 'dac';
  showHeader = true;

  getUser() {
    const rawUser = localStorage.getItem('LOGGED_USER');
    const user = rawUser ? JSON.parse(rawUser) as Login : null;
    return user
  }



  constructor(private router: Router) {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event) => {
        const currentUrl = (event as NavigationEnd).urlAfterRedirects;
        this.showHeader = !['/login', '/sign'].includes(currentUrl);
      });
  }

  logout(): void {
    // Limpa dados do usuário (se necessário)
    // localStorage.clear();

    // Redireciona para a página de login
    this.router.navigate(['/login']);
  }
}
