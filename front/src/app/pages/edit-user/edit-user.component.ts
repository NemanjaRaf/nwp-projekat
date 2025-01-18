import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css'],
})
export class EditUserComponent implements OnInit {
  user: any = {
    firstName: '',
    lastName: '',
    email: '',
    permissions: [],
  };

  constructor(
    private userService: UserService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit() {
    const userId = this.route.snapshot.params['id'];

    this.userService.getUserById(userId).subscribe(
      (response) => {
        this.user = response;
      },
      (error) => {
        alert('Failed to load user data');
        this.router.navigate(['/users']);
      }
    );
  }

  updateUser() {
    this.userService.updateUser(this.user.id, this.user).subscribe(
      () => {
        alert('User updated successfully!');
        this.router.navigate(['/users']);
      },
      (error) => {
        if (error.status === 403) {
          alert('Nemate permisiju za izmenu korisnika!');
        } else {
          alert('Failed to update user');
        }
      }
    );
  }
}
