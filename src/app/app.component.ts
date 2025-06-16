import { Component } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from './services/auth/auth.service';
import { ClientService } from './services/client/client.service';
import { Client } from './shared/models/client/client';
import { ClientDTO } from './shared/dtos/clientDto';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'], // corrigido: era 'styleUrl' (deveria ser plural)
})
export class AppComponent {
  title = 'dac';
  showHeader = true;
  miles = 0;

  constructor(private router: Router, private authService: AuthService, private clientService: ClientService) {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event) => {
        const currentUrl = (event as NavigationEnd).urlAfterRedirects;
        this.showHeader = !['/login', '/sign'].includes(currentUrl);
      });
  }

  ngOnInit(){
    if(this.getUserType() == "CLIENTE"){
      this.getClientMiles()
    }
  }

  getUserType(){
    return this.authService.getCurrentUserType()
  }

  getCurrentUser(){
    return this.authService.getCurrentUserData()
  }

  getClientMiles(): number {
    let client = this.authService.getCurrentUserData() as ClientDTO;

    this.clientService.getById(client.codigo).subscribe((resp) => {
      this.miles = resp?.saldo_milhas!;
    });

    return this.miles;

  }

  logout(): void {
    this.authService.removeCurrentUserData()
    this.router.navigate(['/login']);
  }

}
