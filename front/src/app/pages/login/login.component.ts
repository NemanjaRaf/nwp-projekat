import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  email: string = '';
  password: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  login() {
    this.authService.login({ email: this.email, password: this.password }).subscribe(
      (response: any) => {
        localStorage.setItem('token', response.jwt);
        localStorage.setItem('permissions', JSON.stringify(response.permissions));
        this.router.navigate(['/users']);
      },
      (error) => {
        alert('Invalid email or password');
      }
    );
  }
}