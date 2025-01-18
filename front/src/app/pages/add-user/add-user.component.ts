import { Component } from '@angular/core';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.css'],
})
export class AddUserComponent {
  user = {
    firstName: '',
    lastName: '',
    email: '',
    username: '',
    password: '',
    permissions: [],
  };

  constructor(private userService: UserService, private router: Router) { }

  addUser() {
    this.userService.createUser(this.user).subscribe(
      () => {
        this.router.navigate(['/users']);
      },
      (error) => {
        alert('Failed to create user');
      }
    );
  }
}