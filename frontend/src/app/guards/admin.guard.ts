import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(): boolean {
    const token = localStorage.getItem("auth_token");

    if (token) {
      const payload = JSON.parse(atob(token.split('.')[1])); // decode JWT
      if (payload.role === "ADMIN") {
        return true;
      }
    }

    this.router.navigate(['/']);
    return false;
  }
}
