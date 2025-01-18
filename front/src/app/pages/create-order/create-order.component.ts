import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../services/order.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-order',
  templateUrl: './create-order.component.html',
  styleUrls: ['./create-order.component.css'],
})
export class CreateOrderComponent implements OnInit {
  menu: any[] = [];
  selectedDishes: any[] = [];
  errorMessage: string = '';
  successMessage: string = '';
  scheduledTime: string = '';

  constructor(private orderService: OrderService, private router: Router) { }

  ngOnInit(): void {
    this.fetchMenu();
  }

  fetchMenu(): void {
    this.orderService.getMenu().subscribe({
      next: (data) => {
        this.menu = data;
      },
      error: (error) => {
        this.errorMessage = 'Greška pri dohvatanju menija. Pokušajte ponovo.';
        console.error('Error:', error);
      },
    });
  }

  createOrder(): void {
    if (this.selectedDishes.length === 0) {
      this.errorMessage = 'Morate odabrati barem jedno jelo.';
      return;
    }

    const order = {
      dishes: this.selectedDishes.map((dish) => dish.id),
      active: true,
    };

    this.orderService.placeOrder(order).subscribe({
      next: () => {
        this.successMessage = 'Porudžbina uspešno kreirana!';
        setTimeout(() => this.router.navigate(['/orders']), 2000);
      },
      error: (error) => {
        this.errorMessage = 'Greška pri kreiranju porudžbine. Pokušajte ponovo.';
        console.error('Error:', error);
      },
    });
  }

  scheduleOrder(): void {
    if (this.selectedDishes.length === 0 || !this.scheduledTime) {
      this.errorMessage = 'Morate odabrati barem jedno jelo i uneti vreme.';
      return;
    }

    const order = {
      dishes: this.selectedDishes.map((dish) => dish.id),
      scheduledTime: this.scheduledTime,
    };

    this.orderService.scheduleOrder(order, this.scheduledTime).subscribe({
      next: () => {
        this.successMessage = 'Porudžbina uspešno zakazana!';
        setTimeout(() => this.router.navigate(['/orders']), 2000);
      },
      error: (error) => {
        this.errorMessage = 'Greška pri zakazivanju porudžbine. Pokušajte ponovo.';
        console.error('Error:', error);
      },
    });
  }

  toggleDishSelection(dish: any): void {
    const index = this.selectedDishes.findIndex((d) => d.id === dish.id);
    if (index === -1) {
      this.selectedDishes.push(dish);
    } else {
      this.selectedDishes.splice(index, 1);
    }
  }

  isDishSelected(dish: any): boolean {
    return this.selectedDishes.some((d) => d.id === dish.id);
  }
}
