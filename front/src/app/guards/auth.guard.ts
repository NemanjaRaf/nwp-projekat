import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    const requiredPermission = route.data['permission'];
    if (this.hasPermission(requiredPermission)) {
      return true;
    } else {
      this.router.navigate(['/access-denied']);
      return false;
    }
  }

  private hasPermission(permission: string): boolean {
    let perms = localStorage.getItem('permissions') || '[]';
    let permissions = [];
    try {
      permissions = JSON.parse(perms);
    } catch (e) {
      permissions = [];
    }


    return permissions.includes(permission);
  }
}