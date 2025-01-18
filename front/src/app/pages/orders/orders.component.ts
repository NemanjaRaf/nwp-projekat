import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../services/order.service';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css'],
})
export class OrdersComponent implements OnInit {
  orders: any[] = [];
  users: any[] = [];
  loading: boolean = false;
  errorMessage: string = '';
  isAdmin: boolean = false;

  filters = {
    status: '',
    dateFrom: '',
    dateTo: '',
    userId: '',
  };

  orderStatuses = ['ORDERED', 'PREPARING', 'IN_DELIVERY', 'DELIVERED', 'CANCELED', 'SCHEDULED'];

  constructor(
    private orderService: OrderService,
    private userService: UserService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.checkUserRole();
    this.fetchOrders();
    if (this.isAdmin) {
      this.fetchUsers();
    }
  }

  checkUserRole(): void {
    const token = localStorage.getItem('token');
    if (token) {
      const decoded: any = jwtDecode(token);
      this.isAdmin = decoded?.role === 'ADMIN';
    }
  }

  fetchOrders(): void {
    this.loading = true;
    this.orderService
      .getOrders(this.filters.status, this.filters.dateFrom, this.filters.dateTo, this.filters.userId ? +this.filters.userId : undefined)
      .subscribe({
        next: (data) => {
          this.orders = data.map((order) => ({
            ...order,
            dishesList: order.dishes?.map((dish: any) => dish.name).join(', ') || 'N/A',
          }));
          this.loading = false;
        },
        error: (error) => {
          this.errorMessage = 'Greška pri učitavanju porudžbina.';
          this.loading = false;
          console.error('Error fetching orders:', error);
        },
      });
  }

  fetchUsers(): void {
    this.userService.getUsers().subscribe({
      next: (data) => {
        this.users = data.content || [];
      },
      error: (error) => {
        console.error('Error fetching users:', error);
        this.users = [];
      },
    });
  }


  applyFilters(): void {
    this.fetchOrders();
  }

  trackOrder(orderId: number): void {
    this.router.navigate(['/track-order', orderId]);
  }
}
