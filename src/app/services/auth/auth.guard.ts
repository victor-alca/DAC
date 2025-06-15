import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  

  const router : Router = inject(Router);

  const authService: AuthService = inject(AuthService);

  const userType = authService.getCurrentUserType()
  
  if(userType == route.data['userType']){
    return true;
  }
  
  router.navigate(['login']);
  
  return false;
  
};
