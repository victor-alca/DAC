import { Component } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'dac';
  showHeader = true;

  constructor(private router: Router) {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event) => {
        const currentUrl = (event as NavigationEnd).url;
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
