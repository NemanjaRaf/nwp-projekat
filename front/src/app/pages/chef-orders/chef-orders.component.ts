import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-chef-orders',
  templateUrl: './chef-orders.component.html',
  styleUrls: ['./chef-orders.component.css'],
})
export class ChefOrdersComponent implements OnInit {
  orders: any[] = [];
  loading: boolean = false;
  errorMessage: string = '';

  constructor(private orderService: OrderService) { }

  ngOnInit(): void {
    this.fetchOrders();
  }

  fetchOrders(): void {
    this.loading = true;
    this.orderService.getOrders().subscribe({
      next: (data) => {
        this.orders = data.map(order => ({
          ...order,
          formattedDishes: order.dishes.map((dish: any) => dish.name).join(', '),
          scheduledTime: order.scheduledTime ? new Date(order.scheduledTime) : null,
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


  acceptOrder(orderId: number): void {
    this.orderService.acceptOrder(orderId).subscribe({
      next: () => {
        this.fetchOrders();
      },
      error: (error) => {
        this.errorMessage = 'Greška pri prihvatanju porudžbine.';
        console.error('Error accepting order:', error);
      },
    });
  }

  rejectOrder(orderId: number): void {
    this.orderService.rejectOrder(orderId).subscribe({
      next: () => {
        this.fetchOrders();
      },
      error: (error) => {
        this.errorMessage = 'Greška pri odbijanju porudžbine.';
        console.error('Error rejecting order:', error);
      },
    });
  }

  canAcceptOrder(order: any): boolean {
    const currentTime = new Date();
    const scheduledTime = order.scheduledTime ? new Date(order.scheduledTime) : null;

    return (
      order.status === 'ORDERED' &&
      (!scheduledTime || scheduledTime <= currentTime)
    );
  }
}
