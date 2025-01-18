import { Component, OnInit } from '@angular/core';
import { ErrorService } from './error.service';
import { UserService } from '../services/user.service';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-errors',
  templateUrl: './errors.component.html',
  styleUrls: ['./errors.component.css'],
})
export class ErrorsComponent implements OnInit {
  errors: any[] = [];
  users: any[] = [];
  isAdmin: boolean = false;
  filters = {
    userId: '',
  };

  constructor(private errorService: ErrorService, private userService: UserService) { }

  ngOnInit(): void {
    this.checkAdminRole();
    this.fetchErrors();
    if (this.isAdmin) {
      this.fetchUsers();
    }
  }

  checkAdminRole(): void {
    const token = localStorage.getItem('token');
    if (token) {
      const decoded: any = jwtDecode(token);
      this.isAdmin = decoded?.role === 'ADMIN';
    }
  }

  fetchErrors(): void {
    const userId = this.filters.userId ? +this.filters.userId : undefined;
    this.errorService.getErrors(userId).subscribe((data) => {
      console.log('API Response:', data);
      this.errors = Array.isArray(data) ? data : [data];
    });
  }

  fetchUsers(): void {
    this.userService.getUsers().subscribe((data) => {
      this.users = data.content;
    });
  }

  applyFilters(): void {
    this.fetchErrors();
  }
}