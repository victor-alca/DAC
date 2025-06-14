import { Component } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from './services/auth/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'], // corrigido: era 'styleUrl' (deveria ser plural)
})
export class AppComponent {
  title = 'dac';
  showHeader = true;

  getUserType(){
    return this.authService.getCurrentUserType()
  }

  getCurrentUser(){
    return this.authService.getCurrentUserData()
  }

  constructor(private router: Router, private authService: AuthService) {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event) => {
        const currentUrl = (event as NavigationEnd).urlAfterRedirects;
        this.showHeader = !['/login', '/sign'].includes(currentUrl);
      });
  }

  logout(): void {
    this.authService.removeCurrentUserData()
    this.router.navigate(['/login']);
  }
}
