import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css'],
})
export class UsersComponent implements OnInit {
  users: any[] = [];

  constructor(private userService: UserService, private router: Router, private authService: AuthService) { }

  ngOnInit() {
    this.userService.getUsers().subscribe(
      (response: any) => {
        this.users = response.content;
      },
      (error) => {
        if (error.status === 403) {
          alert('Nemate permisiju za izlistavanje korisnika!');
        } else {
          console.error('Došlo je do greške:', error);
        }
      }
    );
  }

  deleteUser(id: number) {
    this.userService.deleteUser(id).subscribe(
      () => {
        this.users = this.users.filter((user) => user.id !== id);
      },
      (error) => {
        console.error('Došlo je do greške prilikom brisanja korisnika:', error);
      }
    );
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
